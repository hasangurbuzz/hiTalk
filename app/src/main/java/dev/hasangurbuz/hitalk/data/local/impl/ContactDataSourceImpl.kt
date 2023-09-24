package dev.hasangurbuz.hitalk.data.local.impl

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull
import dev.hasangurbuz.hitalk.data.local.ContactDataSource
import dev.hasangurbuz.hitalk.data.local.entity.Contact
import javax.inject.Inject

class ContactDataSourceImpl @Inject constructor(
    private val contentResolver: ContentResolver
) : ContactDataSource {
    override fun fetch(): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val name = fetchColumn(cursor, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val id = fetchColumn(cursor, ContactsContract.CommonDataKinds.Phone._ID)
                val phone = fetchColumn(cursor, ContactsContract.CommonDataKinds.Phone.NUMBER)

                if (name != null && id != null && phone != null) {
                    val contact = Contact(
                        id = id,
                        phoneNumber = phone,
                        name = name
                    )
                    contacts.add(contact)
                }
            }
            cursor.close()
        }
        return contacts
    }

    private fun fetchColumn(cursor: Cursor, column: String): String? {
        return cursor.getStringOrNull(cursor.getColumnIndex(column))
    }
}