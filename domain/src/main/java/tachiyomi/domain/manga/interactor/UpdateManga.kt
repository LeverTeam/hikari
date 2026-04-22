package tachiyomi.domain.manga.interactor

import tachiyomi.domain.manga.model.Manga
import java.time.ZonedDateTime

interface UpdateManga {
    suspend fun awaitUpdateFetchInterval(
        manga: Manga,
        dateTime: ZonedDateTime = ZonedDateTime.now(),
        window: Pair<Long, Long>,
    ): Boolean

    suspend fun awaitUpdateLastUpdate(mangaId: Long): Boolean
}
