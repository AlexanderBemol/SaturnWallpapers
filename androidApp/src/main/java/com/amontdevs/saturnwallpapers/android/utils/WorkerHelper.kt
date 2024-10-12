package com.amontdevs.saturnwallpapers.android.utils

import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.amontdevs.saturnwallpapers.android.services.PhotoDownloaderWorker
import com.amontdevs.saturnwallpapers.android.services.SaturnDailyWorker
import com.amontdevs.saturnwallpapers.resources.SaturnConstants
import java.util.UUID
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

class WorkerHelper {
    companion object {
        fun setWorker(workManager: WorkManager, worker: SaturnWorker): UUID? {
            val workInfo = workManager.getWorkInfosForUniqueWork(worker.workerId)
            val scheduledStates = listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED)

            //Schedule only if there is no scheduled work
            return if (workInfo.get().none { scheduledStates.contains(it.state) }) {
                try {
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                    when(worker){
                        SaturnWorker.DAILY_WORKER -> {
                            val periodicWorkRequest =
                                PeriodicWorkRequestBuilder<SaturnDailyWorker>(
                                    SaturnConstants.WORKER_PERIOD.hours.toJavaDuration()
                                )
                                    .setConstraints(constraints)
                                    .build()

                            workManager.enqueueUniquePeriodicWork(
                                worker.workerId,
                                ExistingPeriodicWorkPolicy.KEEP,
                                periodicWorkRequest
                            )
                            Log.d("SaturnWorker", "Work scheduled")
                            periodicWorkRequest.id
                        }
                        SaturnWorker.DOWNLOADER_WORKER -> {
                            val workRequest = OneTimeWorkRequestBuilder<PhotoDownloaderWorker>()
                                .setConstraints(constraints)
                                .build()

                            workManager.enqueueUniqueWork(
                                worker.workerId,
                                ExistingWorkPolicy.KEEP,
                                workRequest
                            )
                            Log.d("SaturnWorker", "Work scheduled")
                            workRequest.id
                        }
                    }
                } catch (e: Exception) {
                    Log.d("SaturnWorker", "Failed to schedule work: ${e.message}")
                    null
                }
            } else {
                Log.d("SaturnWorker", "Work already scheduled")
                workInfo.get()[0].id
            }
        }

        fun stopWorker(workManager: WorkManager) {
            try {
                workManager.cancelUniqueWork(SaturnConstants.WORKER_ID)
                Log.d("SaturnWorker", "Work cancelled")
            } catch (e: Exception) {
                Log.d("SaturnWorker", "Failed to cancel work: ${e.message}")
            }
        }

        fun isWorkerRunning(workManager: WorkManager, worker: SaturnWorker): Boolean {
            val workInfo = workManager.getWorkInfosForUniqueWork(worker.workerId)
            val scheduledStates = listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED)
            return workInfo.get().any { scheduledStates.contains(it.state) }
        }
    }

    enum class SaturnWorker(
        val workerId: String
    ){
        DAILY_WORKER("SATURN_DAILY_WORKER"),
        DOWNLOADER_WORKER("SATURN_DOWNLOADER_WORKER"),
    }
}

