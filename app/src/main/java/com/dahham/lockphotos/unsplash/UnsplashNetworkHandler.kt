package com.dahham.lockphotos.unsplash

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.lifecycle.LifecycleCoroutineScope
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.StringRequest
import com.dahham.lockphotos.volley.VolleyFactoryInstance
import com.google.gson.Gson
import kotlinx.coroutines.*
import java.util.*

class UnsplashNetworkHandler private constructor(context: Context){


    private val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
    private var RANDOM_PHOTO_COUNT = 10
    private val HOME_URL = "https://api.unsplash.com/"
    private val TRIGGER_DOWNLOAD_URL = "photos/:id/download"
    private val RANDOM_URL = "photos/random"
    private var unsplashPhotos = LinkedList<UnsplashPhoto>()


    private val ACCESS_KEY = "?client_id=6242a05babfcf25857a9470fdeaf2a9ab3f14856c90539b7918033792ca5292b"
    private val SECRET_KEY = "3a34f246f243607869d59b35ab6180906f79429a57e4542eb027a598b2cb680e"

    companion object {
        @Volatile
        private var INSTANCE: UnsplashNetworkHandler? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){

            INSTANCE ?: UnsplashNetworkHandler(context).also {
                INSTANCE = it
            }
        }
    }

    private val volley by lazy {
        VolleyFactoryInstance.getInstance(context)
    }

    private val unsplashPhotoDatabase by lazy {
        UnsplashPhotoDatabase.getInstance(context)
    }

    private var completionListener: ((Array<UnsplashPhoto>?) -> Unit)? = null
    private var onProgressListener: ((UnsplashPhoto) -> Unit)? = null
    private var onErrorListener: ((String?) -> Unit)? = null

    private fun loadImage(unsplashPhoto: UnsplashPhoto, callback: (unsplashPhoto: UnsplashPhoto?) -> Unit = this::loadImages) {
        val imageRequest = ImageRequest(unsplashPhoto.urls.raw, {
            if(it != null) {
                unsplashPhoto.bitmap = it
                callback(unsplashPhoto)
            }
        }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888, {
            unsplashPhoto.bitmap = null
            callback(unsplashPhoto)
        })

        volley.queueRequest(imageRequest)
       /* ImageLoader(volley.requestQueue, object : ImageLoader.ImageCache{
            override fun getBitmap(url: String?): Bitmap? { return null }

            override fun putBitmap(url: String?, bitmap: Bitmap?) {}

        }).get(unsplashPhoto.urls.raw, object : ImageLoader.ImageListener{
            override fun onErrorResponse(error: VolleyError?) {
                callback(null)
            }

            override fun onResponse(response: ImageLoader.ImageContainer?, isImmediate: Boolean) {
                if (isImmediate.not()) {
                    unsplashPhoto.bitmap = response?.bitmap
                    callback(unsplashPhoto)
                }
            }

        })*/
    }

    private fun loadImages(unsplashPhoto: UnsplashPhoto?){
        if (unsplashPhoto != null){
            if (unsplashPhoto.bitmap != null) {
                onProgressListener?.invoke(unsplashPhoto)
            }else {
                onErrorListener?.invoke("Error getting image")
            }
        }

        if (unsplashPhoto?.bitmap != null){
            coroutineScope.launch {
                unsplashPhotoDatabase.savePhoto(unsplashPhoto)
            }
        }

        unsplashPhotos.poll()?.let {
            loadImage(it)
        } ?: completionListener?.invoke(unsplashPhotos.toTypedArray())
    }

    fun loadRandomImages(noOfPhotos: Int = RANDOM_PHOTO_COUNT): UnsplashNetworkHandler {
        if (noOfPhotos != RANDOM_PHOTO_COUNT) {
            RANDOM_PHOTO_COUNT = noOfPhotos
        }

        val request = StringRequest("https://api.unsplash.com/photos/random?client_id=6242a05babfcf25857a9470fdeaf2a9ab3f14856c90539b7918033792ca5292b&count=1", {
            val gson = Gson()
            gson.fromJson(it, Array<UnsplashPhoto>::class.java).let {
                if (unsplashPhotos.addAll(it)){
                    loadImages(null)
                }else {
                    onErrorListener?.invoke("Failed to add items")
                    completionListener?.invoke(unsplashPhotos.toTypedArray())
                }
            }
        }, {
            onErrorListener?.invoke(it.message)
        })

        volley.queueRequest(request)

        return this
    }

    fun invokeOnCompletion(callback: (Array<UnsplashPhoto>?) -> Unit): UnsplashNetworkHandler{
        completionListener = callback
        return this
    }

    fun invokeOnProgressListener(callback: (UnsplashPhoto?) -> Unit): UnsplashNetworkHandler{
        onProgressListener = callback
        return this
    }

    fun invokeOnErrorListener(callback: (String?) -> Unit): UnsplashNetworkHandler{
        onErrorListener = callback
        return this
    }
}