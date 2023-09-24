package dev.hasangurbuz.hitalk.data.repository

import dev.hasangurbuz.hitalk.data.local.ContactDataSource
import dev.hasangurbuz.hitalk.data.local.entity.Contact
import dev.hasangurbuz.hitalk.domain.repository.ContactRepository
import javax.inject.Inject


class ContactRepositoryImpl @Inject constructor(
    private val contactDataSource: ContactDataSource
) : ContactRepository {

    override fun fetch(): List<Contact> {
        return contactDataSource.fetch()
    }
}