package com.dahham.lockphotos.volley

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.Volley

class VolleyFactoryInstance private constructor(context: Context){

    companion object {
        @Volatile
        private var INSTANCE: VolleyFactoryInstance? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: VolleyFactoryInstance(context).also {
                    INSTANCE = it
                }
            }
    }

    val requestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    fun<T> queueRequest(request: Request<T>){
        requestQueue.add(request)
        requestQueue.start()
    }


}