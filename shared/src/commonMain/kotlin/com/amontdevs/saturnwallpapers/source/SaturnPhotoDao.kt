package com.amontdevs.saturnwallpapers.source

import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import io.realm.kotlin.Realm
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmUUID

interface ISaturnPhotoDao {
    suspend fun saveSaturnPhoto(saturnPhoto: SaturnPhoto)
    suspend fun updateSaturnPhoto(uuid: String, isFavorite: Boolean): SaturnPhoto
    suspend fun getAllSaturnPhotos(): List<SaturnPhoto>
    suspend fun getSaturnPhotos(startTime: RealmInstant, endTime: RealmInstant): List<SaturnPhoto>
    suspend fun getLastSaturnPhoto(sort: Sort): SaturnPhoto
    suspend fun getSaturnPhoto(date: RealmInstant): SaturnPhoto
    suspend fun getSaturnPhoto(uuid: String): SaturnPhoto
}

class SaturnPhotoDao(
    private val realm: Realm
) : ISaturnPhotoDao {
    override suspend fun saveSaturnPhoto(saturnPhoto: SaturnPhoto) {
        realm.write {
            copyToRealm(saturnPhoto)
        }
    }

    override suspend fun getAllSaturnPhotos(): List<SaturnPhoto> =
        realm.query(SaturnPhoto::class).find()

    override suspend fun getSaturnPhotos(startTime: RealmInstant, endTime: RealmInstant): List<SaturnPhoto> {
        return realm.query(SaturnPhoto::class,"date >= $0 AND date <= $1", startTime, endTime).find()
    }

    override suspend fun getLastSaturnPhoto(sort: Sort): SaturnPhoto {
        return realm.query(SaturnPhoto::class).sort(SaturnPhoto::date.name, sortOrder = sort).find().first()
    }
    override suspend fun getSaturnPhoto(date: RealmInstant): SaturnPhoto {
        return realm.query(SaturnPhoto::class, "date == $0",date).find().first()
    }

    override suspend fun getSaturnPhoto(uuid: String): SaturnPhoto {
        return realm.query(SaturnPhoto::class, "id == $0", RealmUUID.from(uuid)).find().first()
    }

    override suspend fun updateSaturnPhoto(uuid: String, isFavorite: Boolean): SaturnPhoto {
        return realm.write {
            val saturnPhoto = this.query(SaturnPhoto::class, "id == $0", RealmUUID.from(uuid)).find().first()
            saturnPhoto.isFavorite = isFavorite
            saturnPhoto
        }
    }
}