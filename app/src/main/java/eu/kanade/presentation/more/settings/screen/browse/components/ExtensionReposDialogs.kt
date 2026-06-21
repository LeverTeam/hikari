package eu.kanade.presentation.more.settings.screen.browse.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import eu.kanade.presentation.components.AdaptiveSheet
import tachiyomi.presentation.core.components.material.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.KeyboardType
import hikari.domain.extensionrepo.model.ExtensionRepo
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.coroutines.delay
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.i18n.stringResource
import kotlin.time.Duration.Companion.seconds

@Composable
fun ExtensionRepoCreateDialog(
    onDismissRequest: () -> Unit,
    onCreate: (String) -> Unit,
    repoUrls: ImmutableSet<String>,
) {
    var name by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val nameAlreadyExists = remember(name) { repoUrls.contains(name) }

    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.imePadding(),
        header = {
            Text(
                text = stringResource(MR.strings.action_add_repo),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.padding.medium,
                        top = MaterialTheme.padding.small,
                        end = MaterialTheme.padding.medium,
                        bottom = MaterialTheme.padding.small,
                    ),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.padding.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        ) {
            Text(text = stringResource(MR.strings.action_add_repo_message, stringResource(MR.strings.app_name)))

            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = name,
                onValueChange = { name = it },
                label = {
                    Text(text = stringResource(MR.strings.label_add_repo_input))
                },
                supportingText = {
                    val msgRes = if (name.isNotEmpty() && nameAlreadyExists) {
                        MR.strings.error_repo_exists
                    } else {
                        MR.strings.information_required_plain
                    }
                    Text(text = stringResource(msgRes))
                },
                isError = name.isNotEmpty() && nameAlreadyExists,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    enabled = name.isNotEmpty() && !nameAlreadyExists,
                    onClick = {
                        onCreate(name)
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_add))
                }
            }
        }
    }

    LaunchedEffect(focusRequester) {
        // TODO: https://issuetracker.google.com/issues/204502668
        delay(0.3.seconds)
        focusRequester.requestFocus()
        keyboardController?.show()
    }
}

@Composable
fun ExtensionRepoDeleteDialog(
    onDismissRequest: () -> Unit,
    onDelete: () -> Unit,
    repo: String,
) {
    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        header = {
            Text(
                text = stringResource(MR.strings.action_delete_repo),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.padding.medium,
                        top = MaterialTheme.padding.small,
                        end = MaterialTheme.padding.medium,
                        bottom = MaterialTheme.padding.small,
                    ),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.padding.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        ) {
            Text(text = stringResource(MR.strings.delete_repo_confirmation, repo))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onDelete()
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_ok))
                }
            }
        }
    }
}

@Composable
fun ExtensionRepoConflictDialog(
    oldRepo: ExtensionRepo,
    newRepo: ExtensionRepo,
    onDismissRequest: () -> Unit,
    onMigrate: () -> Unit,
) {
    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        header = {
            Text(
                text = stringResource(MR.strings.action_replace_repo_title),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.padding.medium,
                        top = MaterialTheme.padding.small,
                        end = MaterialTheme.padding.medium,
                        bottom = MaterialTheme.padding.small,
                    ),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.padding.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        ) {
            Text(text = stringResource(MR.strings.action_replace_repo_message, newRepo.name, oldRepo.name))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onMigrate()
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_replace_repo))
                }
            }
        }
    }
}

@Composable
fun ExtensionRepoConfirmDialog(
    onDismissRequest: () -> Unit,
    onCreate: () -> Unit,
    repo: String,
) {
    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        header = {
            Text(
                text = stringResource(MR.strings.action_add_repo),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = MaterialTheme.padding.medium,
                        top = MaterialTheme.padding.small,
                        end = MaterialTheme.padding.medium,
                        bottom = MaterialTheme.padding.small,
                    ),
            )
        },
    ) {
        Column(
            modifier = Modifier.padding(MaterialTheme.padding.medium),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.medium),
        ) {
            Text(text = stringResource(MR.strings.add_repo_confirmation, repo))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
            ) {
                OutlinedButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismissRequest,
                ) {
                    Text(text = stringResource(MR.strings.action_cancel))
                }
                FilledTonalButton(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onCreate()
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_add))
                }
            }
        }
    }
}
