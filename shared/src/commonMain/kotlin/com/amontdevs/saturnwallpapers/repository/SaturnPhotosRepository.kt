package com.amontdevs.saturnwallpapers.repository

import com.amontdevs.saturnwallpapers.model.AlreadyPopulatedException
import com.amontdevs.saturnwallpapers.model.ApodModel
import com.amontdevs.saturnwallpapers.model.PhotoNotFoundException
import com.amontdevs.saturnwallpapers.model.RefreshOperationStatus
import com.amontdevs.saturnwallpapers.model.SaturnConfig
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.source.IAPODService
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.source.ISaturnPhotoDao
import com.amontdevs.saturnwallpapers.source.ISettingsSource
import com.amontdevs.saturnwallpapers.source.ITimeProvider
import com.amontdevs.saturnwallpapers.utils.toCommonFormat
import com.amontdevs.saturnwallpapers.utils.toInstant
import com.amontdevs.saturnwallpapers.utils.toRealmInstant
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

interface ISaturnPhotosRepository {
    val saturnPhotosFlow: MutableSharedFlow<SaturnResult<SaturnPhoto>>
    val saturnPhotoOperation: StateFlow<RefreshOperationStatus>
    suspend fun populate(): SaturnResult<Unit>
    suspend fun getSaturnPhoto(date: Instant): SaturnResult<SaturnPhoto>
    suspend fun getSaturnPhoto(uuid: String): SaturnResult<SaturnPhoto>
    suspend fun getSaturnPhotos(startTime: Instant, endTime: Instant): SaturnResult<List<SaturnPhoto>>
    suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhoto>>
    suspend fun updateSaturnPhoto(uuid: String, isFavorite: Boolean): SaturnResult<SaturnPhoto>
    suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<List<SaturnPhoto>>
    suspend fun refresh(): SaturnResult<Unit>
}

class SaturnPhotosRepository(
    private val apodService: IAPODService,
    private val saturnPhotoDao: ISaturnPhotoDao,
    private val timeProvider: ITimeProvider,
    private val fileManager: IFileManager,
    private val saturnSettings: ISettingsSource
): ISaturnPhotosRepository {

    private val _saturnPhotosFlow = MutableSharedFlow<SaturnResult<SaturnPhoto>>()
    override val saturnPhotosFlow = _saturnPhotosFlow

    private val _saturnPhotoOperation =
        MutableStateFlow<RefreshOperationStatus>(RefreshOperationStatus.OperationFinished)
    override val saturnPhotoOperation = _saturnPhotoOperation

    override suspend fun populate(): SaturnResult<Unit> {
        return try {
            if(!saturnSettings.isAlreadyPopulated()) {
                val daysOfData = SaturnConfig.DAYS_OF_DATA - 1.days
                val today = timeProvider.getCurrentTime()
                val startDate = timeProvider.getCurrentTime().minus(daysOfData)
                downloadDaysOfData(startDate, today)
                saturnSettings.setAlreadyPopulated()
                SaturnResult.Success(Unit)
            } else SaturnResult.Error(AlreadyPopulatedException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getSaturnPhoto(date: Instant): SaturnResult<SaturnPhoto> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getSaturnPhoto(date.toRealmInstant()))
        } catch (e: NoSuchElementException) {
            SaturnResult.Error(PhotoNotFoundException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getSaturnPhoto(uuid: String): SaturnResult<SaturnPhoto> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getSaturnPhoto(uuid))
        } catch (e: NoSuchElementException) {
            SaturnResult.Error(PhotoNotFoundException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getSaturnPhotos(
        startTime: Instant,
        endTime: Instant
    ): SaturnResult<List<SaturnPhoto>> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhoto>> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getAllSaturnPhotos())
        } catch (e: Exception) {
            SaturnResult.Error(e = e)
        }
    }

    override suspend fun updateSaturnPhoto(uuid: String, isFavorite: Boolean): SaturnResult<SaturnPhoto> {
        return try {
            SaturnResult.Success(saturnPhotoDao.updateSaturnPhoto(uuid,isFavorite))
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<List<SaturnPhoto>> {
        return try {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress)
            val olderSavedPhoto = saturnPhotoDao.getLastSaturnPhoto(Sort.ASCENDING).date.toInstant()
            val newStartTime = olderSavedPhoto.minus(daysOfData.toInt().days)
            val newEndTime = olderSavedPhoto.minus(1.days)
            downloadDaysOfData(newStartTime, newEndTime)
            val listOfSaturnPhotos = saturnPhotoDao.
            getSaturnPhotos(newStartTime.toRealmInstant(), newEndTime.toRealmInstant())
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished)
            SaturnResult.Success(listOfSaturnPhotos)
        } catch (e: Exception) {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished)
            SaturnResult.Error(e)
        }
    }

    override suspend fun refresh(): SaturnResult<Unit> {
        return try {
            val lastSavedPhoto = saturnPhotoDao.getLastSaturnPhoto(Sort.DESCENDING).date.toInstant()
            val today = timeProvider.getCurrentTime()
            if(today.minus(lastSavedPhoto) >= 1.days) {
                downloadDaysOfData(lastSavedPhoto.plus(1.days),today)
            }
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    private suspend fun downloadDaysOfData(startTime: Instant, endTime: Instant){
        val apodModelList = apodService.getPhotoOfDays(startTime.toCommonFormat(), endTime.toCommonFormat())

        val saturnPhotosList = apodModelList.map {
            val saturnResult = it.toSaturnPhoto()
            _saturnPhotosFlow.emit(saturnResult)
            saturnResult
        }

        saturnPhotosList.filterIsInstance<SaturnResult.Error>()
            .map { it.e }
            //.forEach { logError(TAG, it.message.toString()) }

        saturnPhotosList
            .filterIsInstance<SaturnResult.Success<SaturnPhoto>>().map { it.data }
            .forEach {
                saturnPhotoDao.saveSaturnPhoto(it)
            }
    }

    private suspend fun ApodModel.toSaturnPhoto() : SaturnResult<SaturnPhoto> {
        return try {
            val regularUrl =
                if(this.mediaType == "image") this.regularDefinitionUrl else this.thumbnailUrl

            val regularPicture =
                fileManager.savePicture(apodService.downloadPhoto(regularUrl.toString()), this.date.toString())

            val hdPicture = if(this.highDefinitionUrl != null) {
                fileManager.savePicture(apodService.downloadPhoto(this.highDefinitionUrl.toString()), this.date.toString())
            } else regularPicture

            when(regularPicture){
                is SaturnResult.Success ->
                    SaturnResult.Success(SaturnPhoto(
                        date = this.date.toString().toInstant("yyyy-MM-dd").toRealmInstant(),
                        title = this.title.toString(),
                        description = this.explanation.toString(),
                        authors = this.author.toString(),
                        mediaType = this.mediaType.toString(),
                        regularPath = regularPicture.data,
                        highDefinitionPath =
                        if(hdPicture is SaturnResult.Success) hdPicture.data else "",
                        videoUrl = if(this.mediaType == "video") this.regularDefinitionUrl.toString() else "",
                        isFavorite = false
                    ))
                is SaturnResult.Error -> SaturnResult.Error(regularPicture.e)
            }
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }

    }

    companion object {
        private const val TAG = "SATURN REPOSITORY"
    }
}