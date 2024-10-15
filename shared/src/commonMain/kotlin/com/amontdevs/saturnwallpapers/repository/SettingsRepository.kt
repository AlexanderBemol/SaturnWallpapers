package com.amontdevs.saturnwallpapers.repository

import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.SaturnSettings
import com.amontdevs.saturnwallpapers.model.UserStatus
import com.amontdevs.saturnwallpapers.source.ISettingsSource

interface ISettingsRepository {
    fun getSettings(): SaturnResult<SaturnSettings>
    fun saveSettings(settings: SaturnSettings): SaturnResult<Unit>
    fun getUserStatus(): SaturnResult<UserStatus>
    fun setUserStatus(status: UserStatus): SaturnResult<Unit>
}

class SettingsRepository(
    private val saturnSettings: ISettingsSource
) : ISettingsRepository {
    override fun getSettings(): SaturnResult<SaturnSettings> {
        return try {
            SaturnResult.Success(saturnSettings.getSettings())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override fun saveSettings(settings: SaturnSettings): SaturnResult<Unit> {
        return try {
            SaturnResult.Success(saturnSettings.saveSettings(settings))
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override fun getUserStatus(): SaturnResult<UserStatus> {
        return try {
            SaturnResult.Success(saturnSettings.getUserStatus())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override fun setUserStatus(status: UserStatus): SaturnResult<Unit> {
        return try {
            SaturnResult.Success(saturnSettings.setUserStatus(status))
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

}