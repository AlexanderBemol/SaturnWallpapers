package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.loading_description
import saturnwallpapers.shared.generated.resources.loading_title

object Loading {
    @Composable
    fun getLoadingTitle() = if(LocalInspectionMode.current) "Loading"
        else stringResource(Res.string.loading_title)

    @Composable
    fun getLoadingDescription() = if(LocalInspectionMode.current) "Please wait while new data is being downloaded."
        else stringResource(Res.string.loading_description)

}