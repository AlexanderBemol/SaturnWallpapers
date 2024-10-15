package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.home_favorites
import saturnwallpapers.shared.generated.resources.home_hello

object Home{
    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getHomeTitle() = if (LocalInspectionMode.current) "Hello!"
    else stringResource(Res.string.home_hello)

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    fun getHomeFavorites() = if (LocalInspectionMode.current) "Favorites photos"
    else stringResource(Res.string.home_favorites)
}