package tachiyomi.domain.download.interactor

import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.manga.model.Manga

interface DeleteDownload {
    suspend fun awaitAll(manga: Manga, vararg chapters: Chapter)
}
