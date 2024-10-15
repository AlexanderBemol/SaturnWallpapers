package com.amontdevs.saturnwallpapers.android.ui.navigation

import androidx.navigation.NavController

interface ISaturnNavigator {
    fun navigateTo(destination: Navigation)
    fun navigateToPopInclusive(destination: Navigation, destinationPopInclusive: Navigation)
}

class SaturnNavigator(
    private val navController: NavController
) : ISaturnNavigator {

    override fun navigateTo(destination: Navigation) {
        navController.navigate(destination)
    }

    override fun navigateToPopInclusive(destination: Navigation, destinationPopInclusive: Navigation) {
        navController.navigate(destination.title){
            popUpTo(destinationPopInclusive.title) {
                inclusive = true
            }
        }
    }
}