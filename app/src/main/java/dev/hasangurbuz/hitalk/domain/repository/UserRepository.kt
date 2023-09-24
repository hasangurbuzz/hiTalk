package dev.hasangurbuz.hitalk.domain.repository

import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.model.User

interface UserRepository {

    suspend fun createUser(user: User): Resource<User>

    suspend fun findById(userId: String): Resource<User>

    suspend fun findById(userIdList: List<String>): Resource<List<User>>

    suspend fun findByPhoneNumber(phoneNumberList: List<String>): Resource<List<User>>

    suspend fun findByPhoneNumber(phoneNumber: String): Resource<User>

}