package com.amontdevs.saturnwallpapers.android.utils

import com.amontdevs.saturnwallpapers.utils.formatDate
import com.amontdevs.saturnwallpapers.utils.toInstant
import io.realm.kotlin.types.RealmInstant

fun RealmInstant.toDisplayableString() =
    this.toInstant().formatDate("MMMM dd, yyyy")

fun RealmInstant.toAPODUrl(): String {
    val date = this.toInstant().formatDate("yyMMdd")
    return "https://apod.nasa.gov/apod/ap$date.html"
}