package com.amontdevs.saturnwallpapers.android.ui.photodetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhotoDetailViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
    private val settingsRepository: ISettingsRepository,
    private val photoId: Long
): ViewModel() {
    private val _fullViewState = MutableStateFlow(PhotoDetailState())
    val fullViewState: StateFlow<PhotoDetailState> = _fullViewState

    fun loadData() {
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.getSaturnPhoto(photoId)) {
                is SaturnResult.Success -> {
                    _fullViewState.value = _fullViewState.value.copy(
                        saturnPhoto = result.data
                    )
                }
                is SaturnResult.Error -> {
                    Log.d("PhotoDetailViewModel", "Error: ${result.e}")
                }
            }
        }
        viewModelScope.launch {
            when (val result = settingsRepository.getSettings()) {
                is SaturnResult.Success -> {
                    _fullViewState.value = _fullViewState.value.copy(
                        isHighQuality = result.data.mediaQuality == MediaQuality.HIGH
                    )
                }
                is SaturnResult.Error -> {
                    Log.d("PhotoDetailViewModel", "Error: ${result.e}")
                }
            }
        }
    }

    fun onFavoriteClick() {
        viewModelScope.launch {
            val saturnPhoto = _fullViewState.value.saturnPhoto!!.copy().apply { saturnPhoto.isFavorite = !saturnPhoto.isFavorite }
            when (
                val result = saturnPhotosRepository.updateSaturnPhoto(saturnPhoto.saturnPhoto)
            ) {
                is SaturnResult.Success -> {
                    _fullViewState.value = fullViewState.value.copy(saturnPhoto = saturnPhoto)
                }
                is SaturnResult.Error -> {
                    Log.d("PhotoDetailViewModel", "Error: ${result.e}")
                }
            }
        }
    }
}