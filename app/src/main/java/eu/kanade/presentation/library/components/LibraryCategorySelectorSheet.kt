package eu.kanade.presentation.library.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.category.visualName
import eu.kanade.presentation.components.AdaptiveSheet
import tachiyomi.domain.category.model.Category
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.HikariCardDefaults
import tachiyomi.presentation.core.components.HikariGroupedListItem
import tachiyomi.presentation.core.components.HikariListItemPosition
import tachiyomi.presentation.core.components.Pill
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun LibraryCategorySelectorSheet(
    categories: List<Category>,
    currentIndex: Int,
    getItemCountForCategory: (Category) -> Int,
    onDismissRequest: () -> Unit,
    onSelectCategory: (Int) -> Unit,
) {
    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(),
        header = {
            Text(
                text = stringResource(MR.strings.label_library),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.padding.medium,
                        top = MaterialTheme.padding.small,
                        end = MaterialTheme.padding.medium,
                        bottom = MaterialTheme.padding.small,
                    ),
            )
        },
    ) {
        LazyColumn(
            modifier = Modifier.padding(vertical = MaterialTheme.padding.small),
        ) {
            itemsIndexed(categories) { index, category ->
                val selected = index == currentIndex
                HikariGroupedListItem(
                    position = itemPosition(index, categories.lastIndex),
                    selected = selected,
                    selectedBorderWidth = 1.dp,
                    containerColor = HikariCardDefaults.containerColor(2.dp),
                    onClick = {
                        onSelectCategory(index)
                        onDismissRequest()
                    },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = MaterialTheme.padding.medium,
                                vertical = MaterialTheme.padding.small,
                            ),
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = category.visualName,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Pill(
                            text = "${getItemCountForCategory(category)}",
                            color = MaterialTheme.colorScheme.surfaceContainerHighest,
                            fontSize = 12.sp,
                        )
                        RadioButton(
                            selected = selected,
                            onClick = null,
                        )
                    }
                }
            }
        }
    }
}

private fun itemPosition(index: Int, lastIndex: Int): HikariListItemPosition {
    return when {
        lastIndex <= 0 -> HikariListItemPosition.Single
        index == 0 -> HikariListItemPosition.First
        index == lastIndex -> HikariListItemPosition.Last
        else -> HikariListItemPosition.Middle
    }
}
