package com.amontdevs.saturnwallpapers.source

import androidx.room.Database
import androidx.room.RoomDatabase
import com.amontdevs.saturnwallpapers.model.SaturnPhoto

@Database(entities = [SaturnPhoto::class], version = 1)
abstract class SaturnDatabase: RoomDatabase() {
    abstract fun saturnPhotoDao(): ISaturnPhotoDao
}