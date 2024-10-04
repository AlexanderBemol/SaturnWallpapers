package com.amontdevs.saturnwallpapers.android.ui.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.resources.BottomNavMenu.getGallery
import com.amontdevs.saturnwallpapers.resources.BottomNavMenu.getHome
import com.amontdevs.saturnwallpapers.resources.BottomNavMenu.getSettings

@Composable
fun RowScope.AddItem(
    screen: Navigation,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val title = when (screen) {
        Navigation.Home -> getHome()
        Navigation.Gallery -> getGallery()
        Navigation.Settings -> getSettings()
        else -> throw IllegalArgumentException("Invalid screen type")
    }

    NavigationBarItem(
        label = { Text(text = title) },
        icon = {
               Icon(
                   painter = painterResource(id = screen.icon),
                   contentDescription = title)
        },
        selected = isSelected ,
        alwaysShowLabel = true,
        onClick = { onClick() },
    )
}

@Composable
fun BottomNavigation(
    navController: NavController = rememberNavController()
) {
    val items = listOf(Navigation.Home, Navigation.Gallery, Navigation.Settings)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.split("?")?.get(0)

    NavigationBar {
        items.forEach {
            AddItem(
                screen = it,
                isSelected = it.title == currentRoute
            ){
                navController.navigate(it.title) {
                    navController.graph.startDestinationRoute?.let { route ->
                        popUpTo(route){ saveState = true }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    }
}


@Preview
@Composable
fun BottomBarPreview() {
    SaturnTheme {
        Scaffold(
            bottomBar = {
                BottomNavigation()
            },
        ) {
            it
        }
    }
}