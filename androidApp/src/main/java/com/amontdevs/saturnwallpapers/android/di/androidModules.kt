package com.amontdevs.saturnwallpapers.android.di

import com.amontdevs.saturnwallpapers.android.system.AndroidWallpaperSetter
import com.amontdevs.saturnwallpapers.android.system.IAndroidWallpaperSetter
import com.amontdevs.saturnwallpapers.android.ui.dialogs.wallpaperbottomsheet.WallpaperBottomSheetViewModel
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
    viewModel { HomeViewModel(get(), get(), get()) }
    viewModel { params -> PhotoDetailViewModel(get(), get(), params.get()) }
    viewModel { SettingsViewModel(get(), get()) }
    viewModel { params -> WallpaperBottomSheetViewModel(get(), get(), get(), get(), params.get()) }
}

fun buildAndroidWallpaperSetter() : IAndroidWallpaperSetter = AndroidWallpaperSetter()

val androidSystemModules = module {
    factory { buildAndroidWallpaperSetter() }
}