package com.amontdevs.saturnwallpapers.android.ui.navigation

import com.amontdevs.saturnwallpapers.android.R

sealed class Navigation(
    val index: Int,
    val title: String,
    val icon: Int,
    val isBottomNavItem: Boolean = false
) {
    data object Loading: Navigation(
        0,
        "Loading",
        0,
        false
    )
    data object Home: Navigation(
        1,
        "Home",
        R.drawable.ic_home,
        true
    )
    data object Gallery: Navigation(
        2,
        "Gallery",
        R.drawable.ic_gallery,
        true
    )
    data object Settings: Navigation(
        3,
        "Settings",
        R.drawable.ic_more,
        true
    )
    data object Details: Navigation(
        4,
        "Details",
        0,
        false
    )
}