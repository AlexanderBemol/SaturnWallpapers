package mock.source

import com.amontdevs.saturnwallpapers.model.SaturnSettings
import com.amontdevs.saturnwallpapers.source.ISettingsSource

class MockSaturnSettings: ISettingsSource {
    private var isPopulated = false
    private var settings = SaturnSettings()

    override fun isAlreadyPopulated() = isPopulated

    override fun setAlreadyPopulated() {
        isPopulated = true
    }

    override fun getSettings(): SaturnSettings = settings

    override fun saveSettings(saturnSettings: SaturnSettings) {
        settings = saturnSettings
    }
}