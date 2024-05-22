package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.menu_gallery
import saturnwallpapers.shared.generated.resources.menu_home
import saturnwallpapers.shared.generated.resources.menu_settings

object BottomNavMenu {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getHome() = if (LocalInspectionMode.current) "Home" else stringResource(Res.string.menu_home)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getGallery() = if (LocalInspectionMode.current) "Gallery" else stringResource(Res.string.menu_gallery)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSettings() = if (LocalInspectionMode.current) "Settings" else stringResource(Res.string.menu_settings)
}