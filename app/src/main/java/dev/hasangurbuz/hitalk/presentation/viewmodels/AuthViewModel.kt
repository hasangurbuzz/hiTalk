package dev.hasangurbuz.hitalk.presentation.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthMissingActivityForRecaptchaException
import com.google.firebase.auth.FirebaseAuthSettings
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.core.AuthContext
import dev.hasangurbuz.hitalk.data.repository.UserRepositoryImpl
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.model.User
import dev.hasangurbuz.hitalk.presentation.events.LoginEvent
import dev.hasangurbuz.hitalk.presentation.events.RegisterEvent
import dev.hasangurbuz.hitalk.presentation.viewstates.LoginInputState
import dev.hasangurbuz.hitalk.presentation.viewstates.RegisterInputState
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
    private val userRepository: UserRepositoryImpl,
    private val authContext: AuthContext
) : ViewModel() {


    private var verificationId: String? = null

    private var tempUser: User? = null

    private val _registerInputState = MutableStateFlow(RegisterInputState())
    val registerInputState = _registerInputState.asStateFlow()

    private val _loginInputState = MutableStateFlow(LoginInputState())
    val loginInputState = _loginInputState.asStateFlow()


    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    // need to access activity to start firebase captcha verification
    // must be removed when firebase fixes this issue
    private var activity: Activity? = null

    private var newUser: Boolean = false

    // phone authentication logic violates CleanArch because firebase auth still does not support SOC

    fun setActivity(activity: Activity) {
        this.activity = activity
    }

    fun onRegisterEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.RegisterClick -> {
                viewModelScope.launch {
                    sendUIEvent(UIEvent.Loading)
                    val searchResult =
                        userRepository.findByPhoneNumber(registerInputState.value.phoneNumber)
                    when (searchResult) {
                        is Resource.Success -> _eventFlow.emit(UIEvent.SnackBar("User exists"))
                        is Resource.Failed -> {
                            newUser = true
                            phoneAuth(_registerInputState.value.phoneNumber)
                        }
                    }
                }
            }

            is RegisterEvent.PhoneNumberChanged -> {
                _registerInputState.value = registerInputState.value.copy(
                    phoneNumber = event.phoneNumber
                )
            }

            is RegisterEvent.UsernameChanged -> {
                _registerInputState.value = registerInputState.value.copy(
                    username = event.username
                )

            }

            is RegisterEvent.ImageChanged -> {
                _registerInputState.value = registerInputState.value.copy(
                    imageUri = event.imageUri
                )
            }
        }
    }

    fun onLoginEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.PhoneNumberChanged -> {
                _loginInputState.value = loginInputState.value.copy(
                    phoneNumber = event.phoneNumber
                )
            }

            is LoginEvent.LoginClick -> {
                newUser = false
                viewModelScope.launch {
                    sendUIEvent(UIEvent.Loading)
                    val searchResult =
                        userRepository.findByPhoneNumber(loginInputState.value.phoneNumber)
                    when (searchResult) {
                        is Resource.Failed -> {
                            sendUIEvent(UIEvent.SnackBar("User not found"))
                        }

                        is Resource.Success -> {
                            phoneAuth(searchResult.data.phoneNumber)
                        }
                    }
                }
            }
        }
    }

    suspend fun getLoggedUser(): User? {
        var user: User? = null
        if (firebaseAuth.currentUser == null) {
            return null
        }
        val result = userRepository.findById(firebaseAuth.currentUser!!.uid)
        if (result is Resource.Success) {
            user = result.data
        }
        return user
    }

    private fun phoneAuth(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder()
            .setPhoneNumber(phoneNumber)
            .setTimeout(90L, TimeUnit.SECONDS)
            .setActivity(activity!!)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    login(credential)
                }

                override fun onVerificationFailed(exception: FirebaseException) {
                    when (exception) {
                        is FirebaseTooManyRequestsException -> {
                            sendUIEvent(UIEvent.Failed("You have tried too many times"))
                        }

                        is FirebaseAuthMissingActivityForRecaptchaException -> {
                            sendUIEvent(UIEvent.Failed("Captcha error"))
                        }

                        else -> {
                            sendUIEvent(UIEvent.Failed("Auth error"))
                        }
                    }
                }

                override fun onCodeSent(
                    credential: String,
                    resendToken: PhoneAuthProvider.ForceResendingToken
                ) {
                    verificationId = credential
                    sendUIEvent(UIEvent.NavigateSMS)
                }

                override fun onCodeAutoRetrievalTimeOut(credential: String) {
                    verificationId = credential
                    sendUIEvent(UIEvent.Failed("Timeout error"))
                }
            }).build()


        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun login(authCredential: AuthCredential) {
        viewModelScope.launch {
            val authResult = firebaseAuth.signInWithCredential(authCredential).await()
            if (authResult.user == null) {
                sendUIEvent(UIEvent.Failed("Failed to authenticate"))
                return@launch
            }

            var user: User? = null
            if (newUser) {
                user = User(
                    id = authResult.user!!.uid,
                    name = _registerInputState.value.username,
                    phoneNumber = _registerInputState.value.phoneNumber,
                    imageUri = _registerInputState.value.imageUri
                )
                val createResult = userRepository.createUser(user)
                when (createResult) {
                    is Resource.Failed -> {
                        sendUIEvent(UIEvent.Failed("User could not created"))
                    }

                    is Resource.Success -> {
                        user = createResult.data
                    }
                }
            } else {
                val searchResult = userRepository.findById(authResult.user!!.uid)
                when (searchResult) {
                    is Resource.Failed -> {
                        sendUIEvent(UIEvent.Failed("Failed to login"))
                        return@launch
                    }

                    is Resource.Success -> {
                        user = searchResult.data
                    }
                }
            }

            authContext.currentUser = user
            sendUIEvent(UIEvent.Success)
        }

    }


    fun verifySmsCode(smsCode: String) {
        viewModelScope.launch {
            verificationId?.let {
                sendUIEvent(UIEvent.Loading)
                val credential = PhoneAuthProvider.getCredential(verificationId!!, smsCode)
                login(credential)
            }
        }
    }

    private fun sendUIEvent(uiEvent: UIEvent) {
        viewModelScope.launch {
            _eventFlow.emit(uiEvent)
        }
    }


    sealed class UIEvent {
        data class SnackBar(val message: String) : UIEvent()
        data object NavigateSMS : UIEvent()

        data object Success : UIEvent()

        data class Failed(val message: String) : UIEvent()

        data object Loading : UIEvent()
    }
}
