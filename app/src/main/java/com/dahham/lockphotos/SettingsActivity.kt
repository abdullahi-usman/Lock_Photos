package com.dahham.lockphotos

import android.app.WallpaperManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.Align
import android.os.Bundle
import android.util.TypedValue
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.applyCanvas
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceFragmentCompat
import androidx.work.ListenableWorker
import androidx.work.Operation
import androidx.work.await
import com.dahham.lockphotos.unsplash.UnsplashViewModel
import com.dahham.lockphotos.work.scheduleWorker
import com.dahham.lockphotos.zenquotes.ZenQuoteVeiwModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.math.ceil


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()

        }
        //supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onStart() {
        super.onStart()

        scheduleWorker(this).let {
            lifecycle.coroutineScope.launch {
                Toast.makeText(baseContext, "${it.await()}", Toast.LENGTH_LONG).show()
            }
        }
//
//        val viewModelProvider = ViewModelProvider.AndroidViewModelFactory.getInstance(application)
////
//        viewModelProvider.create(ZenQuoteVeiwModel::class.java).getRandomQuote {
//            it.toString()
//        }

//        val intent = Intent(WallpaperManager.ACTION_LIVE_WALLPAPER_CHOOSER)
//        val pack = WallpaperService::class.java.`package`?.name
//        val c = WallpaperService::class.java.canonicalName
//        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT, ComponentName(pack!!, c!!))
//        startActivity(intent)

//        viewModelProvider.create(UnsplashViewModel::class.java).getUnsplashPhoto({
//            it.toString()
//        }, {
//            it.toString()
//        })
//
//        viewModelProvider.create(UnsplashViewModel::class.java).getFromDatabase {
//            it.toString()
//        }

//        WallpaperManager.getInstance(this)?.let { wp ->
//
//            viewModelProvider.create(UnsplashViewModel::class.java).getRandomPhoto {
//                val bitmap = it?.bitmap?.copy(Bitmap.Config.ARGB_8888, true)
//                //val canvas = Canvas(bitmap!!)
//
//                lifecycleScope.launch(Job() + Dispatchers.Default) {
//                    lifecycleScope.async(Dispatchers.IO) {
//
//
//                        bitmap?.applyCanvas {
//                            val w = width
//                            val h = height
//                            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
//                            textPaint.color = Color.RED
//                            textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
//                                60f,
//                                resources.displayMetrics))
//                            textPaint.textAlign = Align.CENTER
//                            val metric = textPaint.fontMetrics
//                            val textHeight = ceil((metric.descent - metric.ascent).toDouble())
//                            val y = (textHeight - metric.descent)
//                            drawText("Text of the century", w / 2f, h / 2.0f, textPaint)
//
//                        }
//
//                    }.await()
//                    //canvas.drawBitmap(it?.bitmap!!, 0f, 0f, null)
//                    //canvas.drawText("We believe in yoooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooou", 50f, 50f, textPaint)
//
//                    wp.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
//
//                }
//            }
//        }
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            Toast.makeText(requireContext(), "Hopeee", Toast.LENGTH_LONG).show()
        }
    }
}