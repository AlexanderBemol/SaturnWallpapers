package com.amontdevs.saturnwallpapers.android.ui.starting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.model.AlreadyPopulatedException
import com.amontdevs.saturnwallpapers.model.SaturnConfig.DAYS_OF_DATA
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class StartingViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
): ViewModel() {

    private val _startingState = MutableStateFlow(StartingState())
    val startingState: StateFlow<StartingState> = _startingState
    private val stepSize: Int = (100 / DAYS_OF_DATA.inWholeDays).toInt()

    fun initialize() {
        populate()
        viewModelScope.launch {
            saturnPhotosRepository.saturnPhotosFlow.collect{
                val newProgress = _startingState.value.progress + stepSize
                _startingState.value = _startingState.value.copy(progress = newProgress)
                Log.d("StartingViewModel", "Flow: $it")
            }
        }
    }

    private fun populate(){
        viewModelScope.launch {
            when(val result = saturnPhotosRepository.populate()) {
                is SaturnResult.Success -> {
                    _startingState.value = _startingState.value.copy(isLoading = false)
                }
                is SaturnResult.Error -> {
                    Log.d("StartingViewModel", "Populate failure $result")
                    when (result.e){
                        is AlreadyPopulatedException -> refresh()
                        else ->{
                            Log.d("StartingViewModel", "Populate failure $result")
                            _startingState.value = _startingState.value.copy(isLoading = false)
                        }
                    }
                }
            }
        }
    }

    private suspend fun refresh() {
        when (val result = saturnPhotosRepository.refresh()){
            is SaturnResult.Success -> {
                _startingState.value = _startingState.value.copy(isLoading = false)
            }
            is SaturnResult.Error -> {
                Log.d("StartingViewModel", "Refresh failure $result")
            }
        }
    }

}