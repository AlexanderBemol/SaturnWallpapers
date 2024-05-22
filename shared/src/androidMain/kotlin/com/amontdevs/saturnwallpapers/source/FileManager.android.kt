package com.amontdevs.saturnwallpapers.source

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
        val internalDirectory = context.getDir("images", Context.MODE_PRIVATE)
        val internalFile = File(internalDirectory, filepath).path

        val fileName = "SaturnPhoto_${filepath}.jpg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val resolver = context.contentResolver
            val uri  = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val outputStream = resolver.openOutputStream(uri!!)
            val inputStream = File(internalFile).inputStream()
            inputStream.copyTo(outputStream!!)
            SaturnResult.Success(Unit)
        } else {
            val bitmap = BitmapFactory.decodeFile(internalFile)
            val picturesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val newFile = File(picturesDirectory, fileName)
            if (!picturesDirectory.exists()) {
                picturesDirectory.mkdirs()
            }

            // Save the image to the new file
            val outputStream = FileOutputStream(newFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            SaturnResult.Success(Unit)
        }

    } catch (e: Exception) {
        SaturnResult.Error(e)
    }
}