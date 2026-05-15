package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import eu.kanade.tachiyomi.ui.library.LibraryItem
import tachiyomi.domain.history.model.HistoryWithRelations
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.i18n.MR
import tachiyomi.domain.manga.model.asMangaCover
import tachiyomi.presentation.core.components.Badge
import tachiyomi.presentation.core.components.BadgeGroup

@Composable
fun LibraryDashboard(
    items: List<LibraryItem>,
    columns: Int,
    contentPadding: PaddingValues,
    onMangaClick: (LibraryItem, String) -> Unit,
    onMangaLongClick: (LibraryItem) -> Unit,
    onContinueClick: (LibraryItem) -> Unit,
    continueReadingManga: HistoryWithRelations? = null,
) {
    if (items.isEmpty() && continueReadingManga == null) {
        EmptyScreen(
            stringRes = MR.strings.information_empty_library,
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns.coerceAtLeast(3)),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            bottom = contentPadding.calculateBottomPadding(),
            start = MaterialTheme.padding.medium,
            end = MaterialTheme.padding.medium,
        ),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        if (continueReadingManga != null) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                val libraryItem = items.find { it.libraryManga.manga.id == continueReadingManga.mangaId }

                if (libraryItem != null) {
                    DashboardMangaItem(
                        modifier = Modifier.padding(vertical = MaterialTheme.padding.medium),
                        mangaId = libraryItem.libraryManga.manga.id,
                        coverData = libraryItem.libraryManga.manga.asMangaCover(),
                        title = libraryItem.libraryManga.manga.title,
                        subtitle = continueReadingManga.title,
                        footer = "",
                        progress = libraryItem.libraryManga.readCount.toFloat() / libraryItem.libraryManga.totalChapters.coerceAtLeast(
                            1,
                        ),
                        isSharedElementEnabled = true,
                        sharedElementTag = "dashboard",
                        onClick = { onMangaClick(libraryItem, "dashboard") },
                        onLongClick = { onMangaLongClick(libraryItem) },
                        onClickContinue = { onContinueClick(libraryItem) },
                    )
                }
            }
        }

        items(items) { item ->
            MangaComfortableGridItem(
                coverData = item.libraryManga.manga.asMangaCover(),
                title = item.libraryManga.manga.title,
                onClick = { onMangaClick(item, "cover") },
                onLongClick = { onMangaLongClick(item) },
                isSharedElementEnabled = item.libraryManga.manga.id != continueReadingManga?.mangaId,
                sharedElementTag = "cover",
                coverBadgeEnd = {
                    if (item.libraryManga.unreadCount > 0) {
                        BadgeGroup {
                            Badge(text = item.libraryManga.unreadCount.toString())
                        }
                    }
                },
            )
        }
    }
}
