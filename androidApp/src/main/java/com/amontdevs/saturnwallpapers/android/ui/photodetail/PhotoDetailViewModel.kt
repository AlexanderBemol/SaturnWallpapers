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
    private val photoId: Long,
    private val sharedImageKey: String
): ViewModel() {
    private val _fullViewState = MutableStateFlow(PhotoDetailState())
    val fullViewState: StateFlow<PhotoDetailState> = _fullViewState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.getSaturnPhoto(photoId)) {
                is SaturnResult.Success -> {
                    _fullViewState.value = _fullViewState.value.copy(
                        saturnPhoto = result.data,
                        sharedKey = sharedImageKey
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
            val saturnPhotoWithMedia = _fullViewState.value
                .saturnPhoto!!.copy()
                .apply { saturnPhoto = saturnPhoto.copy(isFavorite = !saturnPhoto.isFavorite) }
            when (
                val result = saturnPhotosRepository.updateSaturnPhoto(saturnPhotoWithMedia.saturnPhoto)
            ) {
                is SaturnResult.Success -> {
                    _fullViewState.value =
                        _fullViewState.value.copy(saturnPhoto = saturnPhotoWithMedia)
                }
                is SaturnResult.Error -> {
                    Log.d("PhotoDetailViewModel", "Error: ${result.e}")
                }
            }
        }
    }
}