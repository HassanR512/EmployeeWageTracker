package com.wagetracker.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val monthLabelFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())

    fun formatDate(millis: Long): String = displayFormat.format(millis)

    fun startOfDay(millis: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun monthKeyOf(millis: Long): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = millis
        return "${cal.get(Calendar.YEAR)}-${(cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')}"
    }

    fun currentMonthKey(): String = monthKeyOf(System.currentTimeMillis())

    fun monthKeyToLabel(monthKey: String): String {
        if (monthKey == "ALL") return "All time"
        val parts = monthKey.split("-")
        val cal = Calendar.getInstance()
        cal.set(parts[0].toInt(), parts[1].toInt() - 1, 1)
        return monthLabelFormat.format(cal.time)
    }
}
