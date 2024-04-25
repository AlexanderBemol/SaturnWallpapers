package com.amontdevs.saturnwallpapers.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toJavaInstant
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

actual fun Instant.formatDate(pattern: String): String {
    return try {
        val zonedDateTime = ZonedDateTime
            .ofInstant(this.toJavaInstant(), ZoneId.of("UTC-05:00")).toLocalDateTime()
        zonedDateTime.format(DateTimeFormatter.ofPattern(pattern))
    } catch (e: Exception) {
        "Unexpected Error"
    }
}

actual fun String.toInstant(pattern: String): Instant {
    return try {
        LocalDate.parse(this)
            .atStartOfDayIn(TimeZone.of("UTC-05:00"))
    } catch (e: Exception){
        Instant.fromEpochMilliseconds(0)
    }
}