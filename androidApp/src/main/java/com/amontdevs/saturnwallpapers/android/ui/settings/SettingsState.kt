package com.amontdevs.saturnwallpapers.android.ui.settings

import com.amontdevs.saturnwallpapers.model.SaturnSettings

data class SettingsState(
    val settings: SaturnSettings = SaturnSettings(),
    val confirmQuality: ConfirmQualityState = ConfirmQualityState()
)

data class ConfirmQualityState(
    val display: Boolean = false,
    val title: String = "",
    val message: String = "",
    val loadingTitle: String = ""
)