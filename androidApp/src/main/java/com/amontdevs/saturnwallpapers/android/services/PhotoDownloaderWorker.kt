package com.amontdevs.saturnwallpapers.android.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhotoDownloaderWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {
    private val saturnPhotosRepository: ISaturnPhotosRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            saturnPhotosRepository.downloadNotDownloadedPhotos()
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

}