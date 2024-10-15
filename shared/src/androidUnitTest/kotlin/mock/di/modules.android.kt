package mock.di

import androidx.room.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.amontdevs.saturnwallpapers.source.SaturnDatabase
import kotlinx.coroutines.Dispatchers
import mock.MockContext
import org.koin.dsl.module

fun buildTestingRoomDatabase(): SaturnDatabase {
    return Room.inMemoryDatabaseBuilder<SaturnDatabase>(
        context = MockContext()
    )
        .fallbackToDestructiveMigrationOnDowngrade(true)
        .setDriver(AndroidSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}

actual fun getTestingPlatformModules() = module {
    single { buildTestingRoomDatabase() }
}