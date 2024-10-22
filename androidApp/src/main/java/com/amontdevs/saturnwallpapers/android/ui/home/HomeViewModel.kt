package com.amontdevs.saturnwallpapers.android.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.android.utils.AnalyticsHelper
import com.amontdevs.saturnwallpapers.android.utils.WorkerHelper
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import com.amontdevs.saturnwallpapers.source.ITimeProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
    private val timeProvider: ITimeProvider,
    private val settingsRepository: ISettingsRepository,
    private val workManager: WorkManager
): ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    fun loadHomeData() {
        AnalyticsHelper.screenView(Navigation.Home)
        viewModelScope.launch {
            val result = saturnPhotosRepository.getSaturnPhoto(timeProvider.getCurrentTime())
            when (result) {
                is SaturnResult.Success -> {
                    _homeState.value = _homeState.value.copy(saturnPhoto = result.data)
                    Log.d("HomeViewModel", "Updated today's photo")
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.getAllSaturnPhotos()) {
                is SaturnResult.Success -> {
                    _homeState.value = _homeState.value.copy(
                        favoritePhotos = result.data
                            .filter { it.saturnPhoto.isFavorite }
                            .sortedByDescending { it.saturnPhoto.timestamp }
                    )
                    Log.d("HomeViewModel", "Updated favorites photos")
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
        viewModelScope.launch {
            when (val result = settingsRepository.getSettings()) {
                is SaturnResult.Success -> {
                    if (result.data.isDailyWallpaperActivated) {
                        WorkerHelper.setWorker(workManager, WorkerHelper.SaturnWorker.DAILY_WORKER)
                    }
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.areDownloadsNeeded()) {
                is SaturnResult.Success -> {
                    if (result.data) {
                        WorkerHelper.setWorker(workManager, WorkerHelper.SaturnWorker.DOWNLOADER_WORKER)
                    } else {
                        Log.d("HomeViewModel", "Not downloads needed")
                    }
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
    }

}