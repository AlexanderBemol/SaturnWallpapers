package com.amontdevs.saturnwallpapers.repository

import com.amontdevs.saturnwallpapers.model.ApodModel
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.PhotoNotFoundException
import com.amontdevs.saturnwallpapers.model.RefreshOperationStatus
import com.amontdevs.saturnwallpapers.model.SaturnConfig
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMedia
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaStatus
import com.amontdevs.saturnwallpapers.model.SaturnPhotoMediaType
import com.amontdevs.saturnwallpapers.model.SaturnPhotoWithMedia
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.repository.SaturnPhotosRepository.PopulateOperationStatus
import com.amontdevs.saturnwallpapers.source.IAPODService
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.source.ISaturnPhotoDao
import com.amontdevs.saturnwallpapers.source.ISaturnPhotoMediaDao
import com.amontdevs.saturnwallpapers.source.ISettingsSource
import com.amontdevs.saturnwallpapers.source.ITimeProvider
import com.amontdevs.saturnwallpapers.utils.ISaturnLogger
import com.amontdevs.saturnwallpapers.utils.toCommonFormat
import com.amontdevs.saturnwallpapers.utils.toInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.days

interface ISaturnPhotosRepository {
    val saturnPhotosFlow: MutableSharedFlow<SaturnResult<SaturnPhoto>>
    val saturnPhotoOperation: StateFlow<RefreshOperationStatus>
    val operationProgress: StateFlow<Int>
    suspend fun populate(): SaturnResult<PopulateOperationStatus>
    suspend fun getSaturnPhoto(date: Instant): SaturnResult<SaturnPhotoWithMedia>
    suspend fun getSaturnPhoto(id: Long): SaturnResult<SaturnPhotoWithMedia>
    suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhotoWithMedia>>
    suspend fun updateSaturnPhoto(saturnPhoto: SaturnPhoto): SaturnResult<Unit>
    suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<List<SaturnPhoto>>
    suspend fun refresh(): SaturnResult<Unit>
    suspend fun updateMediaQuality(mediaQuality: MediaQuality): SaturnResult<Unit>
}

class SaturnPhotosRepository(
    private val saturnLogger: ISaturnLogger,
    private val apodService: IAPODService,
    private val saturnPhotoDao: ISaturnPhotoDao,
    private val saturnPhotoMediaDao: ISaturnPhotoMediaDao,
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

    override suspend fun populate(): SaturnResult<PopulateOperationStatus> {
        return try {
            if(!saturnSettings.isAlreadyPopulated()) {
                _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress)
                val daysOfData = SaturnConfig.DAYS_OF_DATA - 1.days
                val today = timeProvider.getCurrentTime()
                val startDate = timeProvider.getCurrentTime().minus(daysOfData)
                downloadDaysOfData(startDate, today)
                saturnSettings.setAlreadyPopulated()
                _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished)
                SaturnResult.Success(PopulateOperationStatus.Succeeded)
            } else SaturnResult.Success(PopulateOperationStatus.AlreadyPopulated)
        } catch (e: Exception) {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished)
            SaturnResult.Error(e)
        }
    }

    override suspend fun getSaturnPhoto(date: Instant): SaturnResult<SaturnPhotoWithMedia> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getSaturnPhotoByTimestamp(date.toEpochMilliseconds()))
        } catch (e: NoSuchElementException) {
            SaturnResult.Error(PhotoNotFoundException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getSaturnPhoto(id: Long): SaturnResult<SaturnPhotoWithMedia> {
        return try {
            SaturnResult.Success(saturnPhotoDao.getSaturnPhoto(id))
        } catch (e: NoSuchElementException) {
            SaturnResult.Error(PhotoNotFoundException())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhotoWithMedia>> {
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
                saturnPhotoDao.getAllSaturnPhotos()
                    .map { it.saturnPhoto }
                    .minByOrNull { it.timestamp }
                    !!.timestamp
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
            //pruneOldData()
            //new data to download
            val lastSavedPhoto = Instant.fromEpochMilliseconds(
                saturnPhotoDao.getAllSaturnPhotos()
                    .map { it.saturnPhoto }
                    .maxByOrNull { it.timestamp }
                    !!.timestamp
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
        /*
        saturnPhotoDao.findOldData(validOldestData.toEpochMilliseconds()).forEach {
            if(it.regularPath.isNotEmpty()) fileManager.deletePicture(it.regularPath)
            if(it.highDefinitionPath.isNotEmpty()) fileManager.deletePicture(it.highDefinitionPath)
        }*/
        saturnPhotoDao.deleteOldData(validOldestData.toEpochMilliseconds())
    }

    override suspend fun updateMediaQuality(mediaQuality: MediaQuality): SaturnResult<Unit> {
        return try {
            /*
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
             */
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    private suspend fun downloadDaysOfData(startTime: Instant, endTime: Instant){
        val apodModelList = apodService.getPhotoOfDays(startTime.toCommonFormat(), endTime.toCommonFormat())
        val saturnPhotos = convertAndInsertApodModelList(apodModelList)
        downloadMediaAndUpdate(MediaQuality.NORMAL, saturnPhotos)
    }

    private suspend fun convertAndInsertApodModelList(apodModelList: List<ApodModel>): List<SaturnPhotoWithMedia> {
        val saturnPhotos = apodModelList.map { it.toSaturnPhoto() }

        return withContext(Dispatchers.IO) {
            val insertedIds = saturnPhotoDao.insertSaturnPhoto(*saturnPhotos.toTypedArray())
            saturnPhotos.forEachIndexed { index, saturnPhoto ->
                val media = listOfNotNull(
                    SaturnPhotoMedia(
                        saturnPhotoId = insertedIds[index],
                        mediaType = if (saturnPhoto.isVideo) SaturnPhotoMediaType.VIDEO
                            else SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE,
                        url = if (saturnPhoto.isVideo) apodModelList[index].thumbnailUrl.toString()
                            else apodModelList[index].regularDefinitionUrl.toString(),
                        filepath = "",
                        status = SaturnPhotoMediaStatus.NOT_DOWNLOADED_YET,
                        errorMessage = ""
                    ),
                    if (!saturnPhoto.isVideo) SaturnPhotoMedia(
                        saturnPhotoId = insertedIds[index],
                        mediaType = SaturnPhotoMediaType.HIGH_QUALITY_IMAGE,
                        url = apodModelList[index].highDefinitionUrl.toString(),
                        filepath = "",
                        status = SaturnPhotoMediaStatus.NOT_DOWNLOADED_YET,
                        errorMessage = ""
                    ) else null
                )
                saturnPhotoMediaDao.insert(*media.toTypedArray())
            }
            saturnPhotoDao.getSaturnPhotosWithMediaById(insertedIds)
        }
    }

    private suspend fun downloadMediaAndUpdate(
        quality: MediaQuality,
        saturnPhotosWithMedia: List<SaturnPhotoWithMedia>
    ) {
        val mediaTypes = when (quality) {
            MediaQuality.NORMAL -> listOf(
                SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE,
                SaturnPhotoMediaType.VIDEO
            )
            MediaQuality.HIGH -> listOf(SaturnPhotoMediaType.HIGH_QUALITY_IMAGE)
        }

        withContext(Dispatchers.IO) {
            saturnPhotosWithMedia.forEach { saturnPhotoWithMedia ->
                val mediaToDownload = saturnPhotoWithMedia.mediaList.firstOrNull { mediaTypes.contains(it.mediaType) }
                mediaToDownload?.downloadMedia(saturnPhotoWithMedia.saturnPhoto)
                //update progress
                _operationProgress.emit(
                    (saturnPhotosWithMedia.indexOf(saturnPhotoWithMedia) + 1) * 100 / saturnPhotosWithMedia.size
                )
            }
        }
    }

    private suspend fun SaturnPhotoMedia.downloadMedia(saturnPhoto: SaturnPhoto){
        try {
            val prefix = when(this.mediaType) {
                SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE -> "RQ-"
                SaturnPhotoMediaType.HIGH_QUALITY_IMAGE -> "HQ-"
                SaturnPhotoMediaType.VIDEO -> "VT-"
            }
            val regularByteReadChannel = apodService.downloadPhoto(this.url)
            val savedPicture = fileManager.savePicture(
                regularByteReadChannel,
                prefix+saturnPhoto.timestamp.toInstant().toCommonFormat()
            )
            when(savedPicture){
                is SaturnResult.Success -> {
                    this.filepath = savedPicture.data
                    this.status = SaturnPhotoMediaStatus.DOWNLOADED
                }
                is SaturnResult.Error -> {
                    this.status = SaturnPhotoMediaStatus.ERROR
                    this.errorMessage = savedPicture.e.message.toString()
                }
            }
        } catch (e: Exception) {
            this.status = SaturnPhotoMediaStatus.ERROR
            this.errorMessage = e.message.toString()
        } finally {
            saturnPhotoMediaDao.update(this)
        }
    }

    private fun ApodModel.toSaturnPhoto(): SaturnPhoto {
        return SaturnPhoto(
            timestamp = this.date.toString().toInstant("yyyy-MM-dd").toEpochMilliseconds(),
            title = this.title.toString(),
            description = this.explanation.toString(),
            authors = this.author.toString(),
            isVideo = this.mediaType == "video",
            videoUrl = if(this.mediaType == "video") this.regularDefinitionUrl.toString() else "",
            isFavorite = false
        )
    }

    companion object {
        private const val TAG = "SATURN REPOSITORY"
    }
    enum class PopulateOperationStatus {
        Succeeded, AlreadyPopulated
    }
}