package tachiyomi.domain.manga.interactor

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.repository.MangaRepository

class GetHiddenManga(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(sourceId: Long): List<Manga> {
        return mangaRepository.getHiddenMangaBySourceId(sourceId)
    }

    suspend fun await(): List<Manga> {
        return mangaRepository.getHiddenManga()
    }

    fun subscribe(sourceId: Long): Flow<List<Manga>> {
        return mangaRepository.getHiddenMangaBySourceIdAsFlow(sourceId)
    }

    fun subscribeSourceIds(): Flow<Map<Long, Long>> {
        return mangaRepository.getSourceIdsWithHiddenManga()
    }
}
