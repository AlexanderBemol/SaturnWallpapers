package com.amontdevs.saturnwallpapers.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia

@Dao
interface ISaturnPhotoDao {
    @Insert
    suspend fun insertSaturnPhoto(vararg saturnPhoto: SaturnPhoto): List<Long>

    @Update
    suspend fun updateSaturnPhoto(vararg saturnPhoto: SaturnPhoto)

    @Query("SELECT * FROM SaturnPhoto")
    suspend fun getAllSaturnPhotos(): List<SaturnPhotoWithMedia>

    @Query("SELECT * FROM SaturnPhoto WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getSaturnPhotos(startTime: Long, endTime: Long): List<SaturnPhoto>

    @Query("SELECT * FROM SaturnPhoto WHERE timestamp = :timestamp")
    suspend fun getSaturnPhotoByTimestamp(timestamp: Long): SaturnPhotoWithMedia

    @Query("SELECT * FROM SaturnPhoto WHERE id = :id")
    suspend fun getSaturnPhoto(id: Long): SaturnPhotoWithMedia

    @Query("SELECT * FROM SaturnPhoto WHERE isFavorite = false AND timestamp < :timestamp")
    suspend fun findOldData(timestamp: Long): List<SaturnPhotoWithMedia>

    @Query("DELETE FROM SaturnPhoto WHERE isFavorite = false AND timestamp < :timestamp")
    suspend fun deleteOldData(timestamp: Long)

    @Query("SELECT * FROM SaturnPhoto WHERE id IN (:id)")
    suspend fun getSaturnPhotosWithMediaById(id: List<Long>): List<SaturnPhotoWithMedia>
}