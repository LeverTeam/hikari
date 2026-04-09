package eu.kanade.presentation.browse.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.library.components.CommonMangaItemDefaults
import eu.kanade.presentation.library.components.MangaComfortableGridItem
import eu.kanade.tachiyomi.source.CatalogueSource
import tachiyomi.domain.manga.model.Manga
import tachiyomi.domain.manga.model.asMangaCover
import tachiyomi.presentation.core.components.material.padding

@Composable
fun GlobalSearchGroupedContent(
    items: Map<String, List<Pair<CatalogueSource, Manga>>>,
    contentPadding: PaddingValues,
    getManga: @Composable (Manga) -> State<Manga>,
    onClickItem: (Manga) -> Unit,
    onLongClickItem: (Manga) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
    ) {
        items(
            items = items.toList(),
            key = { it.first },
        ) { (title, sources) ->
            val primaryMangaPair = sources.firstOrNull() ?: return@items
            val primaryManga by getManga(primaryMangaPair.second)

            Column(modifier = Modifier.padding(MaterialTheme.padding.extraSmall)) {
                MangaComfortableGridItem(
                    title = primaryManga.title,
                    coverData = primaryManga.asMangaCover(),
                    isSelected = primaryManga.favorite,
                    coverAlpha = if (primaryManga.favorite) CommonMangaItemDefaults.BrowseFavoriteCoverAlpha else 1f,
                    onClick = { onClickItem(primaryManga) },
                    onLongClick = { onLongClickItem(primaryManga) },
                )

                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    sources.forEach { (source, manga) ->
                        SuggestionChip(
                            onClick = { onClickItem(manga) },
                            label = {
                                Text(
                                    text = source.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
