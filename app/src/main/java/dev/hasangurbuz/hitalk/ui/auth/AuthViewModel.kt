package dev.hasangurbuz.hitalk.ui.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.remote.firebase.UserService
import dev.hasangurbuz.hitalk.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthViewModel
@Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userService: UserService
) : ViewModel() {

    private val _authState = MutableSharedFlow<AuthEvent>()
    val authState = _authState.asSharedFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()

    private val _authCredential = MutableStateFlow<PhoneAuthCredential?>(null)
    val authCredential = _authCredential.asStateFlow()

    private var verificationId: String? = null

    private var tempUser: User? = null

    init {
        firebaseAuth.currentUser?.let {
            viewModelScope.launch {
                val result = userService.findById(it.uid)
                if (result is Resource.Success) {
                    val user = result.data
                    _currentUser.emit(user)
                }
            }
        }
    }

    suspend fun getLoggedUser(): User? {
        var user: User? = null
        if (firebaseAuth.currentUser == null) {
            return null
        }
        val result = userService.findById(firebaseAuth.currentUser!!.uid)
        if (result is Resource.Success) {
            user = result.data
        }
        return user
    }

    private fun beginPhoneAuth(user: User, activity: Activity) {
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(user.phoneNumber!!)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    signin(credential)
                    Log.e("AUTH", "auth success")
                    emitAuthEvent(AuthEvent.Success)
                    emitCurrentUser(user)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    when (exception) {
                        is FirebaseTooManyRequestsException -> {
                            Log.e("AUTH", "sms error")
                            emitAuthEvent(AuthEvent.Failed("Please try later"))
                        }

                        is FirebaseAuthMissingActivityForRecaptchaException -> {
                            Log.e("AUTH", "captcha error")
                            emitAuthEvent(AuthEvent.Failed("Failed Recaptcha"))
                        }

                        else -> {
                            Log.e("AUTH", exception.message.toString())
                            emitAuthEvent(AuthEvent.Failed("Auth error"))
                        }
                    }
                }

                override fun onCodeSent(
                    credential: String,
                    resendToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = credential
                    emitAuthEvent(AuthEvent.Next)
                    Log.e("AUTH", "code sent")
                }

                override fun onCodeAutoRetrievalTimeOut(credential: String) {
                    verificationId = credential
                    Log.e("AUTH", "timeout")
                    emitAuthEvent(AuthEvent.Failed("Timeout error"))
                }
            }).build()


        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun startAuthentication(phoneNumber: String, activity: Activity) {
        viewModelScope.launch {
            val userResult = userService.findByPhoneNumber(phoneNumber)
            when (userResult) {
                is Resource.Success -> {
                    val user = userResult.data
                    Log.e("AUTH", "user found starting auth")
                    _currentUser.emit(user)
                    beginPhoneAuth(user, activity)
                }

                is Resource.Failed -> {
                    Log.e("AUTH", "not found user on db")
                    _authState.emit(AuthEvent.Failed("Not found"))
                }
            }

        }
    }


    fun verifySmsCode(smsCode: String) {
        viewModelScope.launch {
            verificationId?.let {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
                _authCredential.emit(credential)
                _authState.emit(AuthEvent.Next)
            }
        }
    }

    fun register(user: User, activity: Activity) {
        viewModelScope.launch {
            val result = userService.findByPhoneNumber(user.phoneNumber!!)
            when (result) {
                is Resource.Success -> {
                    Log.e("Register", "User exists")
                    _authState.emit(AuthEvent.Failed("User exists"))
//                    _registerState.emit(RegisterViewModel.RegisterEvent.Failed("User exists with this number"))
                }

                is Resource.Failed -> {
                    Log.e("Register", "User not found")
                    tempUser = user
                    beginPhoneAuth(user, activity)
//                    _registerState.emit(RegisterViewModel.RegisterEvent.Next)
                }
            }
        }
    }

    fun signin(authCredential: PhoneAuthCredential) {
        viewModelScope.launch {
            val result = firebaseAuth.signInWithCredential(authCredential).await()
            if (result.user == null) {
                _authState.emit(AuthEvent.Failed("User not found"))
                return@launch
            }

            tempUser?.let {
                tempUser!!.id = result.user!!.uid
                val userResult = userService.create(tempUser!!)
                when (userResult) {
                    is Resource.Success -> {
                        _currentUser.emit(userResult.data)
                    }

                    is Resource.Failed -> {
                        _authState.emit(AuthEvent.Failed("User registration failed"))
                    }
                }
            }
            _authState.emit(AuthEvent.Success)
        }
    }


    private fun emitAuthEvent(event: AuthEvent) {
        viewModelScope.launch {
            _authState.emit(event)
        }
    }

    private fun emitCurrentUser(user: User) {
        viewModelScope.launch {
            _currentUser.emit(user)
        }
    }


    sealed class AuthEvent {
        data object Next : AuthEvent()
        data class Failed(val message: String) : AuthEvent()
        data object Success : AuthEvent()
    }
}
