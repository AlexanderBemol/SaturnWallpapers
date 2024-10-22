package com.amontdevs.saturnwallpapers.android.utils

import com.amontdevs.saturnwallpapers.android.ui.navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent

class AnalyticsHelper {
    companion object {
        fun screenView(screen: Navigation) {
            Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW){
                param(FirebaseAnalytics.Param.SCREEN_NAME, screen.title)
                param(FirebaseAnalytics.Param.SCREEN_CLASS, screen.title)
            }
        }
    }
}