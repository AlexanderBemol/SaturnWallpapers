package com.amontdevs.saturnwallpapers.android

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.amontdevs.saturnwallpapers.android.di.androidSystemModules
import com.amontdevs.saturnwallpapers.android.di.viewModelModules
import com.amontdevs.saturnwallpapers.di.getPlatformModules
import com.amontdevs.saturnwallpapers.di.repositoryModules
import com.amontdevs.saturnwallpapers.di.sourceModules
import com.amontdevs.saturnwallpapers.di.systemModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SaturnApplication: Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        val contextModule = module {
            single { applicationContext }
        }
        startKoin {
            androidLogger()
            androidContext(this@SaturnApplication)
            modules(
                contextModule,
                getPlatformModules(),
                sourceModules,
                systemModules,
                androidSystemModules,
                repositoryModules,
                viewModelModules
            )
        }
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(15 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .respectCacheHeaders(false)
            .build()
    }
}