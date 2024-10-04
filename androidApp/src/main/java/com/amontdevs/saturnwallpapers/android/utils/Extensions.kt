package com.amontdevs.saturnwallpapers.android.utils

import android.content.Context
import androidx.navigation.NavDestination
import java.io.File

fun NavDestination.toFixedRoute() =
    this.route?.split("/")?.get(0)?.split("?")?.get(0) ?: ""

fun Context.getPrivateFile(fileName: String) =
    File(this.getDir("images", Context.MODE_PRIVATE), fileName)