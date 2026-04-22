package tachiyomi.domain.download.service

import eu.kanade.tachiyomi.source.Source
import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.chapter.model.Chapter
import tachiyomi.domain.download.model.Download
import tachiyomi.domain.manga.model.Manga

interface DownloadManager {
    fun getQueuedDownloadOrNull(chapterId: Long): Download?
    fun statusFlow(): Flow<Download>
    fun progressFlow(): Flow<Download>

    fun isChapterDownloaded(
        chapterName: String,
        scanlator: String?,
        chapterUrl: String,
        mangaTitle: String,
        sourceId: Long,
        skipCache: Boolean = false,
    ): Boolean

    suspend fun renameChapter(source: Source, manga: Manga, oldChapter: Chapter, newChapter: Chapter)
    fun cancelQueuedDownloads(chapterIds: List<Long>)
    fun readdDownloadsToStartOfQueue(chapterIds: List<Long>)
}

interface DownloadProvider {
    fun isChapterDirNameChanged(oldChapter: Chapter, newChapter: Chapter): Boolean
}
