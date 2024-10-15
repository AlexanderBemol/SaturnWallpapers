package com.amontdevs.saturnwallpapers.android.ui.onboarding

data class OnboardingState(
    val isServiceSwitchActivated: Boolean = false,
    val onboardingStatus: OnboardingStatus = OnboardingStatus.NotOnboarded,
    val populateProgress: Int = 0
)

enum class OnboardingStatus {
    NotOnboarded, PopulatedNotOnboarded
}
