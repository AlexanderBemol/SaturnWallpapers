package com.amontdevs.saturnwallpapers.utils

import android.util.Log

actual fun logSaturnMessage(tag: String, message: String) {
    Log.d(tag,message)
}
actual fun logSaturnError(tag: String, e: Exception, message: String?) {
    println(message)
}