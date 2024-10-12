package com.amontdevs.saturnwallpapers.android.services

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.amontdevs.saturnwallpapers.model.RefreshOperationStatus
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhotoDownloaderWorker(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {
    private val workerScope = CoroutineScope(Dispatchers.IO)
    private val saturnPhotosRepository: ISaturnPhotosRepository by inject()

    override suspend fun doWork(): Result {
        workerScope.launch {
            saturnPhotosRepository.saturnPhotoOperation.collect{
                val status = workDataOf("progress" to it.progress)
                setProgress(status)
            }
        }
        return try {
            saturnPhotosRepository.downloadNotDownloadedPhotos()
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

}