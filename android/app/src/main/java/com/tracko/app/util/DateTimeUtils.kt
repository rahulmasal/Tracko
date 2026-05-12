package com.tracko.app.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object DateTimeUtils {

    private val apiFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val timeFormat = SimpleDateFormat("hh:mm a", Locale.US)
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
    private val monthFormat = SimpleDateFormat("yyyy-MM", Locale.US)
    private val displayDateFormat = SimpleDateFormat("dd MMM yyyy", Locale.US)
    private val dayFormat = SimpleDateFormat("EEEE", Locale.US)

    fun todayDate(): String = dateFormat.format(Date())

    fun nowTimestamp(): String = apiFormat.format(Date())

    fun nowMillis(): Long = System.currentTimeMillis()

    fun formatDate(dateString: String): String {
        return try {
            val date = apiFormat.parse(dateString) ?: return dateString
            displayDateFormat.format(date)
        } catch (_: Exception) {
            try {
                val date = dateFormat.parse(dateString) ?: return dateString
                displayDateFormat.format(date)
            } catch (_: Exception) {
                dateString
            }
        }
    }

    fun formatTime(dateString: String): String {
        return try {
            val date = apiFormat.parse(dateString) ?: return ""
            timeFormat.format(date)
        } catch (_: Exception) {
            ""
        }
    }

    fun formatDateTime(dateString: String): String {
        return try {
            val date = apiFormat.parse(dateString) ?: return dateString
            dateTimeFormat.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatDate(date: Date): String = displayDateFormat.format(date)

    fun formatTime(date: Date): String = timeFormat.format(date)

    fun formatDateTime(date: Date): String = dateTimeFormat.format(date)

    fun formatDateForApi(date: Date): String = dateFormat.format(date)

    fun formatDateTimeForApi(date: Date): String = apiFormat.format(date)

    fun currentMonthPattern(): String = monthFormat.format(Date())

    fun getMonthPattern(year: Int, month: Int): String {
        return String.format(Locale.US, "%04d-%02d", year, month)
    }

    fun getCurrentMonthDates(): Pair<String, String> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        val start = dateFormat.format(cal.time)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        val end = dateFormat.format(cal.time)
        return Pair(start, end)
    }

    fun getWeekDates(): Pair<String, String> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val start = dateFormat.format(cal.time)
        cal.add(Calendar.DAY_OF_WEEK, 6)
        val end = dateFormat.format(cal.time)
        return Pair(start, end)
    }

    fun getDayOfWeek(dateString: String): String {
        return try {
            val date = dateFormat.parse(dateString) ?: return ""
            dayFormat.format(date)
        } catch (_: Exception) {
            ""
        }
    }

    fun diffDays(startDate: String, endDate: String): Long {
        return try {
            val start = dateFormat.parse(startDate) ?: return 0
            val end = dateFormat.parse(endDate) ?: return 0
            val diff = end.time - start.time
            diff / (1000 * 60 * 60 * 24) + 1
        } catch (_: Exception) {
            0
        }
    }

    fun formatDuration(minutes: Int): String {
        val hours = minutes / 60
        val mins = minutes % 60
        return when {
            hours > 0 -> "${hours}h ${mins}m"
            else -> "${mins}m"
        }
    }

    fun formatTimestamp(timestamp: Long): String {
        return dateTimeFormat.format(Date(timestamp))
    }

    fun isToday(dateString: String): Boolean {
        return dateString == todayDate()
    }

    fun convertToServerTimezone(dateString: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        sdf.timeZone = TimeZone.getDefault()
        return try {
            val date = sdf.parse(dateString) ?: return dateString
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.format(date)
        } catch (_: Exception) {
            dateString
        }
    }

    fun getTimeAgo(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "${days}d ago"
            hours > 0 -> "${hours}h ago"
            minutes > 0 -> "${minutes}m ago"
            seconds > 0 -> "${seconds}s ago"
            else -> "just now"
        }
    }
}
