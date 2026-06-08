package eu.kanade.presentation.more.settings.screen.data

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.StringResource
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.components.WarningBanner
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.data.backup.create.BackupCreateJob
import eu.kanade.tachiyomi.data.backup.create.BackupCreator
import eu.kanade.tachiyomi.data.backup.create.BackupOptions
import eu.kanade.tachiyomi.util.system.DeviceUtil
import eu.kanade.tachiyomi.util.system.toast
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.update
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.LazyColumnWithAction
import tachiyomi.presentation.core.components.SectionCard
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.util.secondaryItemAlpha

class CreateBackupScreen : Screen() {

    @Composable
    override fun Content() {
        val context = LocalContext.current
        val navigator = LocalNavigator.currentOrThrow
        val model = rememberScreenModel { CreateBackupScreenModel() }
        val state by model.state.collectAsState()

        val chooseBackupDir = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/*"),
        ) {
            if (it != null) {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
                model.createBackup(context, it)
                navigator.pop()
            }
        }

        Scaffold(
            topBar = {
                AppBar(
                    title = stringResource(MR.strings.pref_create_backup),
                    navigateUp = navigator::pop,
                    scrollBehavior = it,
                )
            },
        ) { contentPadding ->
            LazyColumnWithAction(
                contentPadding = contentPadding,
                actionLabel = stringResource(MR.strings.action_create),
                actionEnabled = state.options.canCreate(),
                onClickAction = {
                    if (!BackupCreateJob.isManualJobRunning(context)) {
                        try {
                            chooseBackupDir.launch(BackupCreator.getFilename())
                        } catch (e: ActivityNotFoundException) {
                            context.toast(MR.strings.file_picker_error)
                        }
                    } else {
                        context.toast(MR.strings.backup_in_progress)
                    }
                },
            ) {
                if (DeviceUtil.isMiui && DeviceUtil.isMiuiOptimizationDisabled()) {
                    item {
                        WarningBanner(MR.strings.restore_miui_warning)
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = MaterialTheme.padding.medium, vertical = MaterialTheme.padding.small),
                    ) {
                        Text(
                            text = stringResource(MR.strings.backup_choice),
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = stringResource(MR.strings.pref_create_backup_summ),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.secondaryItemAlpha(),
                        )
                    }
                }

                item {
                    SectionCard(MR.strings.label_library) {
                        Options(BackupOptions.libraryOptions, state, model)
                    }
                }

                item {
                    SectionCard(MR.strings.label_settings) {
                        Options(BackupOptions.settingsOptions, state, model)
                    }
                }
            }
        }
    }

    @Composable
    private fun Options(
        options: ImmutableList<BackupOptions.Entry>,
        state: CreateBackupScreenModel.State,
        model: CreateBackupScreenModel,
    ) {
        options.forEachIndexed { index, option ->
            if (index > 0) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.padding.medium),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                )
            }
            val label = stringResource(option.label)
            val checked = option.getter(state.options)
            val enabled = option.enabled(state.options)
            ListItem(
                headlineContent = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                supportingContent = getSubtitleForOption(option.label)?.let {
                    {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.secondaryItemAlpha(),
                        )
                    }
                },
                trailingContent = {
                    Switch(
                        checked = checked,
                        onCheckedChange = {
                            model.toggle(option.setter, it)
                        },
                        enabled = enabled,
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = androidx.compose.ui.graphics.Color.Transparent,
                ),
            )
        }
    }

    @Composable
    @ReadOnlyComposable
    private fun getSubtitleForOption(label: StringResource): String? {
        return when (label) {
            MR.strings.manga -> "Includes library entries, custom titles, and details."
            MR.strings.chapters -> "Includes read/unread flags and metadata."
            MR.strings.track -> "Includes remote tracking references and sync progress."
            MR.strings.history -> "Includes reading history logs and duration statistics."
            MR.strings.categories -> "Includes custom categories and sorting arrangements."
            MR.strings.non_library_settings -> "Includes history and read progress for entries not in the library."
            MR.strings.app_settings -> "Includes system preferences, appearance configurations, and reader settings."
            MR.strings.extensionRepo_settings -> "Includes configured external extension repository URLs."
            MR.strings.source_settings -> "Includes source-specific settings and preferences."
            MR.strings.private_settings -> "Includes private settings, passwords, and access tokens."
            else -> null
        }
    }
}

private class CreateBackupScreenModel : StateScreenModel<CreateBackupScreenModel.State>(State()) {

    fun toggle(setter: (BackupOptions, Boolean) -> BackupOptions, enabled: Boolean) {
        mutableState.update {
            it.copy(
                options = setter(it.options, enabled),
            )
        }
    }

    fun createBackup(context: Context, uri: Uri) {
        BackupCreateJob.startNow(context, uri, state.value.options)
    }

    @Immutable
    data class State(
        val options: BackupOptions = BackupOptions(),
    )
}
