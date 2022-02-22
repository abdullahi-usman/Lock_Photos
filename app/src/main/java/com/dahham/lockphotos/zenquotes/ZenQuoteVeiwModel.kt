package com.dahham.lockphotos.zenquotes

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dahham.lockphotos.ApplicationViewModel
import kotlinx.coroutines.*

class ZenQuoteVeiwModel(application: Application) : ApplicationViewModel(application) {

    val zenQuoteModelController = ZenQuoteModelController.getInstance(application)
    fun getRandomQuote(callback: (zenQuote: ZenQuote?) -> Unit) {
        zenQuoteModelController.getRandomQuote(callback)
    }

    override fun onCleared() {
        super.onCleared()
        if (viewModelScope.isActive) {
            viewModelScope.cancel()
        }
    }

}