package com.amontdevs.saturnwallpapers.android.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.koin.core.component.KoinComponent

class PhotoDownloader(
    appContext: Context,
    workerParams: WorkerParameters
): CoroutineWorker(appContext, workerParams), KoinComponent {
    override suspend fun doWork(): Result {
        return try {
            //
            Result.success()
        }catch (e: Exception){
            Result.failure()
        }
    }

}