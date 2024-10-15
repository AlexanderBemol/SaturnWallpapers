package com.amontdevs.saturnwallpapers.android.ui.onboarding

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.amontdevs.saturnwallpapers.android.R
import com.amontdevs.saturnwallpapers.android.SaturnTheme
import com.amontdevs.saturnwallpapers.android.ui.components.fadeInScaleIn
import com.amontdevs.saturnwallpapers.android.ui.components.fadeOutScaleOut
import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.amontdevs.saturnwallpapers.resources.Onboarding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun OnboardingScreen(
    onboardingViewModel: OnboardingViewModel
) {
    Log.d("OnboardingScreen", "OnboardingScreen: recompose")
    OnboardingScreen(
        onboardingViewModel.onboardingState,
        onboardingViewModel::toggleIsServiceActivated,
        onboardingViewModel::completeOnboarding
    )

}

@Composable
fun OnboardingScreen(
    stateFlow: StateFlow<OnboardingState>,
    onServiceSwitchToggled: () -> Unit,
    onCompleteOnboardingClicked: () -> Unit
) {
    val onboardingState by stateFlow.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(initialPage = 0 ,pageCount = { 4 })
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ){ page ->
            when(page){
                0 -> OnboardingScreenWelcome()
                1 -> OnboardingScreenDetails()
                2 -> OnboardingScreenConfigure(
                    onboardingState.isServiceSwitchActivated,
                    onServiceSwitchToggled
                )
                3 -> OnboardingScreenLoading(
                    onboardingState.populateProgress,
                    onboardingState.onboardingStatus == OnboardingStatus.PopulatedNotOnboarded
                )
            }
        }
        Column {
            Row(
                modifier = Modifier
                    .padding(vertical = 32.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pagerState.pageCount){ iteration ->
                    AnimatedVisibility(
                        visible = pagerState.currentPage == iteration,
                        enter = fadeInScaleIn() + expandHorizontally(),
                        exit = fadeOutScaleOut() + shrinkHorizontally()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .height(16.dp)
                                .width(32.dp)
                        )
                    }
                    AnimatedVisibility(visible = pagerState.currentPage != iteration) {
                        Box(
                            modifier = Modifier
                                .padding(2.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary)
                                .size(16.dp)
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = when (pagerState.currentPage) {
                    0 -> Arrangement.End
                    else -> Arrangement.SpaceBetween
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (pagerState.currentPage > 0) {
                    TextButton(
                        onClick = {
                            pagerState.requestScrollToPage(pagerState.currentPage - 1)
                        }
                    ) {
                        Text(text = Onboarding.getOnboardingGoBackButton())
                    }
                }
                Button(
                    onClick = {
                        if (pagerState.currentPage < 3) {
                            pagerState.requestScrollToPage(pagerState.currentPage + 1)
                        } else {
                            onCompleteOnboardingClicked()
                        }
                    },
                    enabled = pagerState.currentPage < 3
                            || onboardingState.onboardingStatus == OnboardingStatus.PopulatedNotOnboarded
                ) {
                    Text(
                        text = if (pagerState.currentPage < 3) Onboarding.getOnboardingNextButton()
                            else Onboarding.getOnboardingGetStartedButton()
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null
                    )
                }

            }
        }

    }
}

@Composable
fun OnboardingScreenWelcome(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = Onboarding.getOnboardingWelcomeTitle(),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!LocalInspectionMode.current) {
            val preloaderLottieComposition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.astronaut_welcome)
            )
            val preloaderProgress by animateLottieCompositionAsState(
                preloaderLottieComposition,
                iterations = LottieConstants.IterateForever,
                isPlaying = true
            )
            LottieAnimation(
                composition = preloaderLottieComposition,
                progress = { preloaderProgress },
                modifier = Modifier
                    .height(350.dp)
                    .padding(16.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(350.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Text(
            text = Onboarding.getOnboardingWelcomeDescription(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

    }

}

@Composable
fun OnboardingScreenDetails(){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = Onboarding.getOnboardingOverviewTitle(),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!LocalInspectionMode.current) {
            val preloaderLottieComposition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.astronaut_hi)
            )
            val preloaderProgress by animateLottieCompositionAsState(
                preloaderLottieComposition,
                speed = .7f,
                iterations = LottieConstants.IterateForever,
                isPlaying = true
            )
            LottieAnimation(
                composition = preloaderLottieComposition,
                progress = { preloaderProgress },
                modifier = Modifier.height(350.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(350.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Text(
            text = Onboarding.getOnboardingOverviewDescription(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }

}

@Composable
fun OnboardingScreenConfigure(
    isServiceSwitchActivated: Boolean,
    onServiceSwitchToggled: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = Onboarding.getOnboardingConfigureTitle(),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!LocalInspectionMode.current) {
            val preloaderLottieComposition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.astronaut_working)
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
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(350.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Text(
            text = Onboarding.getOnboardingConfigureDescription(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Absolute.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = Onboarding.getOnboardingConfigureSwitch(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,

            )
            Switch(
                checked = isServiceSwitchActivated,
                onCheckedChange = {onServiceSwitchToggled()}
            )
        }
    }

}

@Composable
fun OnboardingScreenLoading(
    populateProgress: Int,
    alreadyPopulated: Boolean
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = if (alreadyPopulated) Onboarding.getOnboardingDoneTitle()
                else Onboarding.getOnboardingLoadingTitle(),
            style = MaterialTheme.typography.displaySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (!LocalInspectionMode.current) {
            val preloaderLottieComposition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(
                    if (alreadyPopulated) R.raw.animation_rocket else R.raw.space_loading
                )
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
        }
        else {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .size(350.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (alreadyPopulated) Onboarding.getOnboardingDoneDescription()
                    else Onboarding.getOnboardingLoadingDescription(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            if(!alreadyPopulated) {
                Text(
                    text = "$populateProgress%",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

    }
}

@Preview
@Composable
fun OnboardingPreview() {
    SaturnTheme(
        isDarkTheme = true,
        isDynamicColor = true
    ) {
        Scaffold{

            OnboardingScreen(
                stateFlow = MutableStateFlow(OnboardingState()),
                onServiceSwitchToggled = {},
                onCompleteOnboardingClicked = {}
            )
            it
        }
    }
}