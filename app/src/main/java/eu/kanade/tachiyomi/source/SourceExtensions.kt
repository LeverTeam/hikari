package eu.kanade.tachiyomi.source

import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.source.model.StubSource
import tachiyomi.domain.source.service.SourcePreferences
import tachiyomi.source.local.isLocal

fun Source.getNameForMangaInfo(): String {
    val preferences = koinGet<SourcePreferences>()
    val enabledLanguages = preferences.enabledLanguages.get()
        .filterNot { it in listOf("all", "other") }
    val hasOneActiveLanguages = enabledLanguages.size == 1
    val isInEnabledLanguages = lang in enabledLanguages
    return when {
        // For edge cases where user disables a source they got manga of in their library.
        hasOneActiveLanguages && !isInEnabledLanguages -> toString()
        // Hide the language tag when only one language is used.
        hasOneActiveLanguages && isInEnabledLanguages -> name
        else -> toString()
    }
}

fun Source.isLocalOrStub(): Boolean = isLocal() || this is StubSource
