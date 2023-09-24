package dev.hasangurbuz.hitalk.core

import dev.hasangurbuz.hitalk.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthContext @Inject constructor() {
    var currentUser: User? = null
}