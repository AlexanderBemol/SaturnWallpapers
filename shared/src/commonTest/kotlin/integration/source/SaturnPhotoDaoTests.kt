package integration.source
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.source.ISaturnPhotoDao
import com.amontdevs.saturnwallpapers.source.ISettingsSource
import kotlinx.coroutines.runBlocking
import mock.di.getTestingPlatformModules
import mock.di.testSourceModules
import mock.source.MockFileManager
import mock.source.MockSaturnSettings
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail

class SaturnPhotoDaoTests: KoinTest {
    private val saturnPhotoDao by inject<ISaturnPhotoDao>()

    @BeforeTest
    fun beforeTest(){
        val mockModule = module{
            single<IFileManager> { MockFileManager() }
            single<ISettingsSource> { MockSaturnSettings() }
        }
        startKoin {
            allowOverride(true)
            modules(
                getTestingPlatformModules(),
                testSourceModules,
                mockModule,
            )
        }
    }

    @Test
    fun testWriteAndUpdates() {
        runBlocking {
            try {
                val savedPhoto = SaturnPhoto(
                    title = "Test Photo",
                    mediaType = "image",
                    highDefinitionUrl = ""
                )
                saturnPhotoDao.insertSaturnPhoto(savedPhoto)
                var saturnPhotos = saturnPhotoDao.getAllSaturnPhotos()
                assertTrue("SaturnPhoto not correctly saved") {
                    saturnPhotos.first() == savedPhoto
                }

            } catch (e: Exception) {
                fail(e.message)
            }

        }
    }
}