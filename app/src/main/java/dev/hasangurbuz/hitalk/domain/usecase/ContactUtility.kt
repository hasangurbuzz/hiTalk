package dev.hasangurbuz.hitalk.domain.usecase

import dev.hasangurbuz.hitalk.data.local.entity.Contact

object ContactUtility {
    fun normalize(contacts: List<Contact>): List<Contact> {
        return contacts.map {
            if (it.phoneNumber.startsWith("0")) {
                it.phoneNumber = "+9${it.phoneNumber}"
            }

            it.phoneNumber = it.phoneNumber.filter { !it.isWhitespace() }

            it
        }
    }
}