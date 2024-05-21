package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_download
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_download_high
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_download_normal
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_set_wallpaper
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_set_wallpaper_both
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_set_wallpaper_home
import saturnwallpapers.shared.generated.resources.gallery_bottom_menu_set_wallpaper_lock

object GalleryBottomMenu {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSetWallpaperFor() = if (LocalInspectionMode.current) "Set wallpaper for..."
        else stringResource(Res.string.gallery_bottom_menu_set_wallpaper)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSetLockScreen() = if (LocalInspectionMode.current) "Lock screen"
        else stringResource(Res.string.gallery_bottom_menu_set_wallpaper_lock)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSetHomeScreen() = if (LocalInspectionMode.current) "Home screen"
        else stringResource(Res.string.gallery_bottom_menu_set_wallpaper_home)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getSetBothScreens() = if (LocalInspectionMode.current) "Both screens"
        else stringResource(Res.string.gallery_bottom_menu_set_wallpaper_both)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getDownload() = if (LocalInspectionMode.current) "Download..."
        else stringResource(Res.string.gallery_bottom_menu_download)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getDownloadNormal() = if (LocalInspectionMode.current) "Normal quality"
        else stringResource(Res.string.gallery_bottom_menu_download_normal)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getDownloadHigh() = if (LocalInspectionMode.current) "High quality"
        else stringResource(Res.string.gallery_bottom_menu_download_high)

}