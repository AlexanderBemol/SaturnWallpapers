package com.amontdevs.saturnwallpapers.source

import com.amontdevs.saturnwallpapers.model.SaturnResult
import io.ktor.utils.io.ByteReadChannel

expect suspend fun platformSavePicture(bytes: ByteReadChannel, date: String): SaturnResult<String>

expect fun platformGetPicture(fileName: String): SaturnResult<ByteArray>


interface IFileManager {
    suspend fun savePicture(bytes: ByteReadChannel, date: String): SaturnResult<String>
    fun getPicture(fileName: String): SaturnResult<ByteArray>
}

class FileManager: IFileManager {
    override suspend fun savePicture(bytes: ByteReadChannel, date: String) = platformSavePicture(bytes, date)

    override fun getPicture(fileName: String) = platformGetPicture(fileName)
}