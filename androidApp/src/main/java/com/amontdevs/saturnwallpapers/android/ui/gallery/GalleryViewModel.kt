package com.amontdevs.saturnwallpapers.android.ui.gallery

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.model.RefreshOperationStatus
import com.amontdevs.saturnwallpapers.model.SaturnPhoto
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GalleryViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
    private val filterByFav: Boolean = false,
): ViewModel() {

    private var wholeSaturnList = mutableListOf<SaturnPhoto>()
    private val _galleryState = MutableStateFlow(GalleryState())
    val galleryState: StateFlow<GalleryState>  = _galleryState

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _galleryState.value = _galleryState.value.copy(isLoaded = false)
            when(val result = saturnPhotosRepository.getAllSaturnPhotos()) {
                is SaturnResult.Success -> {
                    Log.d("GalleryViewModel", result.data.toString())
                    wholeSaturnList = result.data.sortedByDescending { it.timestamp }.toMutableList()
                    if(filterByFav) {
                        _galleryState.value = _galleryState.value.copy(
                            areFiltersVisible = true,
                            isFavoriteSelected = true
                        )
                    }
                    sortAndFilterList()
                }
                is SaturnResult.Error -> {
                    Log.d("GalleryViewModel", result.e.message.toString())
                }
            }
        }
    }

    fun sortAndFilter(toggleFilterByFav: Boolean = false, toggleAscSort: Boolean = false) {
        _galleryState.value = _galleryState.value.copy(
            isAscSortSelected = if(toggleAscSort) !_galleryState.value.isAscSortSelected
                else _galleryState.value.isAscSortSelected,
            isFavoriteSelected = if(toggleFilterByFav) !_galleryState.value.isFavoriteSelected
            else _galleryState.value.isFavoriteSelected
        )
        sortAndFilterList()
    }

    fun toggleFiltersVisibility() {
        _galleryState.value = _galleryState.value.copy(
            areFiltersVisible = !_galleryState.value.areFiltersVisible
        )
    }

    fun onBottomScroll() {
        if(saturnPhotosRepository.saturnPhotoOperation.value is RefreshOperationStatus.OperationFinished
            && !_galleryState.value.isAscSortSelected && !_galleryState.value.isFavoriteSelected) {
            _galleryState.value = _galleryState.value.copy(isFetchingPhotos = true)
            viewModelScope.launch {
                when (val result = saturnPhotosRepository.populateAndGetPastDays(2u)){
                    is SaturnResult.Success -> {
                        wholeSaturnList += result.data
                        _galleryState.value = _galleryState.value.copy(isFetchingPhotos = false)
                        sortAndFilterList()
                    }
                    is SaturnResult.Error -> {
                        _galleryState.value = _galleryState.value.copy(isFetchingPhotos = false)
                        Log.d("GalleryViewModel", result.e.message.toString())
                    }
                }
            }
        } else {
            Log.d("GalleryViewModel", "Cannot load more photos")
        }
    }

    private fun sortAndFilterList(){
        val filteredList = if(!_galleryState.value.isFavoriteSelected) wholeSaturnList
        else wholeSaturnList.filter { it.isFavorite }
        val orderedList = if(_galleryState.value.isAscSortSelected) filteredList.sortedBy { it.timestamp }
        else filteredList.sortedByDescending { it.timestamp }

        _galleryState.value = _galleryState.value.copy(
            saturnPhotos = orderedList,
            isLoaded = true
        )
    }

}