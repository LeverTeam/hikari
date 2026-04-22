package eu.kanade.presentation.manga.components

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import coil3.compose.AsyncImage
import eu.kanade.tachiyomi.R
import tachiyomi.presentation.core.util.LocalNavAnimatedVisibilityScope
import tachiyomi.presentation.core.util.LocalSharedTransitionScope
import tachiyomi.presentation.core.util.mangaSharedElement
import tachiyomi.presentation.core.util.rememberResourceBitmapPainter
import tachiyomi.domain.manga.model.MangaCover as MangaCoverModel

enum class MangaCover(val ratio: Float) {
    Square(1f / 1f),
    Book(2f / 3f),
    ;

    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    operator fun invoke(
        data: Any?,
        modifier: Modifier = Modifier,
        contentDescription: String = "",
        shape: Shape = MaterialTheme.shapes.extraSmall,
        onClick: (() -> Unit)? = null,
        mangaId: Long? = null,
    ) {
        val sharedTransitionScope = LocalSharedTransitionScope.current
        val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current

        val id = mangaId ?: when (data) {
            is MangaCoverModel -> data.mangaId
            else -> null
        }

        val sharedElementModifier = Modifier.mangaSharedElement("cover", id)

        AsyncImage(
            model = data,
            placeholder = ColorPainter(CoverPlaceholderColor),
            error = rememberResourceBitmapPainter(id = R.drawable.cover_error),
            contentDescription = contentDescription,
            modifier = modifier
                .then(sharedElementModifier)
                .aspectRatio(ratio)
                .clip(shape)
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            role = Role.Button,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
            contentScale = ContentScale.Crop,
        )
    }
}

private val CoverPlaceholderColor = Color(0x1F888888)
