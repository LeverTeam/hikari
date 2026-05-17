package eu.kanade.presentation.browse.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import eu.kanade.presentation.history.components.ItemPosition
import tachiyomi.presentation.core.components.material.padding

@Composable
fun BaseBrowseItem(
    modifier: Modifier = Modifier,
    position: ItemPosition? = null,
    onClickItem: () -> Unit = {},
    onLongClickItem: () -> Unit = {},
    icon: @Composable RowScope.() -> Unit = {},
    action: @Composable RowScope.() -> Unit = {},
    content: @Composable RowScope.() -> Unit = {},
) {
    if (position == null) {
        Row(
            modifier = modifier
                .combinedClickable(
                    onClick = onClickItem,
                    onLongClick = onLongClickItem,
                )
                .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            icon()
            content()
            action()
        }
    } else {
        val shape = when (position) {
            ItemPosition.First -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            ItemPosition.Last -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
            ItemPosition.Single -> RoundedCornerShape(12.dp)
            ItemPosition.Middle -> RoundedCornerShape(0.dp)
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    start = MaterialTheme.padding.medium,
                    end = MaterialTheme.padding.medium,
                    bottom = if (position == ItemPosition.Last || position == ItemPosition.Single) MaterialTheme.padding.small else 0.dp,
                )
                .clip(shape)
                .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
                .combinedClickable(
                    onClick = onClickItem,
                    onLongClick = onLongClickItem,
                ),
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                content()
                action()
            }
            if (position != ItemPosition.Last && position != ItemPosition.Single) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )
            }
        }
    }
}

