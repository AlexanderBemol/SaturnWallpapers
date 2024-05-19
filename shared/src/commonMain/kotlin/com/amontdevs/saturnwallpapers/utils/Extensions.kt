package com.amontdevs.saturnwallpapers.utils

import kotlinx.datetime.Instant

fun Long.toInstant() =
    Instant.fromEpochMilliseconds(this)

fun Instant.toDisplayableString() : String {
    val formattedDate = this.formatDate("MMMM dd, yyyy")
    return formattedDate[0].uppercase() + formattedDate.substring(1)
}

fun Instant.toAPODUrl(): String {
    val date = this.formatDate("yyMMdd")
    return "https://apod.nasa.gov/apod/ap$date.html"
}

fun Instant.toCommonFormat() = this.formatDate("yyyy-MM-dd")
