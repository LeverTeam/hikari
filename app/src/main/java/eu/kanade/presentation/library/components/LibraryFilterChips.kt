package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.category.visualName
import tachiyomi.domain.category.model.Category

@Composable
internal fun LibraryFilterChips(
    categories: List<Category>,
    pagerState: PagerState,
    getItemCountForCategory: (Category) -> Int?,
    onTabItemClick: (Int) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)

    LaunchedEffect(currentPageIndex) {
        lazyListState.animateScrollToItem(currentPageIndex)
    }

    LazyRow(
        state = lazyListState,
        modifier = Modifier
            .padding(vertical = 8.dp)
            .graphicsLayer {
                translationX = -pagerState.currentPageOffsetFraction * 32.dp.toPx()
            },
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        itemsIndexed(categories, key = { _, category -> category.id }) { index, category ->
            val selected = currentPageIndex == index
            val count = getItemCountForCategory(category)

            FilterChip(
                selected = selected,
                onClick = { onTabItemClick(index) },
                label = {
                    Text(
                        text = if (count != null && count > 0) {
                            "${category.visualName} ($count)"
                        } else {
                            category.visualName
                        },
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                            letterSpacing = 0.5.sp,
                        ),
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                border = null,
            )
        }
    }
}
