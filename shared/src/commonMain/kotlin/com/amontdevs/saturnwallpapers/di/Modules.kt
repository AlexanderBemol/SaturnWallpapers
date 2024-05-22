package com.amontdevs.saturnwallpapers.di

import com.amontdevs.saturnwallpapers.repository.ISaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.ISettingsRepository
import com.amontdevs.saturnwallpapers.repository.SaturnPhotosRepository
import com.amontdevs.saturnwallpapers.repository.SettingsRepository
import com.amontdevs.saturnwallpapers.source.APODService
import com.amontdevs.saturnwallpapers.source.FileManager
import com.amontdevs.saturnwallpapers.source.IAPODService
import com.amontdevs.saturnwallpapers.source.IFileManager
import com.amontdevs.saturnwallpapers.source.ISaturnPhotoDao
import com.amontdevs.saturnwallpapers.source.ISettingsSource
import com.amontdevs.saturnwallpapers.source.ITimeProvider
import com.amontdevs.saturnwallpapers.source.SaturnDatabase
import com.amontdevs.saturnwallpapers.source.SettingsSource
import com.amontdevs.saturnwallpapers.source.TimeProvider
import com.amontdevs.saturnwallpapers.system.IWallpaperSetter
import com.amontdevs.saturnwallpapers.system.WallpaperSetter
import com.amontdevs.saturnwallpapers.utils.ISaturnLogger
import com.amontdevs.saturnwallpapers.utils.SaturnLogger
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun getPlatformModules(): Module

fun buildLogger(): ISaturnLogger = SaturnLogger()

fun buildTimeProvider(): ITimeProvider =
    TimeProvider("UTC-05:00")
fun buildSettings(): Settings = Settings()

@OptIn(ExperimentalSerializationApi::class)
fun buildHTTPClient(): HttpClient = HttpClient(){
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        })
    }
}

fun buildAPODService(client: HttpClient): IAPODService = APODService(client)

fun buildFileManager(): IFileManager = FileManager()

fun buildSaturnSettings(settings: Settings): ISettingsSource = SettingsSource(settings)

fun provideSaturnPhotoDao(saturnDb: SaturnDatabase): ISaturnPhotoDao = saturnDb.saturnPhotoDao()

val sourceModules = module {
    single { buildLogger() }
    single { buildTimeProvider() }
    single { buildHTTPClient() }
    single { buildAPODService(get()) }
    single { buildFileManager() }
    single { buildSettings() }
    single { buildSaturnSettings(get()) }
    single { provideSaturnPhotoDao(get()) }
}

fun buildWallpaperSetter(logger: ISaturnLogger, fileManager: IFileManager): IWallpaperSetter =
    WallpaperSetter(logger, fileManager)

val systemModules = module {
    single { buildWallpaperSetter(get(), get()) }
}

fun buildSaturnPhotosRepository(
    logger: ISaturnLogger,
    apodService: IAPODService,
    saturnPhotoDao: ISaturnPhotoDao,
    timeProvider: ITimeProvider,
    fileManager: IFileManager,
    saturnSettings: ISettingsSource
): ISaturnPhotosRepository =
    SaturnPhotosRepository(logger, apodService, saturnPhotoDao, timeProvider, fileManager, saturnSettings)

fun buildSettingsRepository(settingsSource: ISettingsSource): ISettingsRepository =
    SettingsRepository(settingsSource)

val repositoryModules = module {
    factory { buildSaturnPhotosRepository(get(), get(), get(), get(), get(), get()) }
    factory { buildSettingsRepository(get()) }
}