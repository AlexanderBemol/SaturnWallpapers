package com.amontdevs.saturnwallpapers.utils

import kotlinx.datetime.Instant

expect fun Instant.formatDate(pattern: String): String
expect fun String.toInstant(pattern: String): Instant