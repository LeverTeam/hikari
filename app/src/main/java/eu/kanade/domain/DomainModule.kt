package eu.kanade.domain

import eu.kanade.domain.chapter.interactor.GetAvailableScanlators
import eu.kanade.domain.download.interactor.DeleteDownload
import eu.kanade.domain.extension.interactor.GetExtensionLanguages
import eu.kanade.domain.extension.interactor.GetExtensionSources
import eu.kanade.domain.extension.interactor.GetExtensionsByType
import eu.kanade.domain.extension.interactor.TrustExtension
import eu.kanade.domain.manga.interactor.UpdateManga
import eu.kanade.domain.source.interactor.GetIncognitoState
import eu.kanade.domain.track.interactor.AddTracks
import eu.kanade.domain.track.interactor.RefreshTracks
import eu.kanade.domain.track.interactor.SyncChapterProgressWithTrack
import eu.kanade.domain.track.interactor.TrackChapter
import eu.kanade.tachiyomi.data.cache.CoverCache
import eu.kanade.tachiyomi.data.download.DownloadManager
import eu.kanade.tachiyomi.data.track.TrackerManager
import eu.kanade.tachiyomi.util.system.LanguageComparatorImpl
import hikari.data.repository.ExtensionRepoRepositoryImpl
import hikari.domain.chapter.interactor.FilterChaptersForDownload
import hikari.domain.extensionrepo.interactor.CreateExtensionRepo
import hikari.domain.extensionrepo.interactor.DeleteExtensionRepo
import hikari.domain.extensionrepo.interactor.GetExtensionRepo
import hikari.domain.extensionrepo.interactor.GetExtensionRepoCount
import hikari.domain.extensionrepo.interactor.ReplaceExtensionRepo
import hikari.domain.extensionrepo.interactor.UpdateExtensionRepo
import hikari.domain.extensionrepo.repository.ExtensionRepoRepository
import hikari.domain.extensionrepo.service.ExtensionRepoService
import hikari.domain.migration.usecases.MigrateMangaUseCase
import org.koin.dsl.bind
import org.koin.dsl.module
import tachiyomi.data.category.CategoryRepositoryImpl
import tachiyomi.data.chapter.ChapterRepositoryImpl
import tachiyomi.data.history.HistoryRepositoryImpl
import tachiyomi.data.manga.MangaRepositoryImpl
import tachiyomi.data.release.ReleaseServiceImpl
import tachiyomi.data.source.SourceRepositoryImpl
import tachiyomi.data.source.StubSourceRepositoryImpl
import tachiyomi.data.track.TrackRepositoryImpl
import tachiyomi.data.updates.UpdatesRepositoryImpl
import tachiyomi.domain.category.interactor.CreateCategoryWithName
import tachiyomi.domain.category.interactor.DeleteCategory
import tachiyomi.domain.category.interactor.GetCategories
import tachiyomi.domain.category.interactor.RenameCategory
import tachiyomi.domain.category.interactor.ReorderCategory
import tachiyomi.domain.category.interactor.ResetCategoryFlags
import tachiyomi.domain.category.interactor.SetDisplayMode
import tachiyomi.domain.category.interactor.SetMangaCategories
import tachiyomi.domain.category.interactor.SetSortModeForCategory
import tachiyomi.domain.category.interactor.UpdateCategory
import tachiyomi.domain.category.repository.CategoryRepository
import tachiyomi.domain.chapter.interactor.GetBookmarkedChaptersByMangaId
import tachiyomi.domain.chapter.interactor.GetChapter
import tachiyomi.domain.chapter.interactor.GetChapterByUrlAndMangaId
import tachiyomi.domain.chapter.interactor.GetChaptersByMangaId
import tachiyomi.domain.chapter.interactor.SetMangaDefaultChapterFlags
import tachiyomi.domain.chapter.interactor.SetReadStatus
import tachiyomi.domain.chapter.interactor.ShouldUpdateDbChapter
import tachiyomi.domain.chapter.interactor.SyncChaptersWithSource
import tachiyomi.domain.chapter.interactor.UpdateChapter
import tachiyomi.domain.chapter.repository.ChapterRepository
import tachiyomi.domain.download.service.DownloadPreferences
import tachiyomi.domain.history.interactor.GetHistory
import tachiyomi.domain.history.interactor.GetHistoryHeatmap
import tachiyomi.domain.history.interactor.GetNextChapters
import tachiyomi.domain.history.interactor.GetTotalReadDuration
import tachiyomi.domain.history.interactor.RemoveHistory
import tachiyomi.domain.history.interactor.UpsertHistory
import tachiyomi.domain.history.repository.HistoryRepository
import tachiyomi.domain.manga.interactor.FetchInterval
import tachiyomi.domain.manga.interactor.GetDuplicateLibraryManga
import tachiyomi.domain.manga.interactor.GetExcludedScanlators
import tachiyomi.domain.manga.interactor.GetFavorites
import tachiyomi.domain.manga.interactor.GetHiddenManga
import tachiyomi.domain.manga.interactor.GetLibraryManga
import tachiyomi.domain.manga.interactor.GetManga
import tachiyomi.domain.manga.interactor.GetMangaByUrlAndSourceId
import tachiyomi.domain.manga.interactor.GetMangaWithChapters
import tachiyomi.domain.manga.interactor.NetworkToLocalManga
import tachiyomi.domain.manga.interactor.ResetViewerFlags
import tachiyomi.domain.manga.interactor.SetExcludedScanlators
import tachiyomi.domain.manga.interactor.SetMangaChapterFlags
import tachiyomi.domain.manga.interactor.SetMangaViewerFlags
import tachiyomi.domain.manga.interactor.UpdateMangaNotes
import tachiyomi.domain.manga.repository.MangaRepository
import tachiyomi.domain.release.interactor.GetApplicationRelease
import tachiyomi.domain.release.service.ReleaseService
import tachiyomi.domain.source.interactor.GetEnabledSources
import tachiyomi.domain.source.interactor.GetLanguagesWithSources
import tachiyomi.domain.source.interactor.GetRemoteManga
import tachiyomi.domain.source.interactor.GetSourcesWithFavoriteCount
import tachiyomi.domain.source.interactor.GetSourcesWithNonLibraryManga
import tachiyomi.domain.source.interactor.SetMigrateSorting
import tachiyomi.domain.source.interactor.ToggleIncognito
import tachiyomi.domain.source.interactor.ToggleLanguage
import tachiyomi.domain.source.interactor.ToggleSource
import tachiyomi.domain.source.interactor.ToggleSourcePin
import tachiyomi.domain.source.repository.SourceRepository
import tachiyomi.domain.source.repository.StubSourceRepository
import tachiyomi.domain.source.service.LanguageComparator
import tachiyomi.domain.source.service.SourceManager
import tachiyomi.domain.source.service.SourcePreferences
import tachiyomi.domain.track.interactor.DeleteTrack
import tachiyomi.domain.track.interactor.GetTracks
import tachiyomi.domain.track.interactor.GetTracksPerManga
import tachiyomi.domain.track.interactor.InsertTrack
import tachiyomi.domain.track.repository.TrackRepository
import tachiyomi.domain.updates.interactor.GetUpdates
import tachiyomi.domain.updates.repository.UpdatesRepository

val domainModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
    factory { GetCategories(get()) }
    factory { ResetCategoryFlags(get(), get()) }
    factory { SetDisplayMode(get()) }
    factory { SetSortModeForCategory(get(), get()) }
    factory { CreateCategoryWithName(get(), get()) }
    factory { RenameCategory(get()) }
    factory { ReorderCategory(get()) }
    factory { UpdateCategory(get()) }
    factory { DeleteCategory(get(), get(), get()) }

    single<MangaRepository> { MangaRepositoryImpl(get()) }
    factory { GetDuplicateLibraryManga(get()) }
    factory { GetFavorites(get()) }
    factory { GetLibraryManga(get()) }
    factory { GetMangaWithChapters(get(), get()) }
    factory { GetMangaByUrlAndSourceId(get()) }
    factory { GetManga(get()) }
    factory { GetHiddenManga(get()) }
    factory { GetNextChapters(get(), get(), get()) }

    factory { ResetViewerFlags(get()) }
    factory { SetMangaChapterFlags(get()) }
    factory { FetchInterval(get()) }
    factory { SetMangaDefaultChapterFlags(get(), get(), get()) }
    factory { SetMangaViewerFlags(get()) }
    factory { NetworkToLocalManga(get()) }
    factory {
        UpdateManga(
            get<MangaRepository>(),
            get<FetchInterval>(),
        )
    } bind tachiyomi.domain.manga.interactor.UpdateManga::class
    factory { UpdateMangaNotes(get()) }
    factory { SetMangaCategories(get()) }
    factory { GetExcludedScanlators(get()) }
    factory { SetExcludedScanlators(get()) }
    factory {
        MigrateMangaUseCase(
            get<SourcePreferences>(),
            get<TrackerManager>(),
            get<SourceManager>(),
            get<DownloadManager>(),
            get<UpdateManga>(),
            get<GetChaptersByMangaId>(),
            get<SyncChaptersWithSource>(),
            get<UpdateChapter>(),
            get<GetCategories>(),
            get<SetMangaCategories>(),
            get<GetTracks>(),
            get<InsertTrack>(),
            get<CoverCache>(),
        )
    }

    single<ReleaseService> { ReleaseServiceImpl(get(), get()) }
    factory { GetApplicationRelease(get(), get()) }

    single<TrackRepository> { TrackRepositoryImpl(get()) }
    factory { TrackChapter(get(), get(), get(), get()) }
    factory { AddTracks(get(), get(), get(), get()) }
    factory { RefreshTracks(get(), get(), get(), get()) }
    factory { DeleteTrack(get()) }
    factory { GetTracksPerManga(get()) }
    factory { GetTracks(get()) }
    factory { InsertTrack(get()) }
    factory { SyncChapterProgressWithTrack(get(), get(), get()) }

    single<ChapterRepository> { ChapterRepositoryImpl(get()) }
    factory { GetChapter(get()) }
    factory { GetChaptersByMangaId(get()) }
    factory { GetBookmarkedChaptersByMangaId(get()) }
    factory { GetChapterByUrlAndMangaId(get()) }
    factory { UpdateChapter(get()) }
    factory {
        SetReadStatus(
            get<DownloadPreferences>(),
            get<DeleteDownload>(),
            get<MangaRepository>(),
            get<ChapterRepository>(),
        )
    }
    factory { ShouldUpdateDbChapter() }
    factory { SyncChaptersWithSource(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { GetAvailableScanlators(get()) }
    factory { FilterChaptersForDownload(get(), get(), get()) }

    single<HistoryRepository> { HistoryRepositoryImpl(get()) }
    factory { GetHistory(get()) }
    factory { UpsertHistory(get()) }
    factory { RemoveHistory(get()) }
    factory { GetTotalReadDuration(get()) }
    factory { GetHistoryHeatmap(get()) }

    factory { DeleteDownload(get(), get()) } bind tachiyomi.domain.download.interactor.DeleteDownload::class

    factory { GetExtensionsByType(get(), get()) }
    factory { GetExtensionSources(get()) }
    factory { GetExtensionLanguages(get(), get()) }

    single<UpdatesRepository> { UpdatesRepositoryImpl(get()) }
    factory { GetUpdates(get()) }

    single<SourceRepository> { SourceRepositoryImpl(get(), get()) }
    single<StubSourceRepository> { StubSourceRepositoryImpl(get()) }
    single<LanguageComparator> { LanguageComparatorImpl() }
    factory { GetEnabledSources(get(), get()) }
    factory { GetLanguagesWithSources(get(), get(), get()) }
    factory { GetRemoteManga(get()) }
    factory { GetSourcesWithFavoriteCount(get(), get()) }
    factory { GetSourcesWithNonLibraryManga(get()) }
    factory { SetMigrateSorting(get()) }
    factory { ToggleLanguage(get()) }
    factory { ToggleSource(get()) }
    factory { ToggleSourcePin(get()) }
    factory { TrustExtension(get(), get()) }

    single<ExtensionRepoRepository> { ExtensionRepoRepositoryImpl(get()) }
    factory { ExtensionRepoService(get(), get()) }
    factory { GetExtensionRepo(get()) }
    factory { GetExtensionRepoCount(get()) }
    factory { CreateExtensionRepo(get(), get()) }
    factory { DeleteExtensionRepo(get()) }
    factory { ReplaceExtensionRepo(get()) }
    factory { UpdateExtensionRepo(get(), get()) }
    factory { ToggleIncognito(get()) }
    factory { GetIncognitoState(get(), get(), get()) }
}
