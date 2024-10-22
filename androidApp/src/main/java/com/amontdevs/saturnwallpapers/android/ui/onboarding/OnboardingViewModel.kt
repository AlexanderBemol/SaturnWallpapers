package com.amontdevs.saturnwallpapers.android.ui.onboarding

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amontdevs.saturnwallpapers.android.ui.navigation.ISaturnNavigator
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.android.utils.AnalyticsHelper
import com.amontdevs.saturnwallpapers.model.SaturnResult
import com.amontdevs.saturnwallpapers.model.SaturnSettings
import com.amontdevs.saturnwallpapers.model.UserStatus
import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val saturnPhotosRepository: ISaturnPhotosRepository,
    private val settingsRepository: ISettingsRepository,
    private val navigator: ISaturnNavigator
): ViewModel() {
    private val _onboardingState = MutableStateFlow(OnboardingState())
    val onboardingState: StateFlow<OnboardingState> = _onboardingState

    init {
        AnalyticsHelper.screenView(Navigation.Onboarding)
        val userStatus = when(val result = settingsRepository.getUserStatus()){
            is SaturnResult.Success -> {
                val onboardingStatus = if (!result.data.userOnboarded && result.data.alreadyPopulated)
                    OnboardingStatus.PopulatedNotOnboarded
                else OnboardingStatus.NotOnboarded
                _onboardingState.value = _onboardingState.value.copy(
                    onboardingStatus = onboardingStatus
                )
                result.data
            }
            is SaturnResult.Error -> {
                Log.d("OnboardingViewModel", result.e.message.toString())
                null
            }
        }
        if(userStatus?.userOnboarded == true && userStatus.alreadyPopulated){
            navigator.navigateToPopInclusive(Navigation.Loading, Navigation.Onboarding)
        } else {
            loadOnboarding()
        }
    }

    private fun loadOnboarding(){
        when(val result = settingsRepository.getSettings()){
            is SaturnResult.Success -> {
                _onboardingState.value = _onboardingState.value.copy(
                    isServiceSwitchActivated = result.data.isDailyWallpaperActivated
                )
            }
            is SaturnResult.Error -> Log.d("OnboardingViewModel", result.e.message.toString())
        }
        viewModelScope.launch {
            if(_onboardingState.value.onboardingStatus != OnboardingStatus.NotOnboarded)
                return@launch
            saturnPhotosRepository.saturnPhotoOperation.collect {
                _onboardingState.value = _onboardingState.value.copy(
                    populateProgress = it.progress.toInt()
                )
            }
        }
        viewModelScope.launch {
            if(_onboardingState.value.onboardingStatus != OnboardingStatus.NotOnboarded)
                return@launch
            when(val result = saturnPhotosRepository.populate()){
                is SaturnResult.Success -> {
                    _onboardingState.value = _onboardingState.value.copy(
                        onboardingStatus = OnboardingStatus.PopulatedNotOnboarded
                    )
                }
                is SaturnResult.Error -> {
                    Log.d("OnboardingViewModel", result.e.message.toString())
                }
            }
        }
    }

    fun toggleIsServiceActivated() {
        val settings = SaturnSettings(
            isDailyWallpaperActivated = !_onboardingState.value.isServiceSwitchActivated
        )
        viewModelScope.launch {
            when(val result = settingsRepository.saveSettings(settings)){
                is SaturnResult.Success -> {
                    _onboardingState.value = _onboardingState.value.copy(
                        isServiceSwitchActivated = !_onboardingState.value.isServiceSwitchActivated
                        //!settings.isDailyWallpaperActivated
                    )
                }
                is SaturnResult.Error -> Log.d("OnboardingViewModel", result.e.message.toString())
            }
        }
    }

    fun completeOnboarding() {
        viewModelScope.launch {
            when(val result = settingsRepository.setUserStatus(
                UserStatus(userOnboarded = true, alreadyPopulated = true))
            ){
                is SaturnResult.Success ->
                    navigator.navigateToPopInclusive(Navigation.Home, Navigation.Onboarding)
                is SaturnResult.Error ->
                    Log.d("OnboardingViewModel", result.e.message.toString())
            }
        }
    }

}