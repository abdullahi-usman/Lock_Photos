package com.dahham.lockphotos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.dahham.lockphotos.unsplash.UnsplashNetworkHandler
import com.dahham.lockphotos.zenquotes.ZenQuoteDatabase
import com.dahham.lockphotos.zenquotes.ZenQuotesNetworkHandler
import kotlinx.coroutines.*

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    val context = ApplicationProvider.getApplicationContext<Context>()
    val zenQuoteDatabase = ZenQuoteDatabase.getInstance(context)
    val zenQuotesNetworkHandler = ZenQuotesNetworkHandler.getInstance(context)

    val unsplashNetworkHandler = UnsplashNetworkHandler.getInstance(context)

    val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)



    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dahham.lockphotos", appContext.packageName)
    }

    @Test
    fun useZenQuotes(){
        zenQuotesNetworkHandler.getQuotesFromNetwork{
            GlobalScope.launch {
                zenQuoteDatabase.cacheQuotes(context, it!!)
            }
        }
    }

    @Test
    fun useCache(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        coroutineScope.launch {
            zenQuoteDatabase.getQuotes(context).let {
                it.toString()
            }
        }

    }

    @Test
    fun getUnsplashPhoto(){
        unsplashNetworkHandler.loadRandomImages(1).invokeOnProgressListener {
            it.toString()
        }.invokeOnCompletion {
            it.toString()
        }
    }
    
}