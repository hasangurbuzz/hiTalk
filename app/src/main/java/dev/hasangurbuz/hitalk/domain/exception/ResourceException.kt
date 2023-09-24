package dev.hasangurbuz.hitalk.domain.exception

data class ResourceException(override val message: String) : RuntimeException() {
}