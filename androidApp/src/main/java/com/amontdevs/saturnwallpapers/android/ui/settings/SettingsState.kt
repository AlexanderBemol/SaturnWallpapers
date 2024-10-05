package com.amontdevs.saturnwallpapers.android.ui.settings

import com.amontdevs.saturnwallpapers.model.SaturnSettings

data class SettingsState(
    val settings: SaturnSettings = SaturnSettings(),
    val confirm: ConfirmState = ConfirmState()
)

data class ConfirmState(
    val display: Boolean = false,
    val title: String = "",
    val message: String = "",
    val loadingTitle: String = "",
    val optionToConfirm: OptionToConfirm = OptionToConfirm.MediaQuality
)

enum class OptionToConfirm {
    MediaQuality, DownloadOverCellular
}