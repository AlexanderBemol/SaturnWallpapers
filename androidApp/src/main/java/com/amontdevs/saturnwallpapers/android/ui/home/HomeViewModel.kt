package com.amontdevs.saturnwallpapers.android.ui.home

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val settingsRepository: ISettingsRepository
): ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    fun loadHomeData(context: Context) {
        viewModelScope.launch {
            val result = saturnPhotosRepository.getSaturnPhoto(timeProvider.getCurrentTime())
            when (result) {
                is SaturnResult.Success -> {
                    _homeState.value = _homeState.value.copy(saturnPhoto = result.data)
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.getAllSaturnPhotos()) {
                is SaturnResult.Success -> {
                    _homeState.value = _homeState.value.copy(favoritePhotos = result.data.filter { it.isFavorite })
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
        viewModelScope.launch {
            when (val result = settingsRepository.getSettings()) {
                is SaturnResult.Success -> {
                    if (result.data.isDailyWallpaperActivated) WorkerHelper.setWorker(context)
                }
                is SaturnResult.Error -> {
                    Log.d("HomeViewModel", "Error: ${result.e}")
                }
            }
        }
    }

}