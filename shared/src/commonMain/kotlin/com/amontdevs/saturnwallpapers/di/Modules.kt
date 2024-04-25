package com.amontdevs.saturnwallpapers.di

import com.amontdevs.saturnwallpapers.model.SaturnPhoto
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
import com.amontdevs.saturnwallpapers.source.SaturnPhotoDao
import com.amontdevs.saturnwallpapers.source.SettingsSource
import com.amontdevs.saturnwallpapers.source.TimeProvider
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun buildTimeProvider(): ITimeProvider =
    TimeProvider("UTC-05:00")

fun buildRealm(): Realm =
    Realm.open(RealmConfiguration.create(schema = setOf(SaturnPhoto::class)))

fun buildSettings(): Settings = Settings()

fun buildSaturnPhotoDao(realm: Realm): ISaturnPhotoDao =
    SaturnPhotoDao(realm)

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

val sourceModules = module {
    single { buildTimeProvider() }
    single { buildRealm() }
    single { buildSaturnPhotoDao(get()) }
    single { buildHTTPClient() }
    single { buildAPODService(get()) }
    single { buildFileManager() }
    single { buildSettings() }
    single { buildSaturnSettings(get()) }
}

val testSourceModules = module {
    single { buildTimeProvider() }
    single { buildRealm() }
    single { buildSaturnPhotoDao(get()) }
    single { buildHTTPClient() }
    single { buildAPODService(get()) }
}

fun buildSaturnPhotosRepository(
    apodService: IAPODService,
    saturnPhotoDao: ISaturnPhotoDao,
    timeProvider: ITimeProvider,
    fileManager: IFileManager,
    saturnSettings: ISettingsSource
): ISaturnPhotosRepository =
    SaturnPhotosRepository(apodService, saturnPhotoDao, timeProvider, fileManager, saturnSettings)

fun buildSettingsRepository(settingsSource: ISettingsSource): ISettingsRepository =
    SettingsRepository(settingsSource)

val repositoryModules = module {
    factory { buildSaturnPhotosRepository(get(), get(), get(), get(), get()) }
    factory { buildSettingsRepository(get()) }
}