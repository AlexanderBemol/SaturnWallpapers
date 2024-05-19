package com.amontdevs.saturnwallpapers.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.amontdevs.saturnwallpapers.source.SaturnDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import platform.Foundation.NSHomeDirectory
import com.amontdevs.saturnwallpapers.source.instantiateImpl
import org.koin.dsl.module

fun buildRoomDatabase(): SaturnDatabase {
    val dbFile = NSHomeDirectory() + "/saturn.db"
    return Room.databaseBuilder<SaturnDatabase>(
        name = dbFile,
        factory = { SaturnDatabase::class.instantiateImpl()}
    )
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver()) // Very important
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

actual fun getPlatformModules() = module {
    single { buildRoomDatabase() }
}