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
import kotlinx.datetime.daysUntil
import kotlin.time.Duration.Companion.days

interface ISaturnPhotosRepository {
    val saturnPhotosFlow: MutableSharedFlow<SaturnPhotoWithMedia>
    val saturnPhotoOperation: StateFlow<RefreshOperationStatus>
    suspend fun populate(): SaturnResult<PopulateOperationStatus>
    suspend fun getSaturnPhoto(date: Instant): SaturnResult<SaturnPhotoWithMedia>
    suspend fun getSaturnPhoto(id: Long): SaturnResult<SaturnPhotoWithMedia>
    suspend fun getAllSaturnPhotos(): SaturnResult<List<SaturnPhotoWithMedia>>
    suspend fun updateSaturnPhoto(saturnPhoto: SaturnPhoto): SaturnResult<Unit>
    suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<Unit>
    suspend fun refresh(downloadHQToday: Boolean): SaturnResult<Unit>
    suspend fun areDownloadsNeeded(): SaturnResult<Boolean>
    suspend fun downloadNotDownloadedPhotos(): SaturnResult<Unit>
    suspend fun deleteHighQualityPhotos(): SaturnResult<Unit>
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

    private val _saturnPhotosFlow = MutableSharedFlow<SaturnPhotoWithMedia>()
    override val saturnPhotosFlow = _saturnPhotosFlow

    private val _saturnPhotoOperation =
        MutableStateFlow<RefreshOperationStatus>(RefreshOperationStatus.OperationFinished())
    override val saturnPhotoOperation = _saturnPhotoOperation

    override suspend fun populate(): SaturnResult<PopulateOperationStatus> {
        return try {
            val userStatus = saturnSettings.getUserStatus()
            if(!userStatus.alreadyPopulated) {
                _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress(0.00))
                val daysOfData = SaturnConfig.DAYS_OF_DATA - 1.days
                val today = timeProvider.getCurrentTime()
                val startDate = timeProvider.getCurrentTime().minus(daysOfData)
                downloadDaysOfData(startDate, today)
                saturnSettings.setUserStatus(userStatus.copy(alreadyPopulated = true))
                _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished(100.00))
                SaturnResult.Success(PopulateOperationStatus.Succeeded)
            } else SaturnResult.Success(PopulateOperationStatus.AlreadyPopulated)
        } catch (e: Exception) {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished())
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

    override suspend fun populateAndGetPastDays(daysOfData: UInt): SaturnResult<Unit> {
        return try {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress())
            val missingDays = getPendingDays(saturnPhotoDao.getAllSaturnPhotos(), daysOfData)
            if (missingDays.isNotEmpty()) {
                downloadDaysOfData(missingDays)
            } else {
                val olderSavedPhoto = Instant.fromEpochMilliseconds(
                    saturnPhotoDao.getAllSaturnPhotos()
                        .map { it.saturnPhoto }
                        .minByOrNull { it.timestamp }
                    !!.timestamp
                )
                val newStartTime = olderSavedPhoto.minus(daysOfData.toInt().days)
                val newEndTime = olderSavedPhoto.minus(1.days)
                downloadDaysOfData(newStartTime, newEndTime)
            }
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished())
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished())
            SaturnResult.Error(e)
        }
    }

    private fun getPendingDays(allPhotos: List<SaturnPhotoWithMedia>, daysToGet: UInt): List<String> {
        val today = timeProvider.getCurrentTime()
        val lastSavedPhoto = allPhotos
            .map { it.saturnPhoto }
            .minByOrNull { it.timestamp }
            ?.timestamp
            ?.let { Instant.fromEpochMilliseconds(it) }
            ?:today

        val expectedDays = lastSavedPhoto.daysUntil(today, timeProvider.timeZone).let { days ->
            if(days == 0) listOf(today.toCommonFormat(), lastSavedPhoto.toCommonFormat())
            else {
                (0..days).map {
                    today.minus(it.days).toCommonFormat()
                }
            }
        }
        val currentDays = allPhotos.sortedByDescending { it.saturnPhoto.timestamp }
            .map { it.saturnPhoto.timestamp.toInstant().toCommonFormat() }

        return expectedDays.filterNot { currentDays.contains(it) }.take(daysToGet.toInt()).sortedBy {
            it.toInstant("yyyy-MM-dd")
        }
    }

    override suspend fun refresh(downloadHQToday: Boolean): SaturnResult<Unit> {
        return try {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress())
            //remove old data
            pruneOldData()
            //new data to download
            val lastSavedPhoto = Instant.fromEpochMilliseconds(
                saturnPhotoDao.getAllSaturnPhotos()
                    .map { it.saturnPhoto }
                    .maxByOrNull { it.timestamp }
                    !!.timestamp
            )
            val today = timeProvider.getCurrentTime()
            if(today.minus(lastSavedPhoto) >= 1.days) {
                downloadDaysOfData(lastSavedPhoto.plus(1.days),today, downloadHQToday)
            }
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished())
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun areDownloadsNeeded(): SaturnResult<Boolean> {
        return try {
            SaturnResult.Success(getNotDownloadedPhotos().isNotEmpty())
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun downloadNotDownloadedPhotos(): SaturnResult<Unit> {
        return try {
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationInProgress(0.0))
            val notDownloadedPhotos = getNotDownloadedPhotos()
            val isHQEnabled = saturnSettings.getSettings().mediaQuality == MediaQuality.HIGH
            downloadMediaAndUpdate(notDownloadedPhotos, if(isHQEnabled) null else MediaQuality.NORMAL)
            _saturnPhotoOperation.emit(RefreshOperationStatus.OperationFinished())
            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    override suspend fun deleteHighQualityPhotos(): SaturnResult<Unit> {
        return try {
            val hqMedia = saturnPhotoDao.getAllSaturnPhotos()
                .flatMap { it.mediaList }
                .filter { it.mediaType == SaturnPhotoMediaType.HIGH_QUALITY_IMAGE }

            hqMedia.forEach {
               it.deleteMedia()
            }

            SaturnResult.Success(Unit)
        } catch (e: Exception) {
            SaturnResult.Error(e)
        }
    }

    private suspend fun getNotDownloadedPhotos() =
        saturnPhotoDao.getAllSaturnPhotos()
            .filter { it.mediaList.any { media -> media.status != SaturnPhotoMediaStatus.DOWNLOADED } }


    private suspend fun pruneOldData() {
        val currentTime = timeProvider.getCurrentTime()
        val validOldestData = when(saturnSettings.getSettings().dataMaxAge) {
            DataMaxAge.TWO_WEEKS -> currentTime.minus(14.days)
            DataMaxAge.ONE_MONTH -> currentTime.minus(30.days)
            DataMaxAge.THREE_MONTHS -> currentTime.minus(90.days)
            DataMaxAge.SIX_MONTHS -> currentTime.minus(180.days)
        }
        val dataToDelete = saturnPhotoDao.findOldData(validOldestData.toEpochMilliseconds())
        dataToDelete.forEach { saturnPhotoWithMedia ->
            saturnPhotoWithMedia.mediaList
                .filter { it.status == SaturnPhotoMediaStatus.DOWNLOADED }
                .forEach { media ->
                    media.deleteMedia()
                }
            saturnPhotoMediaDao.delete(*saturnPhotoWithMedia.mediaList.toTypedArray())
        }
        saturnPhotoDao.deleteOldData(validOldestData.toEpochMilliseconds())
    }

    private suspend fun downloadDaysOfData(startTime: Instant, endTime: Instant, downloadHQToday: Boolean = false){
        val apodModelList = apodService.getPhotoOfDays(startTime.toCommonFormat(), endTime.toCommonFormat())
        val saturnPhotos = convertAndInsertApodModelList(apodModelList)
        downloadMediaAndUpdate(saturnPhotos, MediaQuality.NORMAL, true, downloadHQToday)
    }

    private suspend fun downloadDaysOfData(missingDays: List<String>){
        val apodModelList = apodService.getPhotoOfDays(missingDays.first(), missingDays.last())
        val saturnPhotos = convertAndInsertApodModelList(apodModelList)
        downloadMediaAndUpdate(saturnPhotos, MediaQuality.NORMAL, true)
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
        saturnPhotosWithMedia: List<SaturnPhotoWithMedia>,
        quality: MediaQuality? = null,
        publishPhotos: Boolean = false,
        downloadHQToday: Boolean = false
    ) {
        val mediaTypes = when (quality) {
            MediaQuality.NORMAL -> listOf(
                SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE,
                SaturnPhotoMediaType.VIDEO
            )
            MediaQuality.HIGH -> listOf(SaturnPhotoMediaType.HIGH_QUALITY_IMAGE)
            else -> listOf(
                SaturnPhotoMediaType.REGULAR_QUALITY_IMAGE,
                SaturnPhotoMediaType.VIDEO,
                SaturnPhotoMediaType.HIGH_QUALITY_IMAGE
            )
        }

        withContext(Dispatchers.IO) {
            var i = 1
            saturnPhotosWithMedia.sortedByDescending { it.saturnPhoto.timestamp }.forEach {
                saturnPhotoWithMedia ->

                val mediaToDownload = if (i == 1 && downloadHQToday) {
                    saturnPhotoWithMedia.mediaList
                        .filter { it.status != SaturnPhotoMediaStatus.DOWNLOADED }
                } else {
                    saturnPhotoWithMedia.mediaList
                        .filter { mediaTypes.contains(it.mediaType)
                                && it.status != SaturnPhotoMediaStatus.DOWNLOADED
                        }
                }
                mediaToDownload.forEach {
                    it.downloadMedia(saturnPhotoWithMedia.saturnPhoto)
                }
                //update progress
                _saturnPhotoOperation.emit(
                    RefreshOperationStatus.OperationInProgress(i * 100 / saturnPhotosWithMedia.size.toDouble())
                )
                if(publishPhotos){
                    _saturnPhotosFlow.emit(saturnPhotoWithMedia)
                }
                i++
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

    private suspend fun SaturnPhotoMedia.deleteMedia(){
        try {
            withContext(Dispatchers.IO){
                fileManager.deletePicture(this@deleteMedia.filepath)
            }
            filepath = ""
            status = SaturnPhotoMediaStatus.DELETED
        } catch (e: Exception) {
            errorMessage = e.message.toString()
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