package com.amontdevs.saturnwallpapers.android.ui.settings

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.ui.dialogs.ConfirmDialogLoading
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.SettingsMenuOptions
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SettingsScreen(settingsViewModel: SettingsViewModel){
    LaunchedEffect(Unit){
        settingsViewModel.loadSettingsState()
    }
    val onDailyWallpaperChanged = { _: Boolean ->
        settingsViewModel.toggleDailyWallpaperUpdater()
    }

    val onDropDownIndexChanged = { option: SettingsMenuOptions ->
        settingsViewModel.changeDropDownOption(option)
    }

    val onDialogConfirm = {
        settingsViewModel.confirmSettingChangeOperation()
    }

    val onDialogDismiss = {
        settingsViewModel.cancelSettingChangeOperation()
    }

    SettingsScreen(
        settingsViewModel.settingsState,
        onDailyWallpaperChanged,
        onDropDownIndexChanged,
        onDialogConfirm,
        onDialogDismiss
    )
}

@Composable
fun SettingsScreen(
    settingsStateFlow: StateFlow<SettingsState>,
    onDailyWallpaperChanged: (Boolean) -> Unit,
    onDropDownIndexChanged: (SettingsMenuOptions) -> Unit,
    onDialogConfirm: () -> Unit,
    onDialogDismiss: () -> Unit
){
    val settingsState = settingsStateFlow.collectAsState()
    if (settingsState.value.confirmQuality.display){
        ConfirmDialogLoading(
            title = settingsState.value.confirmQuality.title,
            description = settingsState.value.confirmQuality.message,
            onLoadingTitle = settingsState.value.confirmQuality.loadingTitle,
            onConfirm = onDialogConfirm,
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
                text = "Settings",
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
                        QualityOption(
                            mediaQuality = settingsState.value.settings.mediaQuality,
                            onIndexChanged = {
                                if (it != settingsState.value.settings.mediaQuality.id){
                                    onDropDownIndexChanged(MediaQuality.fromInt(it))
                                }
                            }
                        )
                        MaxAgeOption(
                            dataMaxAge = settingsState.value.settings.dataMaxAge,
                            onIndexChanged = {
                                if (it != settingsState.value.settings.dataMaxAge.id){
                                    onDropDownIndexChanged(DataMaxAge.fromInt(it))
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
        Text(
            text = optionExplanation,
            style = MaterialTheme.typography.bodySmall,
        )
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
        optionExplanation = "When activated, the app is going to automatically " +
                "update the wallpaper everyday"
    ) {
        Text(
            text = "Daily wallpaper updater",
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
    val menuOptions = WallpaperScreen.entries.map { it.name }
    OptionRow(
        optionExplanation = "Define the screen where the wallpaper is going to automatically " +
                "be set when the service is activated."
    ) {
        OptionDropDown(
            title = "Screen",
            selectedIndex = wallpaperScreen.ordinal,
            values = menuOptions,
            onIndexChanged = onIndexChanged
        )
    }
}

@Composable
fun QualityOption(
    mediaQuality: MediaQuality,
    onIndexChanged: (Int) -> Unit
) {
    val menuOptions = MediaQuality.entries.map { it.name }
    OptionRow(
        optionExplanation = "Define the resolution of the images, the high quality requires more " +
                "storage and network data"
    ) {
        OptionDropDown(
            title = "Calidad",
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
    val menuOptions = DataMaxAge.entries.map { it.name }
    OptionRow(
        optionExplanation = "Define the maximum of time the images are going to be stored, after " +
                "that, if a record is older (and is not a favorite) is going to be deleted."
    ) {
        OptionDropDown(
            title = "Max age of data",
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
    val menuOptions = DefaultSaturnPhoto.entries.map { it.name }
    OptionRow(
        addDivider = false,
        optionExplanation = "For some days, an image can not be retrieved, define if the app is" +
                " going to take a random photo between your favorites, or a default one instead"
    ) {
        OptionDropDown(
            title = "Default photo",
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
                onDailyWallpaperChanged = {},
                onDropDownIndexChanged = { _, ->},
                onDialogConfirm = {},
                onDialogDismiss = {}
            )
            it
        }
    }
}