package com.amontdevs.saturnwallpapers.utils

import com.amontdevs.saturnwallpapers.model.DataMaxAge
import com.amontdevs.saturnwallpapers.model.DefaultSaturnPhoto
import com.amontdevs.saturnwallpapers.model.MediaQuality
import com.amontdevs.saturnwallpapers.model.WallpaperScreen
import io.realm.kotlin.types.RealmInstant
import kotlinx.datetime.Instant

fun Instant.toRealmInstant() = RealmInstant.from(this.epochSeconds,0)

fun RealmInstant.toInstant() = Instant.fromEpochSeconds(this.epochSeconds)

fun Instant.toCommonFormat() = this.formatDate("yyyy-MM-dd")
