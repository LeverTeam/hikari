package tachiyomi.domain.manga.interactor

import kotlinx.coroutines.flow.Flow
import tachiyomi.domain.manga.repository.MangaRepository

class GetExcludedScanlators(
    private val mangaRepository: MangaRepository,
) {

    suspend fun await(mangaId: Long): Set<String> {
        return mangaRepository.getExcludedScanlators(mangaId)
    }

    fun subscribe(mangaId: Long): Flow<Set<String>> {
        return mangaRepository.getExcludedScanlatorsAsFlow(mangaId)
    }
}
