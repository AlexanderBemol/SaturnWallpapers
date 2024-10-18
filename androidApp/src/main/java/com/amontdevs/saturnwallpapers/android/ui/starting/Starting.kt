package com.amontdevs.saturnwallpapers.android.ui.starting

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.resources.Loading
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun StartingScreen(
    startingViewModel: StartingViewModel = koinViewModel(),
    navigateToHome: () -> Unit,
){
    StartingScreen(
        startingStateFlow = startingViewModel.startingState,
        navigateToHome = navigateToHome
    )
}

@Composable
fun StartingScreen(
    startingStateFlow: StateFlow<StartingState>,
    navigateToHome: () -> Unit,
){
    val startingState by startingStateFlow.collectAsStateWithLifecycle()
    if (startingState.isLoading) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!LocalInspectionMode.current) {
                val preloaderLottieComposition by rememberLottieComposition(
                    LottieCompositionSpec.RawRes(R.raw.space_loading)
                )
                val preloaderProgress by animateLottieCompositionAsState(
                    preloaderLottieComposition,
                    iterations = LottieConstants.IterateForever,
                    isPlaying = true
                )
                LottieAnimation(
                    composition = preloaderLottieComposition,
                    progress = { preloaderProgress },
                    modifier = Modifier.height(350.dp)
                )
            } else {
                CircularProgressIndicator()
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = Loading.getLoadingTitle(),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = Loading.getLoadingDescription(),
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "${startingState.progress}%")
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
            StartingScreen(
                startingStateFlow = MutableStateFlow(
                    StartingState(
                        isLoading = true,
                        progress = 75
                    )
                ),
                navigateToHome = {}
            )
            it
        }
    }
}