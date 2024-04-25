package com.amontdevs.saturnwallpapers.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amontdevs.saturnwallpapers.android.ui.gallery.GalleryViewModel
import com.amontdevs.saturnwallpapers.android.ui.gallery.GalleryScreen
import com.amontdevs.saturnwallpapers.android.ui.home.HomeScreen
import com.amontdevs.saturnwallpapers.android.ui.home.HomeViewModel
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavItem
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.android.ui.photodetail.FullPictureViewScreen
import com.amontdevs.saturnwallpapers.android.ui.photodetail.PhotoDetailViewModel
import com.amontdevs.saturnwallpapers.android.ui.settings.SettingsScreen
import com.amontdevs.saturnwallpapers.android.ui.starting.StartingScreen
import com.amontdevs.saturnwallpapers.android.ui.starting.StartingViewModel
import org.koin.androidx.compose.getStateViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                AppContent()
            }
        }
    }
}

@Composable
fun AppContent(
    startingViewModel: StartingViewModel = koinViewModel(),
    homeViewModel: HomeViewModel = koinViewModel()
) {
    val navController = rememberNavController()
    var displayBottomBar by rememberSaveable { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                if(displayBottomBar) {
                    BottomNavigation(navController)
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Navigation.LOADING.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(BottomNavItem.Home.title) {
                    displayBottomBar = true
                    HomeScreen(navController, homeViewModel)
                }
                composable(
                    BottomNavItem.Gallery.title + "?isFavoriteState={isFavoriteState}",
                    arguments = listOf(navArgument("isFavoriteState"){
                        type = NavType.BoolType
                        defaultValue = false
                    })
                ) {
                    displayBottomBar = true
                    it.arguments?.getBoolean("isFavoriteState")?.let { isFavoriteState ->
                        GalleryScreen(navController, koinViewModel(parameters = { parametersOf(isFavoriteState) }))
                    }
                }
                composable(BottomNavItem.Settings.title) {
                    displayBottomBar = true
                    SettingsScreen(koinViewModel())
                }
                composable(
                    Navigation.FULL_PICTURE.route + "/{photoId}",
                    arguments = listOf(navArgument("photoId"){
                        type = NavType.StringType
                        defaultValue = ""
                    })
                ){
                    displayBottomBar = false
                    it.arguments?.getString("photoId")?.let { photoId ->
                        FullPictureViewScreen(navController, koinViewModel(parameters = { parametersOf(photoId) }))
                    }
                }
                composable(Navigation.LOADING.route) {
                    displayBottomBar = false
                    StartingScreen(startingViewModel) {
                        navController.navigate(BottomNavItem.Home.title) {
                            popUpTo(Navigation.LOADING.route) {
                                inclusive = true
                            }
                        }
                    }
                }
            }
        }
    }
}



@Preview
@Composable
fun DefaultPreview() {
    MyApplicationTheme {
        //AppContent(MainViewModel())
    }
}
