package eu.kanade.tachiyomi.extension.util

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import eu.kanade.domain.source.service.SourcePreferences
import eu.kanade.tachiyomi.data.notification.Notifications
import eu.kanade.tachiyomi.extension.ExtensionManager
import eu.kanade.tachiyomi.extension.model.Extension
import eu.kanade.tachiyomi.util.system.setForegroundSafely
import eu.kanade.tachiyomi.util.system.workManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import logcat.LogPriority
import tachiyomi.core.common.util.lang.withIOContext
import tachiyomi.core.common.util.system.logcat
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.util.concurrent.TimeUnit

class ExtensionUpdateJob(private val context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {

    private val extensionManager: ExtensionManager = Injekt.get()
    private val sourcePreferences: SourcePreferences = Injekt.get()

    override suspend fun doWork(): Result = withIOContext {
        if (!sourcePreferences.autoUpdateExtensions.get()) {
            return@withIOContext Result.success()
        }

        try {
            setForegroundSafely()

            extensionManager.findAvailableExtensions()

            val extensionsToUpdate = extensionManager.installedExtensionsFlow.value
                .filter { it.hasUpdate }

            if (extensionsToUpdate.isNotEmpty()) {
                extensionsToUpdate.forEach { extension ->
                    extensionManager.updateExtension(extension)
                        .onEach { /* Optional: track progress */ }
                        .collect {}
                }
            }

            Result.success()
        } catch (e: Exception) {
            logcat(LogPriority.ERROR, e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "ExtensionUpdate"

        fun setupTask(context: Context, force: Boolean = false) {
            val sourcePreferences: SourcePreferences = Injekt.get()
            val enabled = sourcePreferences.autoUpdateExtensions.get()

            if (enabled || force) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .setRequiresCharging(true)
                    .setRequiresBatteryNotLow(true)
                    .build()

                val request = PeriodicWorkRequestBuilder<ExtensionUpdateJob>(
                    12,
                    TimeUnit.HOURS,
                )
                    .addTag(TAG)
                    .setConstraints(constraints)
                    .setBackoffCriteria(BackoffPolicy.LINEAR, 10, TimeUnit.MINUTES)
                    .build()

                context.workManager.enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    request,
                )
            } else {
                context.workManager.cancelUniqueWork(TAG)
            }
        }
    }
}
