package com.itdeveapps.stepsshare.utils

import android.util.Log
import com.itdeveapps.stepsshare.di.Logger

class AndroidLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        Log.w(tag, lazyMessage())
    }
}


