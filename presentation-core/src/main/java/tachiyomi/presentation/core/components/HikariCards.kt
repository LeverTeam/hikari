package tachiyomi.presentation.core.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.util.selectedBackground

enum class HikariListItemPosition {
    First,
    Middle,
    Last,
    Single,
}

object HikariCardDefaults {
    const val BORDER_ALPHA = 0.08f
    const val DIVIDER_ALPHA = 0.5f
    val cardElevation = 1.dp
    val nestedCardElevation = 2.dp

    @Composable
    fun containerColor(elevation: Dp = cardElevation): Color {
        return MaterialTheme.colorScheme.surfaceColorAtElevation(elevation)
    }

    @Composable
    fun borderStroke(
        selected: Boolean = false,
        selectedWidth: Dp = 2.dp,
        unselectedWidth: Dp = 1.dp,
    ): BorderStroke {
        return BorderStroke(
            width = if (selected) selectedWidth else unselectedWidth,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = BORDER_ALPHA)
            },
        )
    }

    @Composable
    fun dividerColor(): Color {
        return MaterialTheme.colorScheme.outlineVariant.copy(alpha = DIVIDER_ALPHA)
    }

    @Composable
    fun groupShape(): Shape {
        return MaterialTheme.shapes.large
    }

    @Composable
    fun itemShape(position: HikariListItemPosition): Shape {
        return when (position) {
            HikariListItemPosition.First -> RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            HikariListItemPosition.Last -> RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            HikariListItemPosition.Single -> RoundedCornerShape(16.dp)
            HikariListItemPosition.Middle -> RoundedCornerShape(0.dp)
        }
    }
}

@Composable
fun HikariCard(
    modifier: Modifier = Modifier,
    shape: Shape = HikariCardDefaults.groupShape(),
    selected: Boolean = false,
    selectedBorderWidth: Dp = 2.dp,
    showBorder: Boolean = true,
    containerColor: Color = HikariCardDefaults.containerColor(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ElevatedCard(
        modifier = modifier
            .then(
                if (showBorder || selected) {
                    Modifier.border(
                        border = HikariCardDefaults.borderStroke(
                            selected = selected,
                            selectedWidth = selectedBorderWidth,
                        ),
                        shape = shape,
                    )
                } else {
                    Modifier
                },
            ),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        shape = shape,
        content = content,
    )
}

@Composable
fun HikariCardGroup(
    modifier: Modifier = Modifier,
    shape: Shape = HikariCardDefaults.groupShape(),
    selected: Boolean = false,
    horizontalPadding: Dp = MaterialTheme.padding.medium,
    verticalPadding: Dp = MaterialTheme.padding.small,
    containerColor: Color = HikariCardDefaults.containerColor(),
    content: @Composable ColumnScope.() -> Unit,
) {
    HikariCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        shape = shape,
        selected = selected,
        containerColor = containerColor,
        content = content,
    )
}

@Composable
fun HikariGroupedListItem(
    position: HikariListItemPosition,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    selectedBorderWidth: Dp = 2.dp,
    enabled: Boolean = true,
    height: Dp? = null,
    horizontalPadding: Dp = MaterialTheme.padding.medium,
    bottomPadding: Dp = if (position == HikariListItemPosition.Last || position == HikariListItemPosition.Single) {
        MaterialTheme.padding.small
    } else {
        0.dp
    },
    containerColor: Color = HikariCardDefaults.containerColor(),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    showDivider: Boolean = position != HikariListItemPosition.Last && position != HikariListItemPosition.Single,
    dividerHorizontalPadding: Dp = MaterialTheme.padding.medium,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = HikariCardDefaults.itemShape(position)
    HikariCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = horizontalPadding,
                end = horizontalPadding,
                bottom = bottomPadding,
            ),
        shape = shape,
        selected = selected,
        selectedBorderWidth = selectedBorderWidth,
        showBorder = false,
        containerColor = containerColor,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .selectedBackground(selected)
                .then(
                    if (onClick != null || onLongClick != null) {
                        Modifier.combinedClickable(
                            enabled = enabled,
                            onClick = { onClick?.invoke() },
                            onLongClick = onLongClick,
                        )
                    } else {
                        Modifier
                    },
                )
                .then(if (height != null) Modifier.height(height) else Modifier),
        ) {
            content()
            if (showDivider) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = dividerHorizontalPadding),
                    thickness = 0.5.dp,
                    color = HikariCardDefaults.dividerColor(),
                )
            }
        }
    }
}

@Composable
fun HikariSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(
            horizontal = MaterialTheme.padding.medium,
            vertical = MaterialTheme.padding.small,
        ),
    )
}
