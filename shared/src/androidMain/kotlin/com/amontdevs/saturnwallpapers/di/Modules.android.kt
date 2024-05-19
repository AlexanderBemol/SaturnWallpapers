package com.amontdevs.saturnwallpapers.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.amontdevs.saturnwallpapers.source.SaturnDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module

fun buildRoomDatabase(context: Context): SaturnDatabase {
    val dbFile = context.getDatabasePath("saturn.db")
    return Room.databaseBuilder<SaturnDatabase>(
        context = context.applicationContext,
        name = dbFile.absolutePath
    )
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

actual fun getPlatformModules() = module {
        single { buildRoomDatabase(get()) }
}