package tachiyomi.presentation.core.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import tachiyomi.presentation.core.components.material.TextButton

@Composable
fun HikariSnackbar(
    snackbarData: SnackbarData,
    modifier: Modifier = Modifier,
) {
    val visuals = snackbarData.visuals
    val actionLabel = visuals.actionLabel

    val icon = remember(visuals.message) {
        when {
            visuals.message.contains("error", ignoreCase = true) ||
                visuals.message.contains("fail", ignoreCase = true) -> Icons.Outlined.ErrorOutline

            visuals.message.contains("success", ignoreCase = true) ||
                visuals.message.contains("complete", ignoreCase = true) -> Icons.Outlined.CheckCircle

            else -> Icons.Outlined.Info
        }
    }

    Surface(
        modifier = modifier.padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = when (icon) {
                    Icons.Outlined.ErrorOutline -> MaterialTheme.colorScheme.error
                    Icons.Outlined.CheckCircle -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.primary
                },
            )

            Text(
                text = visuals.message,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, end = 8.dp),
                style = MaterialTheme.typography.bodyMedium,
            )

            if (actionLabel != null) {
                TextButton(
                    onClick = { snackbarData.performAction() },
                ) {
                    Text(
                        text = actionLabel,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}
