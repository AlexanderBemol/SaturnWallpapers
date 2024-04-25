package com.amontdevs.saturnwallpapers.source

import com.amontdevs.saturnwallpapers.model.SaturnResult
import io.ktor.utils.io.ByteReadChannel

actual suspend fun platformSavePicture(
    bytes: ByteReadChannel,
    date: String
): SaturnResult<String> {
    TODO("Not yet implemented")
}

actual fun platformGetPicture(fileName: String): SaturnResult<ByteArray> {
    TODO("Not yet implemented")
}