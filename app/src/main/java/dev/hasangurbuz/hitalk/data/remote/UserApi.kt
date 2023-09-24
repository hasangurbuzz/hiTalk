package dev.hasangurbuz.hitalk.data.remote

import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.data.remote.model.UserDto

interface UserApi {

    suspend fun create(user: UserDto): Response<UserDto>

    suspend fun findByPhone(phoneNumber: String): Response<UserDto>

    suspend fun findById(userId: String): Response<UserDto>

    suspend fun findById(userIdList: List<String>): Response<List<UserDto>>

    suspend fun findByPhone(phoneNumberList: List<String>): Response<List<UserDto>>
}