package com.amontdevs.saturnwallpapers.android

import android.app.Application
import com.amontdevs.saturnwallpapers.android.di.viewModelModules
import com.amontdevs.saturnwallpapers.di.getPlatformModules
import com.amontdevs.saturnwallpapers.di.repositoryModules
import com.amontdevs.saturnwallpapers.di.sourceModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class SaturnApplication: Application() {
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
                repositoryModules,
                viewModelModules
            )
        }
    }
}