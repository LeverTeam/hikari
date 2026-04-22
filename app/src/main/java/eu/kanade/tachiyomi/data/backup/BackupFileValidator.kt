package eu.kanade.tachiyomi.data.backup

import android.content.Context
import android.net.Uri
import eu.kanade.tachiyomi.data.track.TrackerManager
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.source.service.SourceManager

class BackupFileValidator(
    private val context: Context,

    private val sourceManager: SourceManager = koinGet(),
    private val trackerManager: TrackerManager = koinGet(),
) {

    /**
     * Checks for critical backup file data.
     *
     * @return List of missing sources or missing trackers.
     */
    fun validate(uri: Uri): Results {
        val backup = try {
            BackupDecoder(context).decode(uri)
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }

        val sources = backup.backupSources.associate { it.sourceId to it.name }
        val missingSources = sources
            .filter { sourceManager.get(it.key) == null }
            .values.map {
                val id = it.toLongOrNull()
                if (id == null) {
                    it
                } else {
                    sourceManager.getOrStub(id).toString()
                }
            }
            .distinct()
            .sorted()

        val trackers = backup.backupManga
            .flatMap { it.tracking }
            .map { it.syncId }
            .distinct()
        val missingTrackers = trackers
            .mapNotNull { trackerManager.get(it.toLong()) }
            .filter { !it.isLoggedIn }
            .map { it.name }
            .sorted()

        return Results(
            missingSources,
            missingTrackers,
            mangaCount = backup.backupManga.size,
            categoryCount = backup.backupCategories.size,
            sourceCount = backup.backupSources.size,
            preferenceCount = backup.backupPreferences.size,
        )
    }

    data class Results(
        val missingSources: List<String>,
        val missingTrackers: List<String>,
        val mangaCount: Int = 0,
        val categoryCount: Int = 0,
        val sourceCount: Int = 0,
        val preferenceCount: Int = 0,
    )
}
