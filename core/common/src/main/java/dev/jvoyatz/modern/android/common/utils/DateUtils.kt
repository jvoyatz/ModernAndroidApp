@file:Suppress("unused")
package dev.jvoyatz.modern.android.common.utils


import dev.jvoyatz.modern.android.common.DATE_dd_MM_yyyy
import dev.jvoyatz.modern.android.common.DATE_dd_MMMM_yyyy
import dev.jvoyatz.modern.android.common.DATE_MMMM_yyyy
import dev.jvoyatz.modern.android.common.DATE_ISO_ZONE_FORMAT
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Formats the given argument to a [Date] instance
 *
 * @param dateTime date in String format
 * @return Date instance
 *
 * @see [DateTimeFormatter.ISO_ZONED_DATE_TIME]
 */
fun formatToDate(dateTime: String): Date? = try {
    Date.from(ZonedDateTime.parse(dateTime).toInstant())
} catch (e: Exception) {
    e.printStackTrace()
    null
}


/**
 * Format the given date to something like
 * March 2024
 *
 * @param dateTime a string representation of the date
 * @return the date in this format [DATE_MMMM_yyyy]
 */
fun toMMMMyyyy(dateTime: String): String {
    return try{
        ZonedDateTime.parse(dateTime).format(
            DateTimeFormatter.ofPattern(DATE_MMMM_yyyy)
        )
        throw Exception()
    }catch (e: Exception){
        toDateIsoZoneFormat(DATE_dd_MM_yyyy, dateTime)
    }
}

/**
 * Formats the given date to something like
 * 01 January 2024
 *
 * @param dateTime the date in string
 * @return the formatted date
 */
fun todDayDigitAndFullMonthAndYearDate(dateTime: String): String = try {
    ZonedDateTime.parse(dateTime).format(
        DateTimeFormatter.ofPattern(DATE_dd_MMMM_yyyy)
    )
} catch (e: Exception) {
    e.printStackTrace()
    toDateIsoZoneFormat(DATE_dd_MMMM_yyyy, dateTime)
}


/**
 * Formats the date to the provided [formatString], otherwise
 * it uses [DATE_ISO_ZONE_FORMAT]
 *
 * @param formatString the expected format for this date
 * @param dateTime the datetime in string format
 * @return a String instance with the formatted date
 */
private fun toDateIsoZoneFormat(formatString: String, dateTime: String): String = try {
    val date = with(SimpleDateFormat(DATE_ISO_ZONE_FORMAT, Locale.getDefault())) {
        timeZone = TimeZone.getTimeZone("UTC")
        parse(dateTime)
    }
    with(SimpleDateFormat(formatString, Locale.getDefault())) {
        format(date)
    }
} catch (e: Exception) {
    e.printStackTrace()
    dateTime
}
