package dev.hasangurbuz.hitalk.data.local

import dev.hasangurbuz.hitalk.data.local.entity.Contact

interface ContactDataSource {
    fun fetch(): List<Contact>
}