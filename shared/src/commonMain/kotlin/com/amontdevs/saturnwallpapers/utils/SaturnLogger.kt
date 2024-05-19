package com.amontdevs.saturnwallpapers.utils

expect fun logSaturnMessage(tag: String, message: String)
expect fun logSaturnError(tag: String, e: Exception, message: String?)

interface ISaturnLogger {
    fun logMessage(tag: String, message: String)
    fun logError(tag: String, e: Exception, message: String?)
}
class SaturnLogger : ISaturnLogger {
    override fun logMessage(tag: String, message: String) {
       logSaturnMessage(tag, message)
    }

    override fun logError(tag: String, e: Exception, message: String?) {
        logSaturnError(tag, e, message)
    }
}