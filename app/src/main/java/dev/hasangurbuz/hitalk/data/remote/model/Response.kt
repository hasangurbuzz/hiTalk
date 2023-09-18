package dev.hasangurbuz.hitalk.data.remote.model

sealed class Response<out T> {
    data class Success<out T>(val data: T) : Response<T>()
    data object Failed : Response<Nothing>()
}