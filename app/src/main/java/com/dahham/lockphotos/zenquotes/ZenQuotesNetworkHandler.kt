package com.dahham.lockphotos.zenquotes

import android.content.Context
import com.android.volley.toolbox.StringRequest
import com.dahham.lockphotos.volley.VolleyFactoryInstance
import com.google.gson.Gson
import kotlinx.coroutines.*

class ZenQuotesNetworkHandler private constructor(context: Context){

    private val QUOTE_URL = "https://zenquotes.io/api/quotes/7d2b3068fc84d4e0c9e8b6af28772351684bce5d"

    companion object {
        @Volatile
        private var INSTANCE: ZenQuotesNetworkHandler? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: ZenQuotesNetworkHandler(context).also {
                INSTANCE = it
            }
        }
    }


    private val volleyFactoryInstance by lazy {
        VolleyFactoryInstance.getInstance(context)
    }

    fun getQuotesFromNetwork(quoteHandler: (zenQuote: Array<ZenQuote>?) -> Unit){
        val request = StringRequest(QUOTE_URL, {
            val zenQuotes = Gson().fromJson(it, Array<ZenQuote>::class.java)
            quoteHandler.invoke(zenQuotes)
        }, {
            quoteHandler.invoke(null)
        })
        volleyFactoryInstance.queueRequest(request)
    }
}