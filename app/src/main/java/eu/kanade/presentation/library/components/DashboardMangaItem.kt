package eu.kanade.presentation.library.components

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import eu.kanade.presentation.manga.components.MangaCover
import eu.kanade.presentation.util.bounceClick
import eu.kanade.presentation.util.mangaSharedElement
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel
import tachiyomi.presentation.core.components.material.padding

@Composable
fun DashboardMangaItem(
    mangaId: Long,
    coverData: MangaCoverModel,
    title: String,
    subtitle: String,
    footer: String,
    progress: Float,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false,
    onClickContinue: (() -> Unit)? = null,
    onClickMenu: (() -> Unit)? = null,
    isSharedElementEnabled: Boolean = true,
    sharedElementTag: String = "cover",
) {
    val coverHeight = if (isCompact) 80.dp else 120.dp
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .bounceClick()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(if (isCompact) 4.dp else MaterialTheme.padding.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MangaCover.Book(
            modifier = Modifier.height(coverHeight),
            shape = MaterialTheme.shapes.small,
            data = coverData,
            mangaId = if (isSharedElementEnabled) mangaId else null,
            tag = sharedElementTag,
        )

        Spacer(modifier = Modifier.width(if (isCompact) MaterialTheme.padding.small else MaterialTheme.padding.medium))

        Column(
            modifier = Modifier
                .weight(1f)
                .height(coverHeight),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Text(
                    text = title,
                    style = if (isCompact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = if (isCompact) 1 else 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .mangaSharedElement("$sharedElementTag-title", if (isSharedElementEnabled) mangaId else null),
                )

                if (onClickMenu != null && !isCompact) {
                    IconButton(
                        onClick = onClickMenu,
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Column {
                Text(
                    text = subtitle,
                    style = if (isCompact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!isCompact) {
                    Text(
                        text = footer,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isCompact) 4.dp else 6.dp)
                            .clip(MaterialTheme.shapes.small),
                        strokeCap = StrokeCap.Round,
                    )
                }

                Spacer(modifier = Modifier.width(MaterialTheme.padding.small))

                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = if (isCompact) 8.sp else 10.sp,
                )

                if (onClickContinue != null && !isCompact) {
                    Spacer(modifier = Modifier.width(MaterialTheme.padding.small))
                    FilledIconButton(
                        onClick = onClickContinue,
                        modifier = Modifier.size(32.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}
