package com.dahham.lockphotos.work

import android.app.WallpaperManager
import android.content.Context
import android.os.Build
import androidx.work.*
import java.util.concurrent.TimeUnit


fun scheduleWorker(context: Context, sync_hours: Long = 1): Operation{

    val wallpaperWorkerBuilder = PeriodicWorkRequestBuilder<WallpaperBackgroundWorker>(sync_hours * 60 + 30, TimeUnit.MINUTES, 15, TimeUnit.MINUTES)
    val wallpaperWorkerConstraint = Constraints.Builder()
    wallpaperWorkerConstraint.setRequiresBatteryNotLow(true)
    wallpaperWorkerConstraint.setRequiresStorageNotLow(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        wallpaperWorkerConstraint.setRequiresDeviceIdle(true)
    }

    wallpaperWorkerBuilder.setConstraints(wallpaperWorkerConstraint.build())
    return WorkManager.getInstance(context).enqueueUniquePeriodicWork("WorkerPQ", ExistingPeriodicWorkPolicy.KEEP, wallpaperWorkerBuilder.build())
}
