package eu.kanade.presentation.more

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.manga.components.MarkdownRender
import eu.kanade.presentation.theme.TachiyomiPreviewTheme
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import androidx.compose.foundation.layout.windowInsetsPadding

@Composable
fun NewUpdateScreen(
    versionName: String,
    changelogInfo: String,
    onOpenInBrowser: () -> Unit,
    onRejectUpdate: () -> Unit,
    onAcceptUpdate: () -> Unit,
) {
    Scaffold(
        topBar = { scrollBehavior ->
            AppBar(
                title = stringResource(MR.strings.update_check_notification_update_available),
                subtitle = versionName,
                navigateUp = onRejectUpdate,
                actions = {
                    IconButton(onClick = onOpenInBrowser) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            contentDescription = stringResource(MR.strings.update_check_open),
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(NavigationBarDefaults.windowInsets)
                    .padding(MaterialTheme.padding.medium),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onAcceptUpdate,
                ) {
                    Text(text = stringResource(MR.strings.update_check_confirm))
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onRejectUpdate,
                ) {
                    Text(text = stringResource(MR.strings.action_not_now))
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.padding.medium),
        ) {
            MarkdownRender(
                content = changelogInfo,
                flavour = GFMFlavourDescriptor(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun NewUpdateScreenPreview() {
    TachiyomiPreviewTheme {
        NewUpdateScreen(
            versionName = "v0.99.9",
            changelogInfo = """
                ## Yay
                Foobar

                ### More info
                - Hello
                - World
            """.trimIndent(),
            onOpenInBrowser = {},
            onRejectUpdate = {},
            onAcceptUpdate = {},
        )
    }
}
