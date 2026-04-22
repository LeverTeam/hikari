package eu.kanade.presentation.more.settings.screen.browse

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import eu.kanade.domain.manga.interactor.UpdateManga
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import tachiyomi.core.common.util.koinGet
import tachiyomi.core.common.util.lang.launchIO
import tachiyomi.domain.manga.interactor.GetHiddenManga
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.toMangaUpdate
import tachiyomi.domain.source.model.Source
import tachiyomi.domain.source.service.SourceManager

class HiddenMangaScreenModel(
    private val sourceId: Long,
    private val getHiddenManga: GetHiddenManga = koinGet(),
    private val sourceManager: SourceManager = koinGet(),
    private val updateManga: UpdateManga = koinGet(),
) : StateScreenModel<HiddenMangaScreenModel.State>(State()) {

    var source: Source? by mutableStateOf(null)
        private set

    init {
        val s = sourceManager.getOrStub(sourceId)
        source = Source(
            id = s.id,
            lang = s.lang,
            name = s.name,
            supportsLatest = false,
            isStub = false,
        )

        screenModelScope.launchIO {
            getHiddenManga.subscribe(sourceId).collectLatest { mangaList ->
                mutableState.update { it.copy(mangaList = mangaList.toImmutableList()) }
            }
        }
    }

    fun search(query: String?) {
        mutableState.update { it.copy(searchQuery = query) }
    }

    fun toggleSelection(manga: Manga) {
        mutableState.update { state ->
            val newSelected = state.selected.toMutableList().apply {
                if (contains(manga)) remove(manga) else add(manga)
            }
            state.copy(selected = newSelected)
        }
    }

    fun selectAll(selection: Boolean) {
        mutableState.update { state ->
            val newSelected = if (selection) state.mangaList else emptyList()
            state.copy(selected = newSelected)
        }
    }

    fun invertSelection() {
        mutableState.update { state ->
            val newSelected = state.mangaList.filter { !state.selected.contains(it) }
            state.copy(selected = newSelected)
        }
    }

    fun unhideSelected() {
        screenModelScope.launchIO {
            val selectedManga = state.value.selected
            val updates = selectedManga.map { it.toMangaUpdate().copy(hidden = false) }
            updateManga.awaitAll(updates)
            selectAll(false)
        }
    }

    fun unhide(manga: Manga) {
        screenModelScope.launchIO {
            updateManga.await(manga.toMangaUpdate().copy(hidden = false))
        }
    }

    data class State(
        val mangaList: ImmutableList<Manga> = kotlinx.collections.immutable.persistentListOf(),
        val selected: List<Manga> = emptyList(),
        val searchQuery: String? = null,
    ) {
        val selectionMode: Boolean = selected.isNotEmpty()

        val filteredMangaList: ImmutableList<Manga>
            get() {
                val query = searchQuery
                return if (query.isNullOrBlank()) {
                    mangaList
                } else {
                    mangaList.filter { it.title.contains(query, ignoreCase = true) }.toImmutableList()
                }
            }
    }
}
