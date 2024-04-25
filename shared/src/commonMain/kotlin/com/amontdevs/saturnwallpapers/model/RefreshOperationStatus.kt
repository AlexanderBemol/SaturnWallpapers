package com.amontdevs.saturnwallpapers.model

sealed class RefreshOperationStatus() {
    data object OperationInProgress : RefreshOperationStatus()
    data object OperationFinished : RefreshOperationStatus()
}