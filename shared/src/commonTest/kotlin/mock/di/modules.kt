package mock.di

import com.amontdevs.saturnwallpapers.di.buildAPODService
import com.amontdevs.saturnwallpapers.di.buildFileManager
import com.amontdevs.saturnwallpapers.di.buildHTTPClient
import com.amontdevs.saturnwallpapers.di.buildSaturnSettings
import com.amontdevs.saturnwallpapers.di.buildSettings
import com.amontdevs.saturnwallpapers.di.buildTimeProvider
import com.amontdevs.saturnwallpapers.di.provideSaturnPhotoDao
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun getTestingPlatformModules(): Module

val testSourceModules = module {
    single { buildTimeProvider() }
    single { buildHTTPClient() }
    single { buildAPODService(get()) }
    single { buildFileManager() }
    single { buildSettings() }
    single { buildSaturnSettings(get()) }
    single { provideSaturnPhotoDao(get())  }
}