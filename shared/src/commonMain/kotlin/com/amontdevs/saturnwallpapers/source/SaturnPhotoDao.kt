package com.amontdevs.saturnwallpapers.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
@Dao
interface ISaturnPhotoDao {
    @Insert
    suspend fun insertSaturnPhoto(vararg saturnPhoto: SaturnPhoto)

    @Update
    suspend fun updateSaturnPhoto(vararg saturnPhoto: SaturnPhoto)

    @Query("SELECT * FROM SaturnPhoto")
    suspend fun getAllSaturnPhotos(): List<SaturnPhoto>

    @Query("SELECT * FROM SaturnPhoto WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getSaturnPhotos(startTime: Long, endTime: Long): List<SaturnPhoto>

    @Query("SELECT * FROM SaturnPhoto WHERE timestamp = :timestamp")
    suspend fun getSaturnPhoto(timestamp: Long): SaturnPhoto

    @Query("SELECT * FROM SaturnPhoto WHERE id = :id")
    suspend fun getSaturnPhoto(id: Int): SaturnPhoto

    @Query("DELETE FROM SaturnPhoto WHERE isFavorite = false AND  timestamp < :timestamp")
    suspend fun deleteOldData(timestamp: Long)
}