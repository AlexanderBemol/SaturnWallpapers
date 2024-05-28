package com.amontdevs.saturnwallpapers.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.amontdevs.saturnwallpapers.android.ui.components.SaturnAnimations.customFadeOut
import com.amontdevs.saturnwallpapers.android.ui.components.fadeInScaleIn
import com.amontdevs.saturnwallpapers.android.ui.components.fadeInSlideIntoEnd
import com.amontdevs.saturnwallpapers.android.ui.components.fadeInSlideIntoStart
import com.amontdevs.saturnwallpapers.android.ui.components.fadeInSlideIntoUp
import com.amontdevs.saturnwallpapers.android.ui.components.fadeOutScaleOut
import com.amontdevs.saturnwallpapers.android.ui.components.fadeOutSlideOutOfDown
import com.amontdevs.saturnwallpapers.android.ui.components.fadeOutSlideOutOfEnd
import com.amontdevs.saturnwallpapers.android.ui.components.fadeOutSlideOutOfStart
import com.amontdevs.saturnwallpapers.android.ui.gallery.GalleryScreen
import com.amontdevs.saturnwallpapers.android.ui.home.HomeScreen
import com.amontdevs.saturnwallpapers.android.ui.home.HomeViewModel
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavItem
import com.amontdevs.saturnwallpapers.android.ui.navigation.BottomNavigation
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.android.ui.photodetail.FullPictureViewScreen
import com.amontdevs.saturnwallpapers.android.ui.settings.SettingsScreen
import com.amontdevs.saturnwallpapers.android.ui.starting.StartingScreen
import com.amontdevs.saturnwallpapers.android.ui.starting.StartingViewModel
import com.amontdevs.saturnwallpapers.android.utils.toFixedRoute
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaturnTheme {
                AppContent()
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
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
                composable(
                    BottomNavItem.Home.title,
                    enterTransition = {
                        when (this.initialState.destination.toFixedRoute()) {
                            BottomNavItem.Gallery.title, BottomNavItem.Settings.title -> fadeInSlideIntoEnd()
                            else -> fadeIn()
                        }
                                      },
                    exitTransition = {
                        when (this.targetState.destination.toFixedRoute()) {
                            BottomNavItem.Gallery.title, BottomNavItem.Settings.title -> fadeOutSlideOutOfStart()
                            else -> fadeOut()
                        }
                    }
                ) {
                    displayBottomBar = true
                    HomeScreen(navController, homeViewModel)
                }
                composable(
                    BottomNavItem.Gallery.title + "?isFavoriteState={isFavoriteState}",
                    arguments = listOf(navArgument("isFavoriteState"){
                        type = NavType.BoolType
                        defaultValue = false
                    }),
                    enterTransition = {
                        when (this.initialState.destination.toFixedRoute()) {
                            Navigation.FULL_PICTURE.route -> fadeInSlideIntoUp()
                            BottomNavItem.Home.title -> fadeInSlideIntoStart()
                            BottomNavItem.Settings.title -> fadeInSlideIntoEnd()
                            else -> fadeIn()
                        }
                                      },
                    exitTransition = {
                        when (this.targetState.destination.toFixedRoute()) {
                            Navigation.FULL_PICTURE.route -> fadeOutSlideOutOfDown()
                            BottomNavItem.Home.title -> fadeOutSlideOutOfEnd()
                            BottomNavItem.Settings.title -> fadeOutSlideOutOfStart()
                            else -> fadeOut()
                        }
                    }
                ) {
                    displayBottomBar = true
                    it.arguments?.getBoolean("isFavoriteState")?.let { isFavoriteState ->
                        GalleryScreen(
                            navController,
                            koinViewModel(parameters = { parametersOf(isFavoriteState) })
                        )
                    }
                }
                composable(
                    BottomNavItem.Settings.title,
                    enterTransition = { this.fadeInSlideIntoStart() },
                    exitTransition = { this.fadeOutSlideOutOfEnd() }
                ) {
                    displayBottomBar = true
                    SettingsScreen(koinViewModel())
                }
                composable(
                    Navigation.FULL_PICTURE.route + "/{photoId}",
                    enterTransition = { fadeInScaleIn() },
                    exitTransition = { fadeOutScaleOut() },
                    arguments = listOf(navArgument("photoId"){
                        type = NavType.IntType
                        defaultValue = 0
                    })
                ){
                    displayBottomBar = false
                    it.arguments?.getInt("photoId")?.let { photoId ->
                        FullPictureViewScreen(
                            navController,
                            koinViewModel(parameters = { parametersOf(photoId) })
                        )
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
    SaturnTheme {
        //AppContent(MainViewModel())
    }
}
