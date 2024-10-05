package com.amontdevs.saturnwallpapers.android.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkManager
import com.amontdevs.saturnwallpapers.android.utils.WorkerHelper
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.SaturnSettings
import com.amontdevs.saturnwallpapers.model.SettingsMenuOptions
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: ISettingsRepository,
    private val saturnPhotosRepository: ISaturnPhotosRepository,
    private val workManager: WorkManager
) : ViewModel() {

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState

    private var settingsToConfirm = settingsState.value.settings

    init {
        loadSettingsState()
    }

    private fun loadSettingsState() {
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
        val newSettingsState = _settingsState.value.settings.copy(
            isDailyWallpaperActivated = !_settingsState.value.settings.isDailyWallpaperActivated
        )
        viewModelScope.launch {
            when(val result = settingsRepository.saveSettings(newSettingsState)){
                is SaturnResult.Success -> {
                    try {
                        if(newSettingsState.isDailyWallpaperActivated){
                            WorkerHelper.setWorker(workManager)
                        } else {
                            WorkerHelper.stopWorker(workManager)
                        }
                        _settingsState.value = _settingsState.value.copy(
                            settings = _settingsState.value.settings.copy(
                                isDailyWallpaperActivated = !_settingsState.value.settings.isDailyWallpaperActivated
                            )
                        )
                    } catch (e: Exception) {
                        Log.e("SettingsViewModel", "Error updating worker: ${e}")
                    }
                }
                is SaturnResult.Error -> {
                    Log.e("SettingsViewModel", "Error updating settings: ${result.e}")
                }
            }
        }
    }

    fun toggleDownloadOverCellular() {
        settingsToConfirm = _settingsState.value.settings.copy(
            isDownloadOverCellularActivated = !_settingsState.value.settings.isDownloadOverCellularActivated
        )
        if(settingsToConfirm.isDownloadOverCellularActivated) {
            _settingsState.value = _settingsState.value.copy(
                confirm = ConfirmState(
                    display = true,
                    title = "Are you sure?",
                    message = "Images download process can consume a lot of data if high quality is selected.",
                    loadingTitle = "",
                    optionToConfirm = OptionToConfirm.DownloadOverCellular
                )
            )
        } else {
            confirmDownloadOverCellular()
        }
    }

    fun confirmDownloadOverCellular() {
        viewModelScope.launch {
            when(val result = settingsRepository.saveSettings(settingsToConfirm)){
                is SaturnResult.Success -> {
                    _settingsState.value = _settingsState.value.copy(
                        confirm = ConfirmState(display = false),
                        settings = settingsToConfirm
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
            is MediaQuality -> {
                _settingsState.value = _settingsState.value.copy(
                    confirm = ConfirmState(
                        display = true,
                        title = "Are you sure?",
                        message = if(settingsMenuOption == MediaQuality.HIGH)
                            "All the images will be downloaded at high quality, this operation can " +
                                    "take some time depending on your internet connection, do you want to continue?"
                        else "All the high quality images are going to be removed",
                        loadingTitle = if(settingsMenuOption == MediaQuality.HIGH)
                            "Downloading high quality images"
                            else "Removing high quality images",
                        optionToConfirm = OptionToConfirm.MediaQuality
                    )
                )
                settingsToConfirm = settingsState.value.settings.copy(
                    mediaQuality = settingsMenuOption
                )
                _settingsState.value.settings
            }
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
        if (settingsMenuOption !is MediaQuality) {
            saveDropDownOption(settings)
        }
    }

    fun confirmQualityChangeOperation() {
        viewModelScope.launch {
            when(val result = saturnPhotosRepository.updateMediaQuality(settingsToConfirm.mediaQuality)){
                is SaturnResult.Success -> {
                    _settingsState.value = _settingsState.value.copy(
                        confirm = ConfirmState(display = false)
                    )
                    saveDropDownOption(settingsToConfirm)
                }
                is SaturnResult.Error -> {
                    Log.e("SettingsViewModel", "Error updating media quality: ${result.e}")
                    _settingsState.value = _settingsState.value.copy(
                        confirm = ConfirmState(display = false)
                    )
                }
            }
        }
    }

    fun cancelSettingChangeOperation() {
        _settingsState.value = _settingsState.value.copy(
            confirm = ConfirmState(display = false)
        )
    }

    private fun saveDropDownOption(saturnSettings: SaturnSettings){
        viewModelScope.launch {
            viewModelScope.launch {
                when(val result = settingsRepository.saveSettings(saturnSettings)){
                    is SaturnResult.Success -> {
                        _settingsState.value = _settingsState.value.copy(
                            settings = saturnSettings
                        )
                    }
                    is SaturnResult.Error -> {
                        Log.e("SettingsViewModel", "Error updating settings: ${result.e}")
                    }
                }
            }
        }
    }




}