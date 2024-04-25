package com.amontdevs.saturnwallpapers.android.ui.navigation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.amontdevs.saturnwallpapers.android.MyApplicationTheme

@Composable
fun RowScope.AddItem(
    screen: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        label = { Text(text = screen.title) },
        icon = {
               Icon(
                   painter = painterResource(id = screen.icon),
                   contentDescription = screen.title)
        },
        selected = isSelected ,
        alwaysShowLabel = true,
        onClick = { onClick() },
    )
}

@Composable
fun BottomNavigation(
    navController: NavController = rememberNavController()
)
{
    val items = listOf(BottomNavItem.Home, BottomNavItem.Gallery, BottomNavItem.Settings)
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
    MyApplicationTheme {
        Scaffold(
            bottomBar = {
                BottomNavigation()
            },
        ) {
            it
        }
    }
}