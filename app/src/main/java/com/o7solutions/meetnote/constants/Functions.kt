package com.o7solutions.meetnote.constants

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Functions {

    fun formatEpochMillisToDateTimeString(
        epochMillis: Long,
        pattern: String = "MMM dd, yyyy HH:mm",
        locale: Locale = Locale.getDefault()
    ): String {
        val date = Date(epochMillis) // Convert milliseconds to a Date object
        val formatter = SimpleDateFormat(pattern, locale) // Create a formatter with the desired pattern and locale
        return formatter.format(date) // Format the Date object into a String
    }

    fun formatTime(ms: Int): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

}