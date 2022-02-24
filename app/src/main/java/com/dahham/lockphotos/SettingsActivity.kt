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
import androidx.preference.ListPreference
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
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            findPreference<ListPreference>("sync_hours")?.setOnPreferenceChangeListener { preference, newValue ->
                scheduleWorker(requireContext(), newValue as Long)
                return@setOnPreferenceChangeListener true
            }
        }
    }
}