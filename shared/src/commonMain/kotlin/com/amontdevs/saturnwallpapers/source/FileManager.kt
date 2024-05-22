package com.amontdevs.saturnwallpapers.source

import com.amontdevs.saturnwallpapers.model.SaturnResult
import io.ktor.utils.io.ByteReadChannel

expect suspend fun platformSavePicture(bytes: ByteReadChannel, date: String): SaturnResult<String>

expect fun platformGetPicture(fileName: String): SaturnResult<ByteArray>

expect fun platformDeletePicture(fileName: String): SaturnResult<Unit>

expect fun platformSavePictureToExternalStorage(filepath: String): SaturnResult<Unit>


interface IFileManager {
    suspend fun savePicture(bytes: ByteReadChannel, date: String): SaturnResult<String>
    fun deletePicture(fileName: String): SaturnResult<Unit>
    fun getPicture(fileName: String): SaturnResult<ByteArray>
    suspend fun savePictureToExternalStorage(filepath: String): SaturnResult<Unit>
}

class FileManager: IFileManager {
    override suspend fun savePicture(bytes: ByteReadChannel, date: String) = platformSavePicture(bytes, date)
    override fun deletePicture(fileName: String): SaturnResult<Unit> = platformDeletePicture(fileName)
    override fun getPicture(fileName: String) = platformGetPicture(fileName)
    override suspend fun savePictureToExternalStorage(filepath: String): SaturnResult<Unit> =
        platformSavePictureToExternalStorage(filepath)
}