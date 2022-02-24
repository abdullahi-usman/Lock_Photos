package com.dahham.lockphotos.unsplash

import android.content.Context
import android.text.style.UnderlineSpan
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class UnsplashModelController(context: Context) {

    companion object{
        private var INSTANCE: UnsplashModelController? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: UnsplashModelController(context).also {
                INSTANCE = it
            }
        }
    }

    interface UnsplashUpdateListener{

        fun progressListener(callback: ((unsplashPhoto: UnsplashPhoto?) -> Unit)?): UnsplashUpdateListener
        fun completionListener(callbackFinished: ((Array<UnsplashPhoto>?) -> Unit)?): UnsplashUpdateListener
    }

    private val unsplashNetworkHandler = UnsplashNetworkHandler.getInstance(context)
    private val unsplashPhotoDatabase = UnsplashPhotoDatabase.getInstance(context)

    private var progressListenerCallback: ((unsplashPhoto: UnsplashPhoto?) -> Unit)? = null
    private var completionListenerCallback: ((callbackFinished: Array<UnsplashPhoto>?) -> Unit)? = null

    private val unsplashUpdateListner = object: UnsplashUpdateListener {

        override fun progressListener(callback: ((UnsplashPhoto?) -> Unit)?): UnsplashUpdateListener {
            progressListenerCallback = callback
            return this
        }

        override fun completionListener(callbackFinished: ((Array<UnsplashPhoto>?) -> Unit)?): UnsplashUpdateListener {
            completionListenerCallback = callbackFinished
            return this
        }


    }

    private fun getUnsplashPhoto(): UnsplashUpdateListener{

        unsplashNetworkHandler.loadRandomImages(3).invokeOnProgressListener {
            progressListenerCallback?.invoke(it)
        }.invokeOnCompletion {
            completionListenerCallback?.invoke(it)

            if(it != null && it.size > 0){
                //lifecycle.launch(Dispatchers.IO) {
                    unsplashPhotoDatabase.Purge(4)
                //}
            }
        }

        return unsplashUpdateListner
    }

    fun getRandomPhoto(callback: (UnsplashPhoto?) -> Unit){
        getFromDatabase {
            if (it == null || it.noOfUsed >= 5){
                val listener = getUnsplashPhoto()
                listener.completionListener {
                    callback.invoke(it?.getOrNull(0))
                }

            }else{
                callback(it)
            }
        }
    }

    private fun getFromDatabase(callback: ((UnsplashPhoto?) -> Unit)){
        //lifecycle.launch(Dispatchers.IO) {
            unsplashPhotoDatabase.getRandomPhoto()?.let {
                //withContext(Dispatchers.Main){
                    callback.invoke(it)
                //}

            } ?: callback.invoke(null)
        //}
    }

}