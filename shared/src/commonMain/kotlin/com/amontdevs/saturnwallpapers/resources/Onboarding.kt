package com.amontdevs.saturnwallpapers.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalInspectionMode
import org.jetbrains.compose.resources.stringResource
import saturnwallpapers.shared.generated.resources.Res
import saturnwallpapers.shared.generated.resources.onboarding_configure_description
import saturnwallpapers.shared.generated.resources.onboarding_configure_switch_description
import saturnwallpapers.shared.generated.resources.onboarding_configure_title
import saturnwallpapers.shared.generated.resources.onboarding_done_description
import saturnwallpapers.shared.generated.resources.onboarding_done_title
import saturnwallpapers.shared.generated.resources.onboarding_get_started_button
import saturnwallpapers.shared.generated.resources.onboarding_go_back_button
import saturnwallpapers.shared.generated.resources.onboarding_loading_description
import saturnwallpapers.shared.generated.resources.onboarding_loading_title
import saturnwallpapers.shared.generated.resources.onboarding_next_button
import saturnwallpapers.shared.generated.resources.onboarding_overview_description
import saturnwallpapers.shared.generated.resources.onboarding_overview_title
import saturnwallpapers.shared.generated.resources.onboarding_welcome_description
import saturnwallpapers.shared.generated.resources.onboarding_welcome_title

object Onboarding {
    @Composable
    fun getOnboardingNextButton() = if (LocalInspectionMode.current) "Next"
        else stringResource(Res.string.onboarding_next_button)

    @Composable
    fun getOnboardingGoBackButton() = if (LocalInspectionMode.current) "Go Back"
        else stringResource(Res.string.onboarding_go_back_button)

    @Composable
    fun getOnboardingGetStartedButton() = if (LocalInspectionMode.current) "Get Started"
        else stringResource(Res.string.onboarding_get_started_button)

    @Composable
    fun getOnboardingWelcomeTitle() = if (LocalInspectionMode.current) "Welcome"
        else stringResource(Res.string.onboarding_welcome_title)

    @Composable
    fun getOnboardingWelcomeDescription() = if (LocalInspectionMode.current) "Welcome to Saturn Wallpapers, your new favorite wallpapers app"
        else stringResource(Res.string.onboarding_welcome_description)

    @Composable
    fun getOnboardingOverviewTitle() = if (LocalInspectionMode.current) "Overview"
        else stringResource(Res.string.onboarding_overview_title)

    @Composable
    fun getOnboardingOverviewDescription() = if (LocalInspectionMode.current) "Everyday you'll get a new image from the NASA APOD service, explore the gallery, find your favorites, refresh your wallpaper or download them and share them with your friends."
        else stringResource(Res.string.onboarding_overview_description)

    @Composable
    fun getOnboardingConfigureTitle() = if (LocalInspectionMode.current) "Configure"
        else stringResource(Res.string.onboarding_configure_title)

    @Composable
    fun getOnboardingConfigureDescription() = if (LocalInspectionMode.current) "You can activate the automatic service, if you do it, every day a new image is going to be set as your wallpaper, do you want to activate it?"
        else stringResource(Res.string.onboarding_configure_description)

    @Composable
    fun getOnboardingConfigureSwitch() = if (LocalInspectionMode.current) "Activate automatic service"
        else stringResource(Res.string.onboarding_configure_switch_description)

    @Composable
    fun getOnboardingLoadingTitle() = if (LocalInspectionMode.current) "Almost there"
        else stringResource(Res.string.onboarding_loading_title)

    @Composable
    fun getOnboardingLoadingDescription() = if (LocalInspectionMode.current) "We are setting everything up for you, please wait a moment"
        else stringResource(Res.string.onboarding_loading_description)

    @Composable
    fun getOnboardingDoneTitle() = if (LocalInspectionMode.current) "We're ready"
        else stringResource(Res.string.onboarding_done_title)

    @Composable
    fun getOnboardingDoneDescription() = if (LocalInspectionMode.current) "Everything is ready, enjoy your new wallpapers!"
        else stringResource(Res.string.onboarding_done_description)

}