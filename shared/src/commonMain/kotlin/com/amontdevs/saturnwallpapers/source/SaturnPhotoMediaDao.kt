package com.amontdevs.saturnwallpapers.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMedia

@Dao
interface ISaturnPhotoMediaDao {
    @Insert
    suspend fun insert(vararg saturnPhotoMedia: SaturnPhotoMedia)

    @Update
    suspend fun update(vararg saturnPhotoMedia: SaturnPhotoMedia)
}