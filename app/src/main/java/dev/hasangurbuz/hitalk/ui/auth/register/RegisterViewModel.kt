package dev.hasangurbuz.hitalk.ui.auth.register


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.hasangurbuz.hitalk.data.model.Resource
import dev.hasangurbuz.hitalk.remote.firebase.UserService
import dev.hasangurbuz.hitalk.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel
@Inject constructor(
    private val userService: UserService
) : ViewModel() {

    private val _registerState = MutableSharedFlow<RegisterEvent>()
    val registerState = _registerState.asSharedFlow()

    private val _imageUri = MutableStateFlow<String?>(null)
    val imageUri = _imageUri.asStateFlow()

    fun setImageUri(uri: String?) {
        viewModelScope.launch {
            _imageUri.emit(uri)
        }
    }

    fun register(user: User) {
        viewModelScope.launch {
            val result = userService.findByPhoneNumber(user.phoneNumber!!)
            when (result) {
                is Resource.Success -> {
                    Log.e("Register", "User exists")
                    _registerState.emit(RegisterEvent.Failed("User exists with this number"))
                }

                is Resource.Failed -> {
                    Log.e("Register", "User not found")
                    _registerState.emit(RegisterEvent.Next)
                }
            }
        }
    }

    private fun beginRegistration(user: User) {

    }

    sealed class RegisterEvent {
        data object Next : RegisterEvent()
        data class Failed(val message: String) : RegisterEvent()
    }
}