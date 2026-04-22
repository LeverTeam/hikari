package eu.kanade.tachiyomi.di

import eu.kanade.domain.base.BasePreferences
import eu.kanade.domain.track.service.TrackPreferences
import eu.kanade.tachiyomi.core.security.SecurityPreferences
import eu.kanade.tachiyomi.network.NetworkPreferences
import eu.kanade.tachiyomi.util.system.isDebugBuildType
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import tachiyomi.core.common.preference.AndroidPreferenceStore
import tachiyomi.core.common.preference.PreferenceStore
import tachiyomi.core.common.storage.FolderProvider
import tachiyomi.domain.backup.service.BackupPreferences
import tachiyomi.domain.download.service.DownloadPreferences
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.reader.service.ReaderPreferences
import tachiyomi.domain.source.service.SourcePreferences
import tachiyomi.domain.storage.service.StoragePreferences
import tachiyomi.domain.ui.UiPreferences
import tachiyomi.domain.updates.service.UpdatesPreferences

val preferenceModule = module {
    single<PreferenceStore> { AndroidPreferenceStore(androidApplication()) }

    single {
        NetworkPreferences(
            preferenceStore = get<PreferenceStore>(),
            verboseLoggingDefault = isDebugBuildType,
        )
    }

    single { SourcePreferences(get<PreferenceStore>()) }
    single { SecurityPreferences(get<PreferenceStore>()) }
    single { LibraryPreferences(get<PreferenceStore>()) }
    single { UpdatesPreferences(get<PreferenceStore>()) }
    single { ReaderPreferences(get<PreferenceStore>()) }
    single { TrackPreferences(get<PreferenceStore>()) }
    single { DownloadPreferences(get<PreferenceStore>()) }
    single { BackupPreferences(get<PreferenceStore>()) }
    single { StoragePreferences(get<FolderProvider>(), get<PreferenceStore>()) }
    single { UiPreferences(get<PreferenceStore>()) }
    single { BasePreferences(androidApplication(), get<PreferenceStore>()) }
}
