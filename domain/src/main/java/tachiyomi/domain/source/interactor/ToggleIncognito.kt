package tachiyomi.domain.source.interactor

import tachiyomi.core.common.preference.getAndSet
import tachiyomi.domain.source.service.SourcePreferences

class ToggleIncognito(
    private val preferences: SourcePreferences,
) {
    fun await(extensions: String, enable: Boolean) {
        preferences.incognitoExtensions.getAndSet {
            if (enable) it.plus(extensions) else it.minus(extensions)
        }
    }
}
