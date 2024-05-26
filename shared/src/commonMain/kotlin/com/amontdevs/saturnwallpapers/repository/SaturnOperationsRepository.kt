package com.amontdevs.saturnwallpapers.repository

import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.source.ISaturnPhotoDao
import com.amontdevs.saturnwallpapers.source.ITimeProvider
import com.amontdevs.saturnwallpapers.source.SettingsSource
import com.amontdevs.saturnwallpapers.source.TimeProvider

interface ISaturnOperationsRepository {
    suspend fun saveOperation(): SaturnResult<Unit>
}
class SaturnOperationsRepository(
    private val timeProvider: ITimeProvider,
    private val settingsSource: SettingsSource,
    private val saturnPhotosDao: ISaturnPhotoDao
) : ISaturnOperationsRepository{
    override suspend fun saveOperation(): SaturnResult<Unit> {
        TODO("Not yet implemented")
    }
}