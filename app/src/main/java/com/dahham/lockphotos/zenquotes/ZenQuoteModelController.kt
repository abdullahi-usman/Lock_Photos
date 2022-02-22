package com.dahham.lockphotos.zenquotes

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ZenQuoteModelController(context: Context) {

    companion object{
        private var INSTANCE: ZenQuoteModelController? = null

        fun getInstance(context: Context) = INSTANCE ?: synchronized(this){
            INSTANCE ?: ZenQuoteModelController(context).also {
                INSTANCE = it
            }
        }
    }

    var quoteDatabase = ZenQuoteDatabase.getInstance(context)
    var zenQuotesNetworkHandler = ZenQuotesNetworkHandler.getInstance(context)
    var lifecycle = CoroutineScope(Job() + Dispatchers.IO)


    private fun getQuotesFromNetworkAndCache(callback: ((Array<ZenQuote>?) -> Unit)? = null){


        zenQuotesNetworkHandler
            .getQuotesFromNetwork { quotes ->
                callback?.invoke(quotes)
                if(quotes != null) {
                    lifecycle.launch(Dispatchers.IO) {
                        quoteDatabase.cacheQuotes(quotes)
                    }
                }
            }
    }

    fun getRandomQuote(callback: (zenQuote: ZenQuote?) -> Unit) {

        lifecycle.launch {
            quoteDatabase.getDaO().getleastUsed()?.let {
                callback.invoke(it)

                if (it.noHasBeenUsed >= 5){
                    getQuotesFromNetworkAndCache{
                        if(it != null && it.size > 0){
                            lifecycle.launch(Dispatchers.IO) {
                                quoteDatabase.purge(4)
                            }
                        }
                    }
                }
            } ?: getQuotesFromNetworkAndCache{
                if(it != null){
                    callback(it.get(0))
                }else{
                    callback(null)
                }
            }
        }

    }

}