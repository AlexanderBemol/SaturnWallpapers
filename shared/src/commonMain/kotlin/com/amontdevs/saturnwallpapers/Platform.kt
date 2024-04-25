package com.amontdevs.saturnwallpapers

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform