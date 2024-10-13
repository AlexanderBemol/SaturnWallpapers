package com.amontdevs.saturnwallpapers.android

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.WorkManager
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
                enableEdgeToEdge(
                    statusBarStyle = if (!isSystemInDarkTheme()) SystemBarStyle.light(
                            MaterialTheme.colorScheme.background.toArgb(),
                            MaterialTheme.colorScheme.onBackground.toArgb()
                        )
                     else SystemBarStyle.dark(MaterialTheme.colorScheme.background.toArgb()),
                )
                AppContent(rememberNavController())
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppContent(navController: NavHostController) {
    Log.d("AppContent", "Recomposing AppContent")
    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigateToHome = {
        navController.navigate(Navigation.Home.title) {
            popUpTo(Navigation.Loading.title) {
                inclusive = true
            }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            bottomBar = {
                if(!hideBottomBar(navBackStackEntry)) {
                    BottomNavigation(navController)
                }
            }
        ) { paddingValues ->
            Log.d("AppContent", "Recomposing Surface")
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = Navigation.Loading.title,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(
                        Navigation.Home.title,
                        enterTransition = {
                            when (this.initialState.destination.toFixedRoute()) {
                                Navigation.Gallery.title, Navigation.Settings.title -> fadeInSlideIntoEnd()
                                else -> fadeIn()
                            }
                        },
                        exitTransition = {
                            when (this.targetState.destination.toFixedRoute()) {
                                Navigation.Gallery.title, Navigation.Settings.title -> fadeOutSlideOutOfStart()
                                else -> fadeOut()
                            }
                        }
                    ) {
                        HomeScreen(
                            navController,
                            koinViewModel(parameters = { parametersOf(WorkManager.getInstance(context)) }),
                            this@SharedTransitionLayout,
                            this@composable
                        )
                    }
                    composable(
                        Navigation.Gallery.title + "?isFavoriteState={isFavoriteState}",
                        arguments = listOf(navArgument("isFavoriteState"){
                            type = NavType.BoolType
                            defaultValue = false
                        }),
                        enterTransition = {
                            when (this.initialState.destination.toFixedRoute()) {
                                Navigation.Details.title -> fadeInSlideIntoUp()
                                Navigation.Home.title -> fadeInSlideIntoStart()
                                Navigation.Settings.title -> fadeInSlideIntoEnd()
                                else -> fadeIn()
                            }
                        },
                        exitTransition = {
                            when (this.targetState.destination.toFixedRoute()) {
                                Navigation.Home.title -> fadeOutSlideOutOfEnd()
                                Navigation.Settings.title -> fadeOutSlideOutOfStart()
                                else -> fadeOut()
                            }
                        }
                    ) {
                        it.arguments?.getBoolean("isFavoriteState")?.let { isFavoriteState ->
                            GalleryScreen(
                                navController,
                                koinViewModel(parameters = { parametersOf(WorkManager.getInstance(context), isFavoriteState) }),
                                this@SharedTransitionLayout,
                                this@composable
                            )
                        }
                    }
                    composable(
                        Navigation.Settings.title,
                        enterTransition = { this.fadeInSlideIntoStart() },
                        exitTransition = { this.fadeOutSlideOutOfEnd() }
                    ) {
                        SettingsScreen(
                            koinViewModel(parameters = { parametersOf(WorkManager.getInstance(context)) })
                        )
                    }
                    composable(
                        Navigation.Details.title + "/{photoId},{sharedKey}",
                        //enterTransition = { fadeInScaleIn() },
                        //exitTransition = { fadeOutScaleOut() },
                        arguments = listOf(navArgument("photoId"){
                            type = NavType.LongType
                            defaultValue = 0
                        }, navArgument("sharedKey"){
                            type = NavType.StringType
                            defaultValue = ""
                        })
                    ){
                        val photoId = it.arguments?.getLong("photoId")
                        val sharedKey = it.arguments?.getString("sharedKey")

                        if(photoId != null && sharedKey != null) {
                            FullPictureViewScreen(
                                navController,
                                koinViewModel(parameters = { parametersOf(photoId, sharedKey) }),
                                this@SharedTransitionLayout,
                                this@composable
                            )
                        }
                    }
                    composable(Navigation.Loading.title) {
                        StartingScreen(navigateToHome = navigateToHome)
                    }
                }
            }
        }
    }
}

@Composable
fun hideBottomBar(navBackStackEntry: NavBackStackEntry?): Boolean {
    val currentRoute = navBackStackEntry?.destination?.toFixedRoute()
    return Navigation.Details.title.contains(currentRoute.toString()) ||
            Navigation.Loading.title.contains(currentRoute.toString()) ||
            currentRoute == null
}

@Preview
@Composable
fun DefaultPreview() {
    SaturnTheme {
        //AppContent(MainViewModel())
    }
}
