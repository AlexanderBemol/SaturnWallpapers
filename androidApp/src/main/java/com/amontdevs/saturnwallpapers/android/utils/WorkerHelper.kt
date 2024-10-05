package com.amontdevs.saturnwallpapers.android.utils

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.amontdevs.saturnwallpapers.android.services.SaturnWorker
import com.amontdevs.saturnwallpapers.resources.SaturnConstants
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.hours
import kotlin.time.toJavaDuration

class WorkerHelper {
    companion object {
        fun setWorker(workManager: WorkManager) {
            val workInfo = workManager.getWorkInfosForUniqueWork(SaturnConstants.WORKER_ID)
            val scheduledStates = listOf(WorkInfo.State.ENQUEUED, WorkInfo.State.RUNNING, WorkInfo.State.BLOCKED)

            //Schedule only if there is no scheduled work
            if (workInfo.get().none { scheduledStates.contains(it.state) }) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

                val periodicWorkRequest =
                    PeriodicWorkRequestBuilder<SaturnWorker>(
                        SaturnConstants.WORKER_PERIOD.hours.toJavaDuration()
                    )
                        .setConstraints(constraints)
                        .build()

                try {
                    workManager.enqueueUniquePeriodicWork(
                        SaturnConstants.WORKER_ID,
                        ExistingPeriodicWorkPolicy.KEEP,
                        periodicWorkRequest
                    )
                    Log.d("SaturnWorker", "Work scheduled")
                } catch (e: Exception) {
                    Log.d("SaturnWorker", "Failed to schedule work: ${e.message}")
                }

            } else {
                Log.d("SaturnWorker", "Work already scheduled")
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
    }
}