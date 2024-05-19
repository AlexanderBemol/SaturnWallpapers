package mock.source

import com.amontdevs.saturnwallpapers.source.ITimeProvider
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime

class MockTimeProvider: ITimeProvider {
    private val timezone = "UTC-05:00"
    private val timeZone = TimeZone.of(timezone)
    private var currentTime = Clock.System.now().toLocalDateTime(timeZone)

    override fun getCurrentTime() = currentTime.date.atStartOfDayIn(timeZone)

    fun setNewTime(newTime: Instant) {
        currentTime = newTime.toLocalDateTime(timeZone)
    }
}