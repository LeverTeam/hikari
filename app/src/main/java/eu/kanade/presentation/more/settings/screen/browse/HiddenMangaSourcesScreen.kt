package eu.kanade.presentation.more.settings.screen.browse

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.browse.components.BaseSourceItem
import eu.kanade.presentation.components.AppBar
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.topSmallPaddingValues
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.screens.EmptyScreen
import tachiyomi.presentation.core.screens.LoadingScreen
import tachiyomi.presentation.core.util.Screen

class HiddenMangaSourcesScreen : Screen() {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = rememberScreenModel { HiddenMangaSourcesScreenModel() }
        val state by screenModel.state.collectAsState()

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(MR.strings.label_hidden_manga),
                    navigateUp = navigator::pop,
                    scrollBehavior = scrollBehavior,
                )
            },
        ) { paddingValues ->
            when (val s = state) {
                is HiddenMangaSourcesScreenModel.State.Loading -> LoadingScreen()
                is HiddenMangaSourcesScreenModel.State.Success -> {
                    if (s.sources.isEmpty()) {
                        EmptyScreen(
                            stringRes = MR.strings.no_results_found,
                        )
                    } else {
                        ScrollbarLazyColumn(
                            modifier = Modifier.padding(paddingValues),
                            contentPadding = topSmallPaddingValues,
                        ) {
                            items(
                                items = s.sources,
                                key = { it.id },
                            ) { source ->
                                BaseSourceItem(
                                    source = source,
                                    onClickItem = { navigator.push(HiddenMangaScreen(source.id)) },
                                    onLongClickItem = { /* No-op */ },
                                    action = {
                                        val count = s.countMap[source.id] ?: 0
                                        Text(
                                            text = pluralStringResource(
                                                MR.plurals.num_hidden_manga,
                                                count.toInt(),
                                                count.toInt(),
                                            ),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
