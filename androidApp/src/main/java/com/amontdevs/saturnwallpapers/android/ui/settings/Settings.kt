package com.amontdevs.saturnwallpapers.android.ui.settings

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.work.WorkInfo
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.ui.dialogs.ConfirmDialogLoading
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SettingsMenuOptions
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import com.amontdevs.saturnwallpapers.resources.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel){
    val onDailyWallpaperChanged = { _: Boolean -> settingsViewModel.toggleDailyWallpaperUpdater() }
    val onDownloadOverCellularChanged = { _: Boolean -> settingsViewModel.toggleDownloadOverCellular() }
    val onDropDownIndexChanged = { option: SettingsMenuOptions ->
        settingsViewModel.changeDropDownOption(option)
    }
    val onQualityDropDownDialogConfirm = { settingsViewModel.confirmQualityChangeOperation() }
    val onDownloadOverCellularDialogConfirm = { settingsViewModel.confirmDownloadOverCellular() }

    val onDialogDismiss = {
        settingsViewModel.cancelSettingChangeOperation()
    }

    var downloadingProgress by remember { mutableDoubleStateOf(-1.00 ) }

    when(settingsViewModel.settingsState.collectAsStateWithLifecycle().value.listeningState) {
        ListeningState.START_LISTENING -> settingsViewModel.startListeningToDownloadState()
        ListeningState.KEEP_LISTENING -> {
            settingsViewModel.progressLiveData.observe(LocalLifecycleOwner.current){ workInfo ->
                if(workInfo != null) {
                    val progress = workInfo.progress.keyValueMap["progress"] as? Double
                    progress?.let {
                        downloadingProgress = progress
                    }
                    if (progress ==null && workInfo.state == WorkInfo.State.SUCCEEDED){
                        downloadingProgress = 100.00
                        settingsViewModel.stopListeningToDownloadState()
                    }
                }
            }
        }
        ListeningState.NOT_LISTENING -> {}
    }

    SettingsScreen(
        settingsViewModel.settingsState,
        onDailyWallpaperChanged,
        onDownloadOverCellularChanged,
        onDropDownIndexChanged,
        onQualityDropDownDialogConfirm,
        onDownloadOverCellularDialogConfirm,
        downloadingProgress,
        onDialogDismiss
    )
}

@Composable
fun SettingsScreen(
    settingsStateFlow: StateFlow<SettingsState>,
    onDailyWallpaperChanged: (Boolean) -> Unit,
    onDownloadOverCellularChanged: (Boolean) -> Unit,
    onDropDownIndexChanged: (SettingsMenuOptions) -> Unit,
    onQualityDropDownDialogConfirm: () -> Unit,
    onDownloadOverCellularDialogConfirm: () -> Unit,
    downloadingProgress: Double,
    onDialogDismiss: () -> Unit
){
    val settingsState = settingsStateFlow.collectAsState()
    if (settingsState.value.dialogState != DialogState.HideDialog) {
        val title = Settings.getSettingsQualityConfirmDialogTitle()
        val message = when(settingsState.value.dialogState){
            DialogState.ConfirmHighQuality -> Settings.getSettingsQualityConfirmDialogHighQualityMessage()
            DialogState.ConfirmRegularQuality -> Settings.getSettingsQualityConfirmDialogRegularQualityMessage()
            else -> ""
        }
        val loadingTitle = when(settingsState.value.dialogState){
            DialogState.ConfirmHighQuality -> Settings.getSettingsQualityConfirmHighQualityLoadingTitle()
            DialogState.ConfirmRegularQuality -> Settings.getSettingsQualityConfirmRegularQualityLoadingTitle()
            else -> ""
        }
        val optionToConfirm = when(settingsState.value.dialogState){
            DialogState.ConfirmHighQuality, DialogState.ConfirmRegularQuality -> onQualityDropDownDialogConfirm
            DialogState.ConfirmCellular -> onDownloadOverCellularDialogConfirm
            else -> {->}
        }
        ConfirmDialogLoading(
            title = title,
            description = message,
            onLoadingTitle = loadingTitle,
            confirmText = @Composable { Text(text = Settings.getDialogConfirmText()) },
            dismissText = @Composable { Text(text = Settings.getDialogCancelText()) },
            onConfirm = optionToConfirm,
            onDismiss = onDialogDismiss
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column {
            Text(
                text = Settings.getSettingsTitle(),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .weight(weight = 1f, fill = false),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Column {
                        AutomaticServiceOption(
                            isChecked = settingsState.value.settings.isDailyWallpaperActivated,
                            onCheckedChange = onDailyWallpaperChanged
                        )
                        ScreenOfWallpaperOption(
                            wallpaperScreen = settingsState.value.settings.wallpaperScreen,
                            onIndexChanged = {
                                if (it != settingsState.value.settings.wallpaperScreen.id) {
                                    onDropDownIndexChanged(WallpaperScreen.fromInt(it))
                                }
                            }
                        )
                        DefaultDateOption(
                            defaultSaturnPhoto = settingsState.value.settings.defaultSaturnPhoto,
                            onIndexChanged = {
                                if (it != settingsState.value.settings.defaultSaturnPhoto.id){
                                    onDropDownIndexChanged(DefaultSaturnPhoto.fromInt(it))
                                }
                            }
                        )
                        QualityOption(
                            downloadingProgress,
                            mediaQuality = settingsState.value.settings.mediaQuality,
                            onIndexChanged = {
                                if (it != settingsState.value.settings.mediaQuality.id){
                                    onDropDownIndexChanged(MediaQuality.fromInt(it))
                                }
                            }
                        )
                        /*
                        DownloadsOverCellularOption(
                            isChecked = settingsState.value.settings.isDownloadOverCellularActivated,
                            onCheckedChange = onDownloadOverCellularChanged
                        )
                        */
                        MaxAgeOption(
                            dataMaxAge = settingsState.value.settings.dataMaxAge,
                            onIndexChanged = {
                                if (it != settingsState.value.settings.dataMaxAge.id){
                                    onDropDownIndexChanged(DataMaxAge.fromInt(it))
                                }
                            }
                        )
                    }
                }
            }

        }

    }
}

@Composable
fun OptionRow(
    addDivider: Boolean = true,
    optionExplanation: String,
    optionalFooter: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp)
    ) {
        content()
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Column {
            Text(
                text = optionExplanation,
                style = MaterialTheme.typography.bodySmall,
            )
            optionalFooter()
        }
    }
    if (addDivider) {
        HorizontalDivider()
    }
}

@Composable
fun AutomaticServiceOption(
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    OptionRow(
        optionExplanation = Settings.getSettingsDailyWallpaperDescription()
    ) {
        Text(
            text = Settings.getSettingsDailyWallpaperTitle(),
            style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun DownloadsOverCellularOption(
    isChecked: Boolean = false,
    onCheckedChange: (Boolean) -> Unit
) {
    OptionRow(
        optionExplanation = Settings.getSettingsDownloadOverCellularDescription()
    ) {
        Text(
            text = Settings.getSettingsDownloadOverCellularTitle(),
            style = MaterialTheme.typography.bodyMedium,
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun ScreenOfWallpaperOption(
    wallpaperScreen: WallpaperScreen,
    onIndexChanged: (Int) -> Unit
) {
    val menuOptions = WallpaperScreen.entries.map {
        when (it) {
            WallpaperScreen.HOME_SCREEN -> Settings.getSettingsScreenOptionHome()
            WallpaperScreen.LOCK_SCREEN -> Settings.getSettingsScreenOptionLock()
            WallpaperScreen.ALL -> Settings.getSettingsScreenOptionBoth()
        }
    }
    OptionRow(
        optionExplanation = Settings.getSettingsScreenDescription()
    ) {
        OptionDropDown(
            title = Settings.getSettingsScreenTitle(),
            selectedIndex = wallpaperScreen.ordinal,
            values = menuOptions,
            onIndexChanged = onIndexChanged
        )
    }
}

@Composable
fun QualityOption(
    downloadingProgress: Double,
    mediaQuality: MediaQuality,
    onIndexChanged: (Int) -> Unit
) {
    val menuOptions = MediaQuality.entries.map {
        when (it) {
            MediaQuality.NORMAL -> Settings.getSettingsQualityOptionNormal()
            MediaQuality.HIGH -> Settings.getSettingsQualityOptionHigh()
        }
    }
    OptionRow(
        optionExplanation = Settings.getSettingsQualityDescription(),
        optionalFooter = {
            if (downloadingProgress >= 0.0 && downloadingProgress < 99.0) {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = Settings.getDownloadingText() + downloadingProgress.roundToInt() + "%",
                    modifier = Modifier.padding(top = 16.dp)
                )
                LinearProgressIndicator(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                )
            }
        }
    ) {
        OptionDropDown(
            title = Settings.getSettingsQualityTitle(),
            selectedIndex = mediaQuality.id,
            values = menuOptions,
            onIndexChanged = onIndexChanged
        )
    }
}

@Composable
fun MaxAgeOption(
    dataMaxAge: DataMaxAge,
    onIndexChanged: (Int) -> Unit
) {
    val menuOptions = DataMaxAge.entries.map {
        when(it) {
            DataMaxAge.TWO_WEEKS -> Settings.getSettingsMaxAgeOptionTwoWeeks()
            DataMaxAge.ONE_MONTH -> Settings.getSettingsMaxAgeOptionOneMonth()
            DataMaxAge.THREE_MONTHS -> Settings.getSettingsMaxAgeOptionThreeMonths()
            DataMaxAge.SIX_MONTHS -> Settings.getSettingsMaxAgeOptionSixMonths()
        }
    }
    OptionRow(
        optionExplanation = Settings.getSettingsMaxAgeDescription()
    ) {
        OptionDropDown(
            title = Settings.getSettingsMaxAgeTitle(),
            selectedIndex = dataMaxAge.id,
            values = menuOptions,
            onIndexChanged = onIndexChanged
        )
    }
}

@Composable
fun DefaultDateOption(
    defaultSaturnPhoto: DefaultSaturnPhoto,
    onIndexChanged: (Int) -> Unit
) {
    val menuOptions = DefaultSaturnPhoto.entries.map {
        when(it) {
            DefaultSaturnPhoto.RANDOM -> Settings.getSettingsDefaultPhotoOptionRandom()
            DefaultSaturnPhoto.RANDOM_BETWEEN_FAVORITES -> Settings.getSettingsDefaultPhotoOptionRandomBetweenFavorites()
        }
    }
    OptionRow(
        optionExplanation = Settings.getSettingsDefaultPhotoDescription()
    ) {
        OptionDropDown(
            title = Settings.getSettingsDefaultPhotoTitle(),
            selectedIndex = defaultSaturnPhoto.id,
            values = menuOptions,
            onIndexChanged
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionDropDown(
    title: String,
    selectedIndex: Int,
    values: List<String>,
    onIndexChanged: (Int) -> Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded }
    ) {
        OutlinedTextField(
            label = { Text(text = title)},
            value = TextFieldValue(values[selectedIndex]),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
            ,
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = isExpanded
                )
            },
            onValueChange = {}
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false }
        ) {
            values.forEachIndexed { index, text ->
                DropdownMenuItem(
                    text = { Text(text = text) },
                    onClick = {
                        onIndexChanged(index)
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun SettingsPreview() {
    SaturnTheme(
        isDarkTheme = false,
        isDynamicColor = true
    ) {
        Scaffold(
            bottomBar = {
                BottomNavigation()
            }
        ){
            SettingsScreen(
                MutableStateFlow(SettingsState()),
                onDailyWallpaperChanged = {_, ->},
                onDownloadOverCellularChanged = {_, ->},
                onDropDownIndexChanged = { _, ->},
                onQualityDropDownDialogConfirm = {},
                onDownloadOverCellularDialogConfirm = {},
                downloadingProgress = 0.0,
                onDialogDismiss = {}
            )
            it
        }
    }
}