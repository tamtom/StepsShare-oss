package com.itdeveapps.stepsshare.utils

import com.itdeveapps.stepsshare.di.Logger
import platform.Foundation.NSLog

class IOSLogger : Logger {
    override fun log(tag: String, lazyMessage: () -> String) {
        NSLog("[$tag] ${lazyMessage()}")
    }
}


