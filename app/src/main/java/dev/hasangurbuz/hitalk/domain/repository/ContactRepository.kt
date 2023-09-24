package dev.hasangurbuz.hitalk.domain.repository

import dev.hasangurbuz.hitalk.data.local.entity.Contact


interface ContactRepository {

    fun fetch(): List<Contact>
}