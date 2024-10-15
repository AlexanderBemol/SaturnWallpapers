package com.amontdevs.saturnwallpapers.model

sealed class RefreshOperationStatus(
    open val progress: Double
) {
    data class OperationInProgress(
        override val progress: Double = 0.0
    ) : RefreshOperationStatus(progress)
    data class OperationFinished(
        override val progress: Double = 0.0
    ) : RefreshOperationStatus(progress)
}