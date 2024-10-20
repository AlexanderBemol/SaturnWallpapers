package com.amontdevs.saturnwallpapers.model

interface SettingsMenuOptions {
    val id: Int
}

enum class MediaQuality(
    override val id: Int
): SettingsMenuOptions {
    HIGH(0),
    NORMAL(1);
    companion object {
        fun fromInt(value: Int) = entries.first { it.id == value }
    }
}

enum class WallpaperScreen(
    override val id: Int
): SettingsMenuOptions {
    ALL(0),
    LOCK_SCREEN(1),
    HOME_SCREEN(2);
    companion object {
        fun fromInt(value: Int) = entries.first { it.id == value }
    }
}

enum class DataMaxAge(
    override val id: Int
): SettingsMenuOptions {
    TWO_WEEKS(0),
    ONE_MONTH(1),
    THREE_MONTHS(2),
    SIX_MONTHS(3);
    companion object {
        fun fromInt(value: Int) = entries.first { it.id == value }
    }
}

enum class DefaultSaturnPhoto(
    override val id: Int
): SettingsMenuOptions {
    RANDOM(0),
    RANDOM_BETWEEN_FAVORITES(1);
    companion object {
        fun fromInt(value: Int) = entries.first { it.id == value }
    }
}