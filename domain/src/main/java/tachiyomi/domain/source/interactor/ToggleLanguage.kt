package tachiyomi.domain.source.interactor

import tachiyomi.core.common.preference.getAndSet
import tachiyomi.domain.source.service.SourcePreferences

class ToggleLanguage(
    val preferences: SourcePreferences,
) {
    fun await(language: String) {
        val isEnabled = language in preferences.enabledLanguages.get()
        preferences.enabledLanguages.getAndSet { enabled ->
            if (isEnabled) enabled.minus(language) else enabled.plus(language)
        }
    }
}
