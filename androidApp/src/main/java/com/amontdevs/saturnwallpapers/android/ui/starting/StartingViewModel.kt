package com.amontdevs.saturnwallpapers.android.ui.starting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.model.AlreadyPopulatedException
import com.amontdevs.saturnwallpapers.model.SaturnConfig.DAYS_OF_DATA
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.SaturnPhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class StartingViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
): ViewModel() {

    private val _startingState = MutableStateFlow(StartingState())
    val startingState = _startingState.asStateFlow()

    init {
        initialize()
    }

    private fun initialize() {
        viewModelScope.launch {
            when (val result = saturnPhotosRepository.refresh(false)){
                is SaturnResult.Success -> {
                    _startingState.value = _startingState.value.copy(isLoading = false)
                }
                is SaturnResult.Error -> {
                    Log.d("StartingViewModel", "Refresh failure $result")
                }
            }
        }
    }

}