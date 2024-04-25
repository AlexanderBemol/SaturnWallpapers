package com.amontdevs.saturnwallpapers.android.ui.navigation

import com.amontdevs.saturnwallpapers.android.R

sealed class BottomNavItem(
    val index: Int,
    val title: String,
    val icon: Int
) {
    data object Home: BottomNavItem(
        0,
        "Home",
        R.drawable.ic_home
    )

    data object Gallery: BottomNavItem(
        1,
        "Gallery",
        R.drawable.ic_gallery
    )

    data object Settings: BottomNavItem(
        2,
        "Settings",
        R.drawable.ic_more
    )
}