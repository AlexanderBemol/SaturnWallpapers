package mock.source

import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.source.IFileManager
import io.ktor.utils.io.ByteReadChannel

class MockFileManager: IFileManager {
    override suspend fun savePicture(bytes: ByteReadChannel, date: String) =
        SaturnResult.Success("fakepath")

    override fun deletePicture(fileName: String): SaturnResult<Unit> = SaturnResult.Success(Unit)

    override fun getPicture(fileName: String) = SaturnResult.Success(byteArrayOf())
}