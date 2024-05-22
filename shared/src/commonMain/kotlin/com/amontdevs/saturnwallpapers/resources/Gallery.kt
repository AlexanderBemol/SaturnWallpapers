package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.gallery_favorites
import saturnwallpapers.shared.generated.resources.gallery_title
import saturnwallpapers.shared.generated.resources.menu_home

object Gallery {
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getTitle() = if (LocalInspectionMode.current) "Gallery"
        else stringResource(Res.string.gallery_title)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getFavorites() = if (LocalInspectionMode.current) "Favorites"
        else stringResource(Res.string.gallery_favorites)

}