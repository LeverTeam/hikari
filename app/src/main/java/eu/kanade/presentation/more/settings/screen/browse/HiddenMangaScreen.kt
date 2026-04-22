package eu.kanade.presentation.more.settings.screen.browse

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FlipToBack
import androidx.compose.material.icons.outlined.SelectAll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBarTitle
import eu.kanade.presentation.components.SearchToolbar
import eu.kanade.presentation.library.components.CommonMangaItemDefaults
import eu.kanade.presentation.library.components.MangaCompactGridItem
import eu.kanade.presentation.manga.components.MangaBottomActionMenu
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.MangaCover
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.util.Screen
import tachiyomi.presentation.core.util.plus

class HiddenMangaScreen(
    private val sourceId: Long,
) : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HiddenMangaScreenModel(sourceId) }
        val state by screenModel.state.collectAsState()

        BackHandler(enabled = state.selectionMode || state.searchQuery != null) {
            when {
                state.selectionMode -> screenModel.selectAll(false)
                state.searchQuery != null -> screenModel.search(null)
            }
        }

        Scaffold(
            topBar = { scrollBehavior ->
                SearchToolbar(
                    searchQuery = state.searchQuery,
                    onChangeSearchQuery = screenModel::search,
                    titleContent = {
                        AppBarTitle(
                            title = screenModel.source?.visualName ?: stringResource(MR.strings.label_hidden_manga),
                        )
                    },
                    navigateUp = navigator::pop,
                    searchEnabled = !state.selectionMode,
                    actions = {
                        if (state.selectionMode) {
                            var showMenu by remember { mutableStateOf(false) }
                            IconButton(onClick = { screenModel.selectAll(true) }) {
                                Icon(
                                    Icons.Outlined.SelectAll,
                                    contentDescription = stringResource(MR.strings.action_select_all),
                                )
                            }
                            IconButton(onClick = { screenModel.invertSelection() }) {
                                Icon(
                                    Icons.Outlined.FlipToBack,
                                    contentDescription = stringResource(MR.strings.action_select_inverse),
                                )
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            bottomBar = {
                MangaBottomActionMenu(
                    visible = state.selectionMode,
                    modifier = Modifier.fillMaxWidth(),
                    onMarkAsReadClicked = { screenModel.unhideSelected() },
                )
            },
        ) { paddingValues ->
            if (state.mangaList.isEmpty()) {
                EmptyScreen(
                    stringRes = MR.strings.no_results_found,
                    modifier = Modifier.padding(paddingValues),
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(120.dp),
                    modifier = Modifier.padding(paddingValues),
                    contentPadding = PaddingValues(8.dp) + PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(CommonMangaItemDefaults.GridVerticalSpacer),
                    horizontalArrangement = Arrangement.spacedBy(CommonMangaItemDefaults.GridHorizontalSpacer),
                ) {
                    items(
                        items = state.filteredMangaList,
                        key = { it.id },
                    ) { manga ->
                        HiddenMangaGridItem(
                            manga = manga,
                            isSelected = state.selected.contains(manga),
                            onClick = {
                                if (state.selectionMode) {
                                    screenModel.toggleSelection(manga)
                                } else {
                                    /* Navigate to manga info? */
                                }
                            },
                            onLongClick = { screenModel.toggleSelection(manga) },
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun HiddenMangaGridItem(
        manga: Manga,
        isSelected: Boolean,
        onClick: () -> Unit,
        onLongClick: () -> Unit,
    ) {
        MangaCompactGridItem(
            title = manga.title,
            coverData = MangaCover(
                mangaId = manga.id,
                sourceId = manga.source,
                isMangaFavorite = manga.favorite,
                url = manga.thumbnailUrl,
                lastModified = manga.coverLastModified,
            ),
            isSelected = isSelected,
            onClick = onClick,
            onLongClick = onLongClick,
        )
    }
}
