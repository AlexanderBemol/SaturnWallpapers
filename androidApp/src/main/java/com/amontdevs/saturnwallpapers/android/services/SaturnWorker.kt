package com.amontdevs.saturnwallpapers.android.services

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amontdevs.saturnwallpapers.android.system.IAndroidWallpaperSetter
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.SaturnSettings
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.source.ITimeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SaturnWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {
    private val timeProvider: ITimeProvider by inject()
    private val saturnPhotosRepository: ISaturnPhotosRepository by inject()
    private val settingsRepository: ISettingsRepository by inject()
    private val androidWallpaperSetter: IAndroidWallpaperSetter by inject()
    private val fileManager: IFileManager by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            refreshPhotos()
            val settings = getSettings()
            val todaySaturnPhoto = getTodayWallpaper()
            val saturnPhotoToSet = if (todaySaturnPhoto.mediaType == "image") todaySaturnPhoto
                else getDefaultPhoto(settings.defaultSaturnPhoto)

            val filename = if (settings.mediaQuality == MediaQuality.HIGH) saturnPhotoToSet.highDefinitionPath
                else saturnPhotoToSet.regularPath

            val photoByteArray = getPhotoByteArray(filename)
            androidWallpaperSetter.setWallpaper(settings.wallpaperScreen, photoByteArray)
            Result.success()
        } catch (e: Exception) {
            Log.e("SaturnWorker", e.message, e)
            Result.retry()
        }
    }

    private suspend fun refreshPhotos() {
        when(val refreshResult = saturnPhotosRepository.refresh()) {
            is SaturnResult.Error -> {
                throw refreshResult.e
            }
            is SaturnResult.Success -> Log.d("SaturnWorker", "Data refreshed")
        }
    }

    private fun getSettings(): SaturnSettings {
        return when(val settings = settingsRepository.getSettings()) {
            is SaturnResult.Error -> {
                throw settings.e
            }
            is SaturnResult.Success -> {
                settings.data
            }
        }
    }

    private suspend fun  getTodayWallpaper(): SaturnPhoto {
        return when(val saturnPhotoResult = saturnPhotosRepository.getSaturnPhoto(timeProvider.getCurrentTime())) {
            is SaturnResult.Error -> {
                throw saturnPhotoResult.e
            }
            is SaturnResult.Success -> {
                saturnPhotoResult.data
            }
        }
    }

    private fun getPhotoByteArray(filename: String): ByteArray {
        return when(val byteArray = fileManager.getPicture(filename)) {
            is SaturnResult.Error -> {
                throw byteArray.e
            }
            is SaturnResult.Success -> {
                byteArray.data
            }
        }
    }

    private suspend fun getDefaultPhoto(defaultPhoto: DefaultSaturnPhoto): SaturnPhoto {
        return when(val saturnPhotoResult = saturnPhotosRepository.getAllSaturnPhotos()) {
            is SaturnResult.Error -> {
                throw saturnPhotoResult.e
            }
            is SaturnResult.Success -> {
                val validPhotos = saturnPhotoResult.data.filter {it.mediaType == "image"}
                if (defaultPhoto == DefaultSaturnPhoto.RANDOM) validPhotos.random()
                else validPhotos.filter { it.isFavorite }.random()
            }
        }
    }
}