package eu.kanade.presentation.manga.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import eu.kanade.presentation.components.AdaptiveSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import eu.kanade.tachiyomi.util.system.isReleaseBuildType
import kotlinx.collections.immutable.toImmutableList
import tachiyomi.domain.manga.interactor.FetchInterval
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.WheelTextPicker
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun DeleteChaptersDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
) {
    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        header = {
            Text(
                text = stringResource(MR.strings.are_you_sure),
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
            Text(text = stringResource(MR.strings.confirm_delete_chapters))

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
                        onDismissRequest()
                        onConfirm()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_ok))
                }
            }
        }
    }
}

@Composable
fun SetIntervalDialog(
    interval: Int,
    onDismissRequest: () -> Unit,
    onValueChanged: ((Int) -> Unit)? = null,
) {
    var selectedInterval by rememberSaveable { mutableIntStateOf(if (interval < 0) -interval else 0) }

    AdaptiveSheet(
        onDismissRequest = onDismissRequest,
        header = {
            Text(
                text = stringResource(MR.strings.pref_library_update_smart_update),
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
            Text(
                stringResource(MR.strings.manga_interval_expected_update_null),
            )

            if (onValueChanged != null && (!isReleaseBuildType)) {
                Text(stringResource(MR.strings.manga_interval_custom_amount))

                BoxWithConstraints(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    val size = DpSize(width = maxWidth / 2, height = 128.dp)
                    val items = (0..FetchInterval.MAX_INTERVAL)
                        .map {
                            if (it == 0) {
                                stringResource(MR.strings.label_default)
                            } else {
                                it.toString()
                            }
                        }
                        .toImmutableList()
                    WheelTextPicker(
                        items = items,
                        size = size,
                        startIndex = selectedInterval,
                        onSelectionChanged = { selectedInterval = it },
                    )
                }
            }

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
                        onValueChanged?.invoke(selectedInterval)
                        onDismissRequest()
                    },
                ) {
                    Text(text = stringResource(MR.strings.action_ok))
                }
            }
        }
    }
}
