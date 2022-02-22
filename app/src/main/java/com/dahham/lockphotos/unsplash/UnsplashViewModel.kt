package com.dahham.lockphotos.unsplash

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dahham.lockphotos.ApplicationViewModel
import kotlinx.coroutines.*


class UnsplashViewModel(application: Application): ApplicationViewModel(application) {

    val unsplashModelController = UnsplashModelController.getInstance(application)
    fun getRandomPhoto(callback: (UnsplashPhoto?) -> Unit) {
        unsplashModelController.getRandomPhoto(callback)
    }
}