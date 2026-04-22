package eu.kanade.tachiyomi.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import app.cash.sqldelight.db.SqlDriver
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteConfiguration
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDatabaseType
import com.eygraber.sqldelight.androidx.driver.AndroidxSqliteDriver
import eu.kanade.domain.extension.interactor.TrustExtension
import eu.kanade.domain.track.store.DelayedTrackingStore
import eu.kanade.tachiyomi.data.cache.ChapterCache
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.download.DownloadCache
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.download.DownloadProvider
import eu.kanade.tachiyomi.data.saver.ImageSaver
import eu.kanade.tachiyomi.data.track.TrackerManager
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.network.JavaScriptEngine
import eu.kanade.tachiyomi.source.AndroidSourceManager
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import tachiyomi.core.common.storage.AndroidStorageFolderProvider
import tachiyomi.core.common.storage.FolderProvider
import tachiyomi.data.AndroidDatabaseHandler
import tachiyomi.data.Database
import tachiyomi.data.DatabaseHandler
import tachiyomi.data.DateColumnAdapter
import tachiyomi.data.History
import tachiyomi.data.Mangas
import tachiyomi.data.StringListColumnAdapter
import tachiyomi.data.UpdateStrategyColumnAdapter
import tachiyomi.domain.category.interactor.GetCategories
import tachiyomi.domain.download.service.DownloadPreferences
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.source.repository.StubSourceRepository
import tachiyomi.domain.source.service.SourceManager
import tachiyomi.domain.source.service.SourcePreferences
import tachiyomi.domain.storage.service.StorageManager
import tachiyomi.domain.storage.service.StoragePreferences
import tachiyomi.source.local.image.LocalCoverManager
import tachiyomi.source.local.io.LocalSourceFileSystem
import tachiyomi.domain.download.service.DownloadManager as DomainDownloadManager
import tachiyomi.domain.download.service.DownloadProvider as DomainDownloadProvider

val dataModule = module {
    single<SqlDriver> {
        AndroidxSqliteDriver(
            driver = BundledSQLiteDriver(),
            databaseType = AndroidxSqliteDatabaseType.FileProvider {
                androidApplication().getDatabasePath("tachiyomi.db").absolutePath
            },
            schema = Database.Schema,
            configuration = AndroidxSqliteConfiguration(
                isForeignKeyConstraintsEnabled = true,
            ),
        )
    }

    single {
        Database(
            driver = get<SqlDriver>(),
            historyAdapter = History.Adapter(
                last_readAdapter = DateColumnAdapter,
            ),
            mangasAdapter = Mangas.Adapter(
                genreAdapter = StringListColumnAdapter,
                update_strategyAdapter = UpdateStrategyColumnAdapter,
            ),
        )
    }

    single<DatabaseHandler> { AndroidDatabaseHandler(get<Database>(), get<SqlDriver>()) }

    single { ChapterCache(androidApplication(), get<Json>()) }
    single { CoverCache(androidApplication()) }

    single { JavaScriptEngine(androidApplication()) }

    single<SourceManager> {
        AndroidSourceManager(
            androidApplication(),
            get<ExtensionManager>(),
            get<StubSourceRepository>(),
        )
    }
    single { ExtensionManager(androidApplication(), get<SourcePreferences>(), get<TrustExtension>()) }

    single {
        DownloadProvider(
            context = androidApplication(),
            storageManager = get<StorageManager>(),
            libraryPreferences = get<LibraryPreferences>(),
        )
    }
    single<DomainDownloadProvider> { get<DownloadProvider>() }
    single {
        DownloadManager(
            context = androidApplication(),
            provider = get<DownloadProvider>(),
            cache = get<DownloadCache>(),
            getCategories = get<GetCategories>(),
            sourceManager = get<SourceManager>(),
            downloadPreferences = get<DownloadPreferences>(),
        )
    }
    single<DomainDownloadManager> { get<DownloadManager>() }
    single {
        DownloadCache(
            context = androidApplication(),
            provider = get<DownloadProvider>(),
            sourceManager = get<SourceManager>(),
            extensionManager = get<ExtensionManager>(),
            storageManager = get<StorageManager>(),
        )
    }

    single { TrackerManager() }
    single { DelayedTrackingStore(androidApplication()) }

    single { ImageSaver(androidApplication()) }

    single<FolderProvider> { AndroidStorageFolderProvider(androidApplication()) }
    single { StorageManager(androidApplication(), get<StoragePreferences>()) }
    single { LocalSourceFileSystem(get<StorageManager>()) }
    single { LocalCoverManager(androidApplication(), get<LocalSourceFileSystem>()) }
}
