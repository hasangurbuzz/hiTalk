package dev.hasangurbuz.hitalk.presentation.util

import android.text.Editable
import android.text.TextWatcher
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


object UIUtils {
    fun inputListener(onChange: (value: String) -> Unit): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                onChange(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
    }

    fun dateToHour(date: String): String {
        val dateTime = LocalDateTime.parse(date)
        val zoned = dateTime.atZone(ZoneId.systemDefault())
        val time = zoned.format(DateTimeFormatter.ofPattern("HH:mm"))
        return time
    }

    fun toString(dateTime: LocalDateTime): String {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
    }
}