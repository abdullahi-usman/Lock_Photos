package com.dahham.lockphotos

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel

abstract class ApplicationViewModel(application: Application): AndroidViewModel(application) {


    fun getRequiredContext(): Context {
        return getApplication<Application>().baseContext
    }
}