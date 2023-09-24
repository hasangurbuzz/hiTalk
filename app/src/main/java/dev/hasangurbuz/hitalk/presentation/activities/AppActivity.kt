package dev.hasangurbuz.hitalk.presentation.activities

import android.database.Cursor
import android.provider.ContactsContract
import android.view.LayoutInflater
import androidx.core.database.getStringOrNull
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.data.local.entity.Contact
import dev.hasangurbuz.hitalk.databinding.ActivityAppBinding
import dev.hasangurbuz.hitalk.presentation.base.activities.BindingActivity

@AndroidEntryPoint
class AppActivity : BindingActivity<ActivityAppBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = ActivityAppBinding::inflate

    fun readContacts(): MutableList<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = this.contentResolver
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