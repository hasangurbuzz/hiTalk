package dev.hasangurbuz.hitalk.data.repository

import android.net.Uri
import dev.hasangurbuz.hitalk.data.remote.ImageApi
import dev.hasangurbuz.hitalk.data.remote.UserApi
import dev.hasangurbuz.hitalk.data.remote.model.Response
import dev.hasangurbuz.hitalk.data.remote.model.UserDto
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.model.User
import dev.hasangurbuz.hitalk.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl
@Inject constructor(private val userApi: UserApi, private val imageApi: ImageApi) : UserRepository {

    override suspend fun createUser(user: User): Resource<User> {
        val imageUri: Uri
        val uploadResponse = imageApi.upload(
            collectionName = user.id,
            imageUri = user.imageUri,
            fileName = "profile"
        )

        when (uploadResponse) {
            is Response.Failed -> {
                return Resource.Failed
            }

            is Response.Success -> {
                imageUri = uploadResponse.data
            }
        }

        val userDto = UserDto(
            id = user.id,
            name = user.name,
            imageUri = imageUri.toString(),
            phoneNumber = user.phoneNumber
        )

        val createResponse = userApi.create(userDto)

        when (createResponse) {
            is Response.Failed -> return Resource.Failed
            is Response.Success -> {
                createResponse.data.let {
                    val user = User(
                        id = it.id!!,
                        name = it.name!!,
                        phoneNumber = it.phoneNumber!!,
                        imageUri = Uri.parse(it.imageUri)
                    )
                    return Resource.Success(user)
                }
            }
        }
    }

    override suspend fun findById(userId: String): Resource<User> {
        val result = userApi.findById(userId)
        val user: User

        when (result) {
            is Response.Failed -> return Resource.Failed
            is Response.Success -> {
                result.data.apply {
                    user = User(
                        id = this.id!!,
                        imageUri = Uri.parse(this.imageUri),
                        phoneNumber = this.phoneNumber!!,
                        name = this.name!!
                    )
                }
            }
        }

        return Resource.Success(user)
    }

    override suspend fun findById(userIdList: List<String>): Resource<List<User>> {
        val result = userApi.findById(userIdList)
        if (result is Response.Failed) {
            return Resource.Failed
        }
        val userDtos = (result as Response.Success).data

        val users = userDtos.map { dto ->
            User(
                id = dto.id!!,
                name = dto.name!!,
                imageUri = Uri.parse(dto.imageUri),
                phoneNumber = dto.phoneNumber!!
            )
        }
        return Resource.Success(users)
    }


    override suspend fun findByPhoneNumber(phoneNumberList: List<String>): Resource<List<User>> {
        val result = userApi.findByPhone(phoneNumberList)
        if (result is Response.Failed) {
            return Resource.Failed
        }
        val userDtos = (result as Response.Success).data

        val users = userDtos.map { userDto ->
            User(
                id = userDto.id!!,
                name = userDto.name!!,
                imageUri = Uri.parse(userDto.imageUri),
                phoneNumber = userDto.phoneNumber!!
            )
        }

        return Resource.Success(users)
    }

    override suspend fun findByPhoneNumber(phoneNumber: String): Resource<User> {
        val result = userApi.findByPhone(phoneNumber)
        if (result is Response.Failed) {
            return Resource.Failed
        }
        val userDto = (result as Response.Success).data

        val user = userDto.toUser()

        return Resource.Success(user)
    }


}