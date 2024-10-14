package com.amontdevs.saturnwallpapers.android.ui.settings

import com.amontdevs.saturnwallpapers.model.SaturnSettings

data class SettingsState(
    val settings: SaturnSettings = SaturnSettings(),
    val dialogState: DialogState = DialogState.HideDialog,
    val listeningState: ListeningState = ListeningState.NOT_LISTENING
)

data class ConfirmState(
    val display: Boolean = false,
    val title: String = "",
    val message: String = "",
    val loadingTitle: String = "",
    val optionToConfirm: OptionToConfirm = OptionToConfirm.MediaQuality
)

enum class DialogState {
    ConfirmHighQuality, ConfirmRegularQuality, ConfirmCellular, HideDialog
}

enum class OptionToConfirm {
    MediaQuality, DownloadOverCellular
}

enum class ListeningState{
    START_LISTENING,
    KEEP_LISTENING,
    NOT_LISTENING
}