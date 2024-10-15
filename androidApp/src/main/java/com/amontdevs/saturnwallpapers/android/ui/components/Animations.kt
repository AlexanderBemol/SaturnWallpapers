package com.amontdevs.saturnwallpapers.android.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.navigation.NavBackStackEntry
import com.amontdevs.saturnwallpapers.android.ui.components.SaturnAnimations.customFadeIn
import com.amontdevs.saturnwallpapers.android.ui.components.SaturnAnimations.customFadeOut

object SaturnAnimations {
    val linearTween: TweenSpec<Float> = tween(300, easing = LinearEasing)
    val customFadeIn = fadeIn(animationSpec = linearTween)
    val customFadeOut = fadeOut(animationSpec = linearTween)
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeInSlideIntoEnd() =
    customFadeIn + slideIntoContainer(
        animationSpec = tween(300, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.End
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeOutSlideOutOfStart() =
    customFadeOut + slideOutOfContainer(
        animationSpec = tween(300, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.Start
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeInSlideIntoUp() =
    customFadeIn + slideIntoContainer(
        animationSpec = tween(300, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Up
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeOutSlideOutOfDown() =
    customFadeOut + slideOutOfContainer(
        animationSpec = tween(300, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.Down
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeInSlideIntoStart() =
    customFadeIn + slideIntoContainer(
        animationSpec = tween(300, easing = EaseIn),
        towards = AnimatedContentTransitionScope.SlideDirection.Start
    )

fun AnimatedContentTransitionScope<NavBackStackEntry>.fadeOutSlideOutOfEnd() =
    customFadeOut + slideOutOfContainer(
        animationSpec = tween(300, easing = EaseOut),
        towards = AnimatedContentTransitionScope.SlideDirection.End
    )

fun fadeInScaleIn() =
    customFadeIn + scaleIn(
        animationSpec = tween(300, easing = EaseIn),
        initialScale = 0.1f
    )

fun fadeOutScaleOut() =
    customFadeOut + scaleOut(
        animationSpec = tween(300, easing = EaseOut),
        targetScale = 0.1f
    )