package com.amontdevs.saturnwallpapers.source

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMedia

@Database(entities = [SaturnPhoto::class, SaturnPhotoMedia::class], version = 1)
@ConstructedBy(SaturnDatabaseConstructor::class)
abstract class SaturnDatabase: RoomDatabase() {
    abstract fun saturnPhotoDao(): ISaturnPhotoDao
    abstract fun saturnPhotoMediaDao(): ISaturnPhotoMediaDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object SaturnDatabaseConstructor : RoomDatabaseConstructor<SaturnDatabase> {
    override fun initialize(): SaturnDatabase
}