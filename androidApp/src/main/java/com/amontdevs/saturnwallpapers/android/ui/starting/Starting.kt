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
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartingScreen(
    startingViewModel: StartingViewModel = koinViewModel(),
    navigateToHome: () -> Unit,
){
    val startingState by startingViewModel.startingState.collectAsStateWithLifecycle()
    if (startingState.isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "${startingState.progress}%")
            Text(text = "Loading...")
        }
    } else {
        Log.d("StartingScreen", "StartingScreen: navigateToHome")
        navigateToHome()
    }
}

@Preview
@Composable
fun StartingScreenPreview(){
    SaturnTheme(
        isDarkTheme = true
    ) {
        Scaffold {
            it
        }
    }
}