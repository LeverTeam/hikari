package tachiyomi.presentation.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.StringResource
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    titleRes: StringResource? = null,
    highEmphasis: Boolean = false,
    showAccent: Boolean = true,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.padding.medium),
    ) {
        if (titleRes != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = MaterialTheme.padding.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showAccent) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .height(if (highEmphasis) 28.dp else 20.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(2.dp),
                            ),
                    )
                }

                Text(
                    modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                    text = stringResource(titleRes),
                    style = if (highEmphasis) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                    color = color,
                )
            }
        }

        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun LazyItemScope.SectionCard(
    titleRes: StringResource? = null,
    highEmphasis: Boolean = false,
    showAccent: Boolean = true,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
    content: @Composable ColumnScope.() -> Unit,
) {
    SectionCard(
        modifier = Modifier,
        titleRes = titleRes,
        highEmphasis = highEmphasis,
        showAccent = showAccent,
        color = color,
        content = content,
    )
}
