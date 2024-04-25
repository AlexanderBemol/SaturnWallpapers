package com.amontdevs.saturnwallpapers.android.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.SettingsMenuOptions
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: ISettingsRepository
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState

    fun loadSettingsState() {
        when(val result = settingsRepository.getSettings()){
            is SaturnResult.Success -> {
                _settingsState.value = _settingsState.value.copy(settings = result.data)
            }
            is SaturnResult.Error -> {
                Log.e("SettingsViewModel", "Error loading settings: ${result.e}")
            }
        }
    }

    fun toggleDailyWallpaperUpdater() {
        viewModelScope.launch {
            when(val result = settingsRepository.saveSettings(
                _settingsState.value.settings.copy(
                    isDailyWallpaperActivated = !_settingsState.value.settings.isDailyWallpaperActivated
                ))
            ){
                is SaturnResult.Success -> {
                    _settingsState.value = _settingsState.value.copy(
                        settings = _settingsState.value.settings.copy(
                            isDailyWallpaperActivated = !_settingsState.value.settings.isDailyWallpaperActivated
                        )
                    )
                }
                is SaturnResult.Error -> {
                    Log.e("SettingsViewModel", "Error updating settings: ${result.e}")
                }
            }
        }
    }

    fun changeDropDownOption(settingsMenuOption: SettingsMenuOptions) {
        val settings = when(settingsMenuOption){
            is MediaQuality -> _settingsState.value.settings.copy(
                mediaQuality = settingsMenuOption
            )
            is WallpaperScreen -> _settingsState.value.settings.copy(
                wallpaperScreen = settingsMenuOption
            )
            is DataMaxAge -> _settingsState.value.settings.copy(
                dataMaxAge = settingsMenuOption
            )
            is DefaultSaturnPhoto -> _settingsState.value.settings.copy(
                defaultSaturnPhoto = settingsMenuOption
            )
            else -> _settingsState.value.settings
        }
        viewModelScope.launch {
            when(val result = settingsRepository.saveSettings(settings)){
                is SaturnResult.Success -> {
                    _settingsState.value = _settingsState.value.copy(
                        settings = settings
                    )
                }
                is SaturnResult.Error -> {
                    Log.e("SettingsViewModel", "Error updating settings: ${result.e}")
                }
            }
        }
    }




}