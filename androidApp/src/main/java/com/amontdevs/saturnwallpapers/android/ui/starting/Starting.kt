package com.amontdevs.saturnwallpapers.android.ui.starting

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amontdevs.saturnwallpapers.android.MyApplicationTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun StartingScreen(
    startingViewModel: StartingViewModel,
    navigateToHome: () -> Unit,
) {
    StartingScreen(
        startingStateFlow = startingViewModel.startingState,
        navigateToHome = navigateToHome) {
        startingViewModel.initialize()
    }
}

@Composable
fun StartingScreen(
    startingStateFlow: StateFlow<StartingState>,
    navigateToHome: () -> Unit,
    initialize: () -> Unit,
) {
    LaunchedEffect(Unit) {
        initialize()
        Log.d("StartingScreen", "initialize")
    }
    val startingState = startingStateFlow.collectAsState()
    if (startingState.value.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "${startingState.value.progress}%")
            Text(text = "Loading...")
        }
    } else {
        navigateToHome()
        Log.d("StartingScreen", "StartingScreen: navigateToHome")

    }
}

@Preview
@Composable
fun StartingScreenPreview(){
    MyApplicationTheme(
        isDarkTheme = true
    ) {
        Scaffold {
            StartingScreen(
                startingStateFlow = MutableStateFlow(StartingState()),
                navigateToHome = {},
                initialize = {}
            )
            it
        }
    }
}