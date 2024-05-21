package com.amontdevs.saturnwallpapers.source

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.amontdevs.saturnwallpapers.model.SaturnResult
import io.ktor.util.cio.writeChannel
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.copyAndClose
import kotlinx.datetime.Clock
import org.koin.core.context.GlobalContext
import java.io.File
import java.io.FileOutputStream

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

actual fun platformDeletePicture(fileName: String): SaturnResult<Unit> {
    return try {
        val context = GlobalContext.get().get<Context>()
        val directory = context.getDir("images", Context.MODE_PRIVATE)
        File(directory, fileName).delete()
        SaturnResult.Success(Unit)
    } catch (e: Exception) {
        SaturnResult.Error(e)
    }
}

actual fun platformSavePictureToExternalStorage(filepath: String): SaturnResult<Unit> {
    // Read the image from the internal memory
    return try {
        val context = GlobalContext.get().get<Context>()
        val directory = context.getDir("images", Context.MODE_PRIVATE)
        val bitmap = BitmapFactory.decodeFile(File(directory, filepath).path)

        // Create a new file in the public media directory
        val fileName = "SaturnPhoto_${filepath}.jpg"
        val publicDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val newFile = File(publicDirectory, fileName)

        // Save the image to the new file
        val outputStream = FileOutputStream(newFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.flush()
        outputStream.close()

        // Add the new file to the MediaStore
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/${context.packageName}")
            }
        }
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        SaturnResult.Success(Unit)
    } catch (e: Exception) {
        SaturnResult.Error(e)
    }
}