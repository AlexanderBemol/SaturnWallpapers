package com.amontdevs.saturnwallpapers.android.utils

import androidx.navigation.NavDestination

fun NavDestination.toFixedRoute() = this.route?.split("/")?.get(0)?.split("?")?.get(0) ?: ""