package eu.kanade.tachiyomi.ui.browse.extension.details

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.preference.EditTextPreference
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceGroup
import androidx.preference.SwitchPreferenceCompat
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.core.util.ifSourcesLoaded
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.more.settings.widget.EditTextPreferenceWidget
import eu.kanade.presentation.more.settings.widget.ListPreferenceWidget
import eu.kanade.presentation.more.settings.widget.MultiSelectListPreferenceWidget
import eu.kanade.presentation.more.settings.widget.PreferenceGroupHeader
import eu.kanade.presentation.more.settings.widget.SwitchPreferenceWidget
import eu.kanade.presentation.more.settings.widget.TextPreferenceWidget
import eu.kanade.tachiyomi.data.preference.SharedPreferencesDataStore
import eu.kanade.tachiyomi.source.ConfigurableSource
import eu.kanade.tachiyomi.source.sourcePreferences
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import tachiyomi.core.common.preference.Preference
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.source.service.SourceManager
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.screens.LoadingScreen
import tachiyomi.presentation.core.util.Screen

class SourcePreferencesScreen(val sourceId: Long) : Screen() {

    @Composable
    override fun Content() {
        val source = remember { koinGet<SourceManager>().getOrStub(sourceId) }
        if (!ifSourcesLoaded() || source !is ConfigurableSource) {
            LoadingScreen()
            return
        }

        val navigator = LocalNavigator.currentOrThrow
        val context = LocalContext.current

        val sourceScreen = remember {
            @SuppressLint("RestrictedApi")
            val manager = androidx.preference.PreferenceManager(context)
            val dataStore = SharedPreferencesDataStore(source.sourcePreferences())
            manager.preferenceDataStore = dataStore
            manager.createPreferenceScreen(context).apply {
                source.setupPreferenceScreen(this)
            }
        }

        Scaffold(
            topBar = {
                AppBar(
                    title = source.toString(),
                    navigateUp = navigator::pop,
                    scrollBehavior = it,
                )
            },
        ) { contentPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(contentPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
            ) {
                RenderPreferences(sourceScreen)
            }
        }
    }

    @Composable
    private fun RenderPreferences(group: PreferenceGroup) {
        (0 until group.preferenceCount).forEach { i ->
            val pref = group.getPreference(i)
            if (pref is PreferenceGroup) {
                val title = pref.title?.toString() ?: ""
                PreferenceGroupHeader(title = title)
                RenderPreferences(pref)
            } else {
                PreferenceItem(pref)
            }
        }
    }

    @Composable
    private fun PreferenceItem(pref: androidx.preference.Preference) {
        val dataStore = (pref.preferenceManager?.preferenceDataStore as? SharedPreferencesDataStore)
            ?: SharedPreferencesDataStore(
                koinGet<SourceManager>().getOrStub(sourceId).let { (it as ConfigurableSource).sourcePreferences() },
            )

        val title = pref.title?.toString() ?: ""
        val summary = pref.summary?.toString()

        when (pref) {
            is SwitchPreferenceCompat -> {
                var checked by remember { mutableStateOf(dataStore.getBoolean(pref.key, pref.isChecked)) }
                SwitchPreferenceWidget(
                    title = title,
                    subtitle = summary,
                    checked = checked,
                    onCheckedChanged = {
                        dataStore.putBoolean(pref.key, it)
                        checked = it
                    },
                )
            }

            is ListPreference -> {
                var value by remember { mutableStateOf(dataStore.getString(pref.key, pref.value) ?: "") }
                val entries = remember(pref) {
                    pref.entryValues.map { it.toString() }.zip(pref.entries.map { it.toString() }).toMap()
                        .toImmutableMap()
                }
                ListPreferenceWidget(
                    value = value,
                    title = title,
                    subtitle = summary,
                    icon = null,
                    entries = entries,
                    onValueChange = {
                        dataStore.putString(pref.key, it)
                        value = it
                    },
                )
            }

            is MultiSelectListPreference -> {
                var values by remember { mutableStateOf(dataStore.getStringSet(pref.key, pref.values) ?: emptySet()) }
                val entries = remember(pref) {
                    pref.entryValues.map { it.toString() }.zip(pref.entries.map { it.toString() }).toMap()
                        .toImmutableMap()
                }
                MultiSelectListPreferenceWidget(
                    preference = eu.kanade.presentation.more.settings.Preference.PreferenceItem.MultiSelectListPreference(
                        preference = object : Preference<Set<String>> {
                            override fun key() = pref.key ?: ""
                            override fun get() = values
                            override fun set(value: Set<String>) {
                                dataStore.putStringSet(pref.key, value.toMutableSet())
                                values = value
                            }

                            override fun defaultValue() = pref.values ?: emptySet()
                            override fun isSet() = dataStore.getStringSet(pref.key, null) != null
                            override fun delete() { /* not implemented */
                            }

                            override fun changes(): Flow<Set<String>> = throw UnsupportedOperationException()
                            override fun stateIn(scope: CoroutineScope): StateFlow<Set<String>> =
                                throw UnsupportedOperationException()
                        },
                        entries = entries,
                        title = title,
                    ),
                    values = values,
                    onValuesChange = {
                        dataStore.putStringSet(pref.key, it.toMutableSet())
                        values = it
                    },
                )
            }

            is EditTextPreference -> {
                var value by remember { mutableStateOf(dataStore.getString(pref.key, pref.text) ?: "") }
                EditTextPreferenceWidget(
                    title = title,
                    subtitle = summary,
                    icon = null,
                    value = value,
                    onConfirm = {
                        dataStore.putString(pref.key, it)
                        value = it
                        true
                    },
                )
            }

            else -> {
                TextPreferenceWidget(
                    title = title,
                    subtitle = summary,
                    onPreferenceClick = { pref.onPreferenceClickListener?.onPreferenceClick(pref) },
                )
            }
        }
    }
}
