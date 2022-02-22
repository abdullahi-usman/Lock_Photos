package com.dahham.lockphotos

import android.app.Service
import android.app.WallpaperManager
import android.content.Intent
import android.graphics.*
import android.os.IBinder
import android.service.wallpaper.WallpaperService
import android.util.SparseArray
import android.view.SurfaceHolder
import android.widget.Toast
import androidx.core.graphics.BitmapCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.ViewModelProvider
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.dahham.lockphotos.unsplash.UnsplashViewModel
import com.dahham.lockphotos.zenquotes.ZenQuoteVeiwModel

class WallpaperService : WallpaperService() {

    private val engine =  WallpaperEngine()
    private lateinit var wallpaperManager: WallpaperManager
    private lateinit var screenSize: Rect
    private lateinit var bitmaps: SparseArray<Bitmap>

    private lateinit var unsplashViewModel: UnsplashViewModel
    private lateinit var zenQuoteVeiwModel: ZenQuoteVeiwModel

    override fun onCreate() {
        super.onCreate()
        val viewModel =  ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        wallpaperManager = WallpaperManager.getInstance(this)
        unsplashViewModel = viewModel.create(UnsplashViewModel::class.java)
        zenQuoteVeiwModel = viewModel.create(ZenQuoteVeiwModel::class.java)

        screenSize = Rect(0, 0, wallpaperManager.desiredMinimumWidth, wallpaperManager.desiredMinimumHeight)
    }

    override fun onCreateEngine(): Engine {
        return WallpaperEngine()
    }

    inner class WallpaperEngine: WallpaperService.Engine(){
        override fun onCreate(surfaceHolder: SurfaceHolder?) {
            super.onCreate(surfaceHolder)
            setTouchEventsEnabled(false)
            setOffsetNotificationsEnabled(true)
        }

        override fun onSurfaceCreated(holder: SurfaceHolder?) {
            super.onSurfaceCreated(holder)
            drawImage(holder!!)
        }

        override fun onSurfaceChanged(
            holder: SurfaceHolder?,
            format: Int,
            width: Int,
            height: Int
        ) {
            super.onSurfaceChanged(holder, format, width, height)
            drawImage(holder!!)
        }

        override fun onSurfaceRedrawNeeded(holder: SurfaceHolder?) {
            super.onSurfaceRedrawNeeded(holder)
            drawImage(holder!!)
        }

        private fun drawImage(surfaceHolder: SurfaceHolder){
            surfaceHolder.lockCanvas().apply {
                //if (isPreview) {
                  //  drawColor(Color.YELLOW)
                //}else {
                    //drawColor(Color.MAGENTA)

                //val bit = BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)

                unsplashViewModel.getRandomPhoto {
                    drawBitmap(it?.bitmap!!, 0f, 0f,  Paint(Paint.ANTI_ALIAS_FLAG  or Paint.FILTER_BITMAP_FLAG))

                }

                //}
                surfaceHolder.unlockCanvasAndPost(this)
            }
        }

    }
}