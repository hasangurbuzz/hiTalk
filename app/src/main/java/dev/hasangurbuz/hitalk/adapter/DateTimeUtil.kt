package dev.hasangurbuz.hitalk.adapter

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class DateTimeUtil {
    companion object {
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
}