package dev.hasangurbuz.hitalk.data.model

sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data object Failed : Resource<Nothing>()
}

