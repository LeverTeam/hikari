package eu.kanade.domain.source.interactor

import eu.kanade.domain.base.BasePreferences
import eu.kanade.tachiyomi.extension.ExtensionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import tachiyomi.domain.source.service.SourcePreferences

class GetIncognitoState(
    private val basePreferences: BasePreferences,
    private val sourcePreferences: SourcePreferences,
    private val extensionManager: ExtensionManager,
) {

    fun subscribe(sourceId: Long?): Flow<Boolean> {
        return combine(
            basePreferences.incognitoMode.changes(),
            sourcePreferences.incognitoExtensions.changes(),
            extensionManager.installedExtensionsFlow,
        ) { incognitoMode, incognitoExtensions, _ ->
            incognitoMode || (sourceId != null && isExtensionIncognito(sourceId, incognitoExtensions))
        }
    }

    fun await(sourceId: Long?): Boolean {
        return basePreferences.incognitoMode.get() ||
            (sourceId != null && isExtensionIncognito(sourceId, sourcePreferences.incognitoExtensions.get()))
    }

    private fun isExtensionIncognito(sourceId: Long, incognitoExtensions: Set<String>): Boolean {
        val pkgName = extensionManager.getExtensionPackage(sourceId) ?: return false
        return pkgName in incognitoExtensions
    }
}
