package dev.hasangurbuz.hitalk.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SmsViewModel : ViewModel() {

    private val _countdownState = MutableStateFlow(0)
    val countdownState = _countdownState.asStateFlow()


    init {
        viewModelScope.launch {
            for (i in 60 downTo 0) {
                _countdownState.emit(i)
                delay(1000L)
            }
        }
    }


}