package eu.kanade.presentation.library.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import eu.kanade.presentation.category.visualName
import tachiyomi.domain.category.model.Category
import tachiyomi.presentation.core.components.material.padding

@Composable
internal fun LibraryTabs(
    categories: List<Category>,
    pagerState: PagerState,
    getItemCountForCategory: (Category) -> Int?,
    onTabItemClick: (Int) -> Unit,
) {
    val currentPageIndex = pagerState.currentPage.coerceAtMost(categories.lastIndex)
    val scrollState = rememberScrollState()

    val chipPositions = remember { mutableMapOf<Int, Pair<Float, Int>>() }
    var rowWidth by remember { mutableIntStateOf(0) }

    LaunchedEffect(currentPageIndex, rowWidth) {
        val (offset, width) = chipPositions[currentPageIndex] ?: return@LaunchedEffect
        if (rowWidth > 0) {
            val targetScroll = (offset - (rowWidth / 2f) + (width / 2f)).coerceIn(0f, scrollState.maxValue.toFloat())
            scrollState.animateScrollTo(targetScroll.toInt())
        }
    }

    Row(
        modifier = Modifier
            .zIndex(2f)
            .onGloballyPositioned { rowWidth = it.size.width }
            .horizontalScroll(scrollState)
            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        categories.forEachIndexed { index, category ->
            val selected = currentPageIndex == index
            val name = category.visualName
            val count = getItemCountForCategory(category)
            FilterChip(
                selected = selected,
                onClick = { onTabItemClick(index) },
                modifier = Modifier.onGloballyPositioned { coords ->
                    chipPositions[index] = coords.positionInParent().x to coords.size.width
                },
                label = {
                    Text(
                        text = if (count != null) "$name ($count)" else name,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
                border = null,
                shape = MaterialTheme.shapes.medium,
            )
        }
    }
}
