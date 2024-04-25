package com.amontdevs.saturnwallpapers.android.di

import com.amontdevs.saturnwallpapers.android.ui.gallery.GalleryViewModel
import com.amontdevs.saturnwallpapers.android.ui.home.HomeViewModel
import com.amontdevs.saturnwallpapers.android.ui.photodetail.PhotoDetailViewModel
import com.amontdevs.saturnwallpapers.android.ui.settings.SettingsViewModel
import com.amontdevs.saturnwallpapers.android.ui.starting.StartingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModules = module {
    viewModel { params -> GalleryViewModel(get(), params.get()) }
    viewModel { StartingViewModel(get()) }
    viewModel { HomeViewModel(get(), get()) }
    viewModel { params -> PhotoDetailViewModel(get(), params.get()) }
    viewModel { SettingsViewModel(get()) }
}

