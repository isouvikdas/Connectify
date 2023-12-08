package com.example.connectify

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Utils {
    fun getTimeAgo(timestamp: Long): String {
        val currentTime = System.currentTimeMillis()
        val timeDifference = currentTime - timestamp

        return when {
            timeDifference < DateUtils.MINUTE_IN_MILLIS -> "just now"
            timeDifference < 2 * DateUtils.MINUTE_IN_MILLIS -> "a minute ago"
            timeDifference < DateUtils.HOUR_IN_MILLIS -> "${timeDifference / DateUtils.MINUTE_IN_MILLIS} minutes ago"
            timeDifference < 2 * DateUtils.HOUR_IN_MILLIS -> "an hour ago"
            timeDifference < DateUtils.DAY_IN_MILLIS -> "${timeDifference / DateUtils.HOUR_IN_MILLIS} hours ago"
            timeDifference < 2 * DateUtils.DAY_IN_MILLIS -> "yesterday"
            else -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }
}