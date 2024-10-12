package com.amontdevs.saturnwallpapers.di

import androidx.room.Room
import com.amontdevs.saturnwallpapers.source.SaturnDatabase
import platform.Foundation.NSHomeDirectory
import org.koin.dsl.module

fun buildRoomDatabase(): SaturnDatabase {
    val dbFile = NSHomeDirectory() + "/saturn.db"
    return Room.databaseBuilder<SaturnDatabase>(
        name = dbFile
    ).build()
}

actual fun getPlatformModules() = module {
    single { buildRoomDatabase() }
}