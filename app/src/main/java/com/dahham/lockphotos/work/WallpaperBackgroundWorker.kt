package com.dahham.lockphotos.work

import android.app.Application
import android.app.NotificationChannel
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.SystemClock
import android.util.TypedValue
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.dahham.lockphotos.R
import com.dahham.lockphotos.unsplash.UnsplashModelController
import com.dahham.lockphotos.unsplash.UnsplashViewModel
import com.dahham.lockphotos.zenquotes.ZenQuoteModelController
import com.dahham.lockphotos.zenquotes.ZenQuoteVeiwModel
import java.util.*

class WallpaperBackgroundWorker(appContext: Context,
                                params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val CHANNEL_ID = "BackgroundWallpaperService"
    private fun applyWallper(){

        val wallpaperManager = WallpaperManager.getInstance(applicationContext)
        val quoteModelController = ZenQuoteModelController.getInstance(applicationContext)

        UnsplashModelController.getInstance(applicationContext).getRandomPhoto {
            it?.bitmap?.let { bitmap ->

                wallpaperManager.setBitmap(bitmap)
                quoteModelController.getRandomQuote { quote ->

                    bitmap.copy(Bitmap.Config.ARGB_8888, true).let { __bitmap ->

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            if (quote != null) {
                                Palette.from(__bitmap).generate {
                                    __bitmap.applyCanvas {


                                        val paint = Paint()

                                        paint.color = it?.getLightMutedColor(it.getDarkVibrantColor(it.lightMutedSwatch?.bodyTextColor ?: Color.LTGRAY))
                                            ?: it?.vibrantSwatch?.bodyTextColor ?: Color.DKGRAY

                                        paint.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 28f, applicationContext.resources.displayMetrics)
                                        paint.textAlign = Paint.Align.CENTER

                                        drawText(quote.q, width / 2f, height / 2f, paint)
                                    }
                                }
                            }

                            wallpaperManager.setBitmap(__bitmap ?: bitmap,
                                null,
                                true,
                                WallpaperManager.FLAG_LOCK)
                        }
                    }
                }
            }
        }
    }

    override suspend fun doWork(): Result {
        val current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        //if(true){//(current_hour > 22 || current_hour < 7).not()){
            applyWallper()
        //}

        return Result.success()
    }


    override suspend fun getForegroundInfo(): ForegroundInfo {

        val nChannel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName("Background Listener Worker").build()

        NotificationManagerCompat.from(applicationContext).createNotificationChannel(nChannel)

        val not = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentText("Obtaining wallpapers from network").setSmallIcon(R.drawable.ic_noti).setContentTitle("Background Wallpaper Service").build()

        return ForegroundInfo(WallpaperBackgroundWorker::class.java.hashCode(), not)

    }
}