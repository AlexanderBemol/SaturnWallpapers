package com.amontdevs.saturnwallpapers.model

sealed class SaturnResult<out T> {
    data class Success<out T>(val data: T): SaturnResult<T>()
    data class Error(val e: Exception): SaturnResult<Nothing>()
}