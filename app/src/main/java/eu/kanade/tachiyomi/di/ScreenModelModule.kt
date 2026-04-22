package eu.kanade.tachiyomi.di

import eu.kanade.domain.base.BasePreferences
import eu.kanade.domain.manga.interactor.UpdateManga
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.download.DownloadCache
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.track.TrackerManager
import eu.kanade.tachiyomi.ui.library.LibraryScreenModel
import eu.kanade.tachiyomi.ui.library.LibrarySettingsScreenModel
import org.koin.dsl.module
import tachiyomi.domain.category.interactor.GetCategories
import tachiyomi.domain.category.interactor.SetDisplayMode
import tachiyomi.domain.category.interactor.SetMangaCategories
import tachiyomi.domain.category.interactor.SetSortModeForCategory
import tachiyomi.domain.chapter.interactor.GetBookmarkedChaptersByMangaId
import tachiyomi.domain.chapter.interactor.GetChaptersByMangaId
import tachiyomi.domain.chapter.interactor.SetReadStatus
import tachiyomi.domain.history.interactor.GetNextChapters
import tachiyomi.domain.library.service.LibraryPreferences
import tachiyomi.domain.manga.interactor.GetLibraryManga
import tachiyomi.domain.source.service.SourceManager
import tachiyomi.domain.track.interactor.GetTracksPerManga

val screenModelModule = module {
    factory {
        LibraryScreenModel(
            getLibraryManga = get<GetLibraryManga>(),
            getCategories = get<GetCategories>(),
            getTracksPerManga = get<GetTracksPerManga>(),
            getNextChapters = get<GetNextChapters>(),
            getChaptersByMangaId = get<GetChaptersByMangaId>(),
            getBookmarkedChaptersByMangaId = get<GetBookmarkedChaptersByMangaId>(),
            setReadStatus = get<SetReadStatus>(),
            updateManga = get<UpdateManga>(),
            setMangaCategories = get<SetMangaCategories>(),
            preferences = get<BasePreferences>(),
            libraryPreferences = get<LibraryPreferences>(),
            coverCache = get<CoverCache>(),
            sourceManager = get<SourceManager>(),
            downloadManager = get<DownloadManager>(),
            downloadCache = get<DownloadCache>(),
            trackerManager = get<TrackerManager>(),
        )
    }

    factory {
        LibrarySettingsScreenModel(
            preferences = get<BasePreferences>(),
            libraryPreferences = get<LibraryPreferences>(),
            setDisplayMode = get<SetDisplayMode>(),
            setSortModeForCategory = get<SetSortModeForCategory>(),
            trackerManager = get<TrackerManager>(),
        )
    }
}
