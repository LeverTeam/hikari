package eu.kanade.tachiyomi.ui.browse.source

import androidx.compose.runtime.Immutable
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.source.interactor.GetLanguagesWithSources
import tachiyomi.domain.source.interactor.ToggleLanguage
import tachiyomi.domain.source.interactor.ToggleSource
import tachiyomi.domain.source.model.Source
import tachiyomi.domain.source.service.SourcePreferences
import java.util.SortedMap

class SourcesFilterScreenModel(
    private val preferences: SourcePreferences = koinGet(),
    private val getLanguagesWithSources: GetLanguagesWithSources = koinGet(),
    private val toggleSource: ToggleSource = koinGet(),
    private val toggleLanguage: ToggleLanguage = koinGet(),
) : StateScreenModel<SourcesFilterScreenModel.State>(State.Loading) {

    init {
        screenModelScope.launch {
            combine(
                getLanguagesWithSources.subscribe(),
                preferences.enabledLanguages.changes(),
                preferences.disabledSources.changes(),
            ) { a, b, c -> Triple(a, b, c) }
                .catch { throwable ->
                    mutableState.update {
                        State.Error(
                            throwable = throwable,
                        )
                    }
                }
                .collectLatest { (languagesWithSources, enabledLanguages, disabledSources) ->
                    mutableState.update {
                        State.Success(
                            items = languagesWithSources,
                            enabledLanguages = enabledLanguages,
                            disabledSources = disabledSources,
                        )
                    }
                }
        }
    }

    fun toggleSource(source: Source) {
        toggleSource.await(source)
    }

    fun toggleLanguage(language: String) {
        toggleLanguage.await(language)
    }

    sealed interface State {

        @Immutable
        data object Loading : State

        @Immutable
        data class Error(
            val throwable: Throwable,
        ) : State

        @Immutable
        data class Success(
            val items: SortedMap<String, List<Source>>,
            val enabledLanguages: Set<String>,
            val disabledSources: Set<String>,
        ) : State, KoinComponent {

            val isEmpty: Boolean
                get() = items.isEmpty()
        }
    }
}
