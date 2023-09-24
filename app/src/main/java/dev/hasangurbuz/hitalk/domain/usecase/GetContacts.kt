package dev.hasangurbuz.hitalk.domain.usecase

import dev.hasangurbuz.hitalk.data.local.entity.Contact
import dev.hasangurbuz.hitalk.domain.model.Resource
import dev.hasangurbuz.hitalk.domain.model.User
import dev.hasangurbuz.hitalk.domain.repository.ContactRepository
import dev.hasangurbuz.hitalk.domain.repository.UserRepository
import javax.inject.Inject

class GetContacts
@Inject constructor(
    private val userRepository: UserRepository,
    private val contactRepository: ContactRepository
) {

    suspend operator fun invoke(): List<User> {
        val localContacts = ContactUtility.normalize(contactRepository.fetch())

        val phoneNumbers = localContacts.map { contact: Contact ->
            contact.phoneNumber
        }

        val result = userRepository.findByPhoneNumber(phoneNumbers)

        if (result is Resource.Failed) {
            return emptyList()
        }

        val users = (result as Resource.Success).data

        val contacts = mutableListOf<User>()

        for (user in users) {
            for (localContact in localContacts) {
                if (user.phoneNumber == localContact.phoneNumber) {
                    val contact = User(
                        id = user.id,
                        name = localContact.name,
                        phoneNumber = user.phoneNumber,
                        imageUri = user.imageUri
                    )
                    contacts.add(contact)
                }
            }
        }


        return contacts
    }
}