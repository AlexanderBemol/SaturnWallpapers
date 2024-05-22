package com.amontdevs.saturnwallpapers.repository

import com.amontdevs.saturnwallpapers.model.AlreadyPopulatedException
import com.amontdevs.saturnwallpapers.model.ApodModel
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.MediaQuality
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
import com.amontdevs.saturnwallpapers.utils.ISaturnLogger
import com.amontdevs.saturnwallpapers.utils.SaturnLogger
import com.amontdevs.saturnwallpapers.utils.toCommonFormat
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.flow.*
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

interface ISaturnPhotosRepository {
    val saturnPhotosFlow: MutableSharedFlow<SaturnResult<SaturnPhoto>>
    val saturnPhotoOperation: StateFlow<RefreshOperationStatus>
    val operationProgress: StateFlow<Int>
    suspend fun populate(): SaturnResult<Unit>
    suspend fun getSaturnPhoto(date: Instant): SaturnResult<SaturnPhoto>
    suspend fun getSaturnPhoto(id: Int): SaturnResult<SaturnPhoto>
    suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhoto>>
    suspend fun updateSaturnPhoto(saturnPhoto: SaturnPhoto): SaturnResult<Unit>
    suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<List<SaturnPhoto>>
    suspend fun refresh(): SaturnResult<Unit>
    suspend fun updateMediaQuality(mediaQuality: MediaQuality): SaturnResult<Unit>
}

class SaturnPhotosRepository(
    private val saturnLogger: ISaturnLogger,
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

    private val _operationProgress = MutableStateFlow(0)
    override val operationProgress = _operationProgress

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
            SaturnResult.Success(saturnPhotoDao.getSaturnPhoto(date.toEpochMilliseconds()))
        } catch (e: NoSuchElementException) {
            SaturnResult.Error(PhotoNotFoundException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getSaturnPhoto(id: Int): SaturnResult<SaturnPhoto> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getSaturnPhoto(id))
        } catch (e: NoSuchElementException) {
            SaturnResult.Error(PhotoNotFoundException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhoto>> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getAllSaturnPhotos())
        } catch (e: Exception) {
            SaturnResult.Error(e = e)
        }
    }

    override suspend fun updateSaturnPhoto(saturnPhoto: SaturnPhoto): SaturnResult<Unit> {
        return try {
            SaturnResult.Success(saturnPhotoDao.updateSaturnPhoto(saturnPhoto))
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<List<SaturnPhoto>> {
        return try {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress)
            val olderSavedPhoto = Instant.fromEpochMilliseconds(
                saturnPhotoDao.getAllSaturnPhotos().minByOrNull { it.timestamp }!!.timestamp
            )
            val newStartTime = olderSavedPhoto.minus(daysOfData.toInt().days)
            val newEndTime = olderSavedPhoto.minus(1.days)
            downloadDaysOfData(newStartTime, newEndTime)
            val listOfSaturnPhotos = saturnPhotoDao.
            getSaturnPhotos(newStartTime.toEpochMilliseconds(), newEndTime.toEpochMilliseconds())
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished)
            SaturnResult.Success(listOfSaturnPhotos)
        } catch (e: Exception) {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished)
            SaturnResult.Error(e)
        }
    }

    override suspend fun refresh(): SaturnResult<Unit> {
        return try {
            //remove old data
            pruneOldData()
            //new data to download
            val lastSavedPhoto = Instant.fromEpochMilliseconds(
                saturnPhotoDao.getAllSaturnPhotos().maxByOrNull { it.timestamp }!!.timestamp
            )
            val today = timeProvider.getCurrentTime()
            if(today.minus(lastSavedPhoto) >= 1.days) {
                downloadDaysOfData(lastSavedPhoto.plus(1.days),today)
            }
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    private suspend fun pruneOldData() {
        val currentTime = timeProvider.getCurrentTime()
        val validOldestData = when(saturnSettings.getSettings().dataMaxAge) {
            DataMaxAge.ONE_MONTH -> currentTime.minus(30.days)
            DataMaxAge.THREE_MONTHS -> currentTime.minus(90.days)
            DataMaxAge.SIX_MONTHS -> currentTime.minus(180.days)
            DataMaxAge.ONE_YEAR -> currentTime.minus(365.days)
        }
        saturnPhotoDao.deleteOldData(validOldestData.toEpochMilliseconds())
    }

    override suspend fun updateMediaQuality(mediaQuality: MediaQuality): SaturnResult<Unit> {
        return try {
            _operationProgress.emit(0)
            val saturnPhotos = saturnPhotoDao.getAllSaturnPhotos()
            when(mediaQuality){
                MediaQuality.NORMAL -> {
                    //Remove all high quality photos
                    saturnLogger.logMessage(TAG, "Removing all high quality photos")
                    saturnPhotos.forEach {
                        if(it.mediaType != "image") return@forEach
                        fileManager.deletePicture(it.highDefinitionPath)
                        it.highDefinitionPath = ""
                    }
                    saturnPhotoDao.updateSaturnPhoto(*saturnPhotos.toTypedArray())
                }
                MediaQuality.HIGH -> {
                    //Download all stored photos with high quality
                    saturnLogger.logMessage(TAG, "Downloading all high quality photos")
                    saturnPhotos.forEachIndexed { index, it ->
                        if(it.mediaType != "image") return@forEachIndexed
                        val hqPath = fileManager.savePicture(
                            apodService.downloadPhoto(it.highDefinitionUrl),
                            it.timestamp.toInstant().toCommonFormat()
                        )
                        saturnLogger.logMessage(TAG, "High quality photo downloaded: $index")
                        when(hqPath) {
                            is SaturnResult.Success -> it.highDefinitionPath = hqPath.data
                            is SaturnResult.Error -> {
                                it.highDefinitionPath
                                saturnLogger.logError(TAG,hqPath.e, hqPath.e.message.toString())
                            }
                        }
                    }
                    saturnPhotoDao.updateSaturnPhoto(*saturnPhotos.toTypedArray())
                }
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
                saturnPhotoDao.insertSaturnPhoto(it)
            }
    }

    private suspend fun ApodModel.toSaturnPhoto() : SaturnResult<SaturnPhoto> {
        return try {
            val isHQActivated = saturnSettings.getSettings().mediaQuality == MediaQuality.HIGH
            val regularUrl =
                if(this.mediaType == "image") this.regularDefinitionUrl else this.thumbnailUrl

            val regularPicture =
                fileManager.savePicture(apodService.downloadPhoto(regularUrl.toString()), this.date.toString())

            val hdPicture = if(isHQActivated && this.highDefinitionUrl != null) {
                fileManager.savePicture(apodService.downloadPhoto(this.highDefinitionUrl.toString()), this.date.toString())
            } else regularPicture

            when(regularPicture){
                is SaturnResult.Success ->
                    SaturnResult.Success(SaturnPhoto(
                        timestamp = this.date.toString().toInstant("yyyy-MM-dd").toEpochMilliseconds(),
                        title = this.title.toString(),
                        description = this.explanation.toString(),
                        authors = this.author.toString(),
                        mediaType = this.mediaType.toString(),
                        regularUrl = this.regularDefinitionUrl.toString(),
                        highDefinitionUrl = this.highDefinitionUrl.toString(),
                        regularPath = regularPicture.data,
                        highDefinitionPath =
                        if(hdPicture is SaturnResult.Success && isHQActivated && this.mediaType == "image") hdPicture.data else "",
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