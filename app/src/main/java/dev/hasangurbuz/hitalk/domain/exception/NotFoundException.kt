package dev.hasangurbuz.hitalk.domain.exception

data class NotFoundException(override val message: String) : RuntimeException() {
}