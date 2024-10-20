package com.amontdevs.saturnwallpapers.source

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

interface ITimeProvider {
    val timeZone: TimeZone
    fun getCurrentTime(): Instant
}

class TimeProvider(
    timezone: String
) : ITimeProvider {
    override val timeZone = TimeZone.of(timezone)
    private val currentLocalDate: LocalDateTime
        get() = Clock.System.now().toLocalDateTime(timeZone)

    override fun getCurrentTime() = currentLocalDate.date.atStartOfDayIn(timeZone)

}