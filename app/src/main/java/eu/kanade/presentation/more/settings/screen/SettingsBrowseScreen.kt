package eu.kanade.presentation.more.settings.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.presentation.more.settings.Preference
import eu.kanade.presentation.more.settings.PreferenceItem
import eu.kanade.presentation.more.settings.screen.browse.ExtensionReposScreen
import eu.kanade.presentation.more.settings.screen.browse.HiddenMangaSourcesScreen
import eu.kanade.tachiyomi.util.system.AuthenticatorUtil.authenticate
import kotlinx.collections.immutable.persistentListOf
import hikari.domain.extensionrepo.interactor.GetExtensionRepoCount
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.pluralStringResource
import tachiyomi.presentation.core.i18n.stringResource
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import tachiyomi.presentation.core.components.SectionCard

object SettingsBrowseScreen : SearchableSettings {

    @ReadOnlyComposable
    @Composable
    override fun getTitleRes() = MR.strings.browse

    @Composable
    override fun getPreferences(): List<Preference> {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow

        val sourcePreferences = remember { Injekt.get<SourcePreferences>() }
        val getExtensionRepoCount = remember { Injekt.get<GetExtensionRepoCount>() }

        val reposCount by getExtensionRepoCount.subscribe().collectAsState(0)

        return listOf(
            getSourcesGroup(sourcePreferences, reposCount),
            getNsfwGroup(context, sourcePreferences),
        )
    }

    @Composable
    private fun getSourcesGroup(
        sourcePreferences: SourcePreferences,
        reposCount: Int,
    ): Preference.PreferenceGroup {
        val navigator = LocalNavigator.currentOrThrow
        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.label_sources),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.CustomPreference(
                    title = stringResource(MR.strings.label_sources),
                ) {
                    SectionCard {
                        Column {
                            PreferenceItem(
                                item = Preference.PreferenceItem.SwitchPreference(
                                    preference = sourcePreferences.hideInLibraryItems,
                                    title = stringResource(MR.strings.pref_hide_in_library_items),
                                ),
                                highlightKey = null,
                            )

                            HorizontalDivider()

                            PreferenceItem(
                                item = Preference.PreferenceItem.TextPreference(
                                    title = stringResource(MR.strings.label_extension_repos),
                                    subtitle = pluralStringResource(MR.plurals.num_repos, reposCount, reposCount),
                                    onClick = {
                                        navigator.push(ExtensionReposScreen())
                                    },
                                ),
                                highlightKey = null,
                            )

                            HorizontalDivider()

                            PreferenceItem(
                                item = Preference.PreferenceItem.TextPreference(
                                    title = stringResource(MR.strings.label_hidden_manga),
                                    onClick = {
                                        navigator.push(HiddenMangaSourcesScreen())
                                    },
                                ),
                                highlightKey = null,
                            )

                            HorizontalDivider()

                            PreferenceItem(
                                item = Preference.PreferenceItem.SwitchPreference(
                                    preference = sourcePreferences.autoUpdateExtensions,
                                    title = "Auto-update extensions",
                                ),
                                highlightKey = null,
                            )
                        }
                    }
                },
            ),
        )
    }

    @Composable
    private fun getNsfwGroup(
        context: android.content.Context,
        sourcePreferences: SourcePreferences,
    ): Preference.PreferenceGroup {
        return Preference.PreferenceGroup(
            title = stringResource(MR.strings.pref_category_nsfw_content),
            preferenceItems = persistentListOf(
                Preference.PreferenceItem.CustomPreference(
                    title = stringResource(MR.strings.pref_category_nsfw_content),
                ) {
                    SectionCard {
                        Column {
                            PreferenceItem(
                                item = Preference.PreferenceItem.SwitchPreference(
                                    preference = sourcePreferences.showNsfwSource,
                                    title = stringResource(MR.strings.pref_show_nsfw_source),
                                    subtitle = stringResource(MR.strings.requires_app_restart),
                                    onValueChanged = {
                                        (context as FragmentActivity).authenticate(
                                            title = context.stringResource(MR.strings.pref_category_nsfw_content),
                                        )
                                    },
                                ),
                                highlightKey = null,
                            )

                            PreferenceItem(
                                item = Preference.PreferenceItem.InfoPreference(stringResource(MR.strings.parental_controls_info)),
                                highlightKey = null,
                            )
                        }
                    }
                },
            ),
        )
    }
}
