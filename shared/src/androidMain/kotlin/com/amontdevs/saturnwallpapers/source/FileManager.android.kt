package com.amontdevs.saturnwallpapers.source

import android.content.Context
import com.amontdevs.saturnwallpapers.model.SaturnResult
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.datetime.Clock
import org.koin.core.context.GlobalContext
import java.io.File

actual suspend fun platformSavePicture(bytes: ByteReadChannel, date: String): SaturnResult<String> {
    return try {
        val context = GlobalContext.get().get<Context>()
        val directory = context.getDir("images", Context.MODE_PRIVATE)
        val filename = "${Clock.System.now().toEpochMilliseconds()}-$date"
        val file = File(directory, filename)
        bytes.copyAndClose(file.writeChannel())
        SaturnResult.Success(filename)
    } catch (e: Exception) {
        SaturnResult.Error(e)
    }
}

actual fun platformGetPicture(fileName: String): SaturnResult<ByteArray> {
    return try {
        val context = GlobalContext.get().get<Context>()
        val directory = context.getDir("images", Context.MODE_PRIVATE)
        SaturnResult.Success(File(directory, fileName).inputStream().readBytes())
    } catch (e: Exception) {
        SaturnResult.Error(e)
    }
}