package eu.kanade.presentation.more.settings.screen.about

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.SystemUpdate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import eu.kanade.presentation.components.AppBar
import eu.kanade.presentation.more.LogoHeader
import eu.kanade.presentation.util.LocalBackPress
import eu.kanade.presentation.util.Screen
import eu.kanade.tachiyomi.BuildConfig
import eu.kanade.tachiyomi.data.updater.AppUpdateChecker
import eu.kanade.tachiyomi.data.updater.RELEASE_URL
import eu.kanade.tachiyomi.ui.more.NewUpdateScreen
import kotlinx.coroutines.launch
import logcat.LogPriority
import tachiyomi.core.common.i18n.stringResource
import tachiyomi.core.common.util.lang.toDateTimestampString
import tachiyomi.core.common.util.lang.withIOContext
import tachiyomi.core.common.util.lang.withUIContext
import tachiyomi.core.common.util.system.logcat
import tachiyomi.domain.release.interactor.GetApplicationRelease
import tachiyomi.domain.ui.UiPreferences
import tachiyomi.i18n.MR
import tachiyomi.presentation.core.components.CommunityTile
import tachiyomi.presentation.core.components.HikariSnackbarHost
import tachiyomi.presentation.core.components.ScrollbarLazyColumn
import tachiyomi.presentation.core.components.SectionCard
import tachiyomi.presentation.core.components.material.Scaffold
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource
import tachiyomi.presentation.core.icons.CustomIcons
import tachiyomi.presentation.core.icons.Discord
import tachiyomi.presentation.core.icons.Facebook
import tachiyomi.presentation.core.icons.Github
import tachiyomi.presentation.core.icons.Reddit
import tachiyomi.presentation.core.icons.X
import uy.kohesive.injekt.Injekt
import uy.kohesive.injekt.api.get
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

object AboutScreen : Screen() {

    @Composable
    override fun Content() {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        val handleBack = LocalBackPress.current
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        var isCheckingUpdates by remember { mutableStateOf(false) }

        Scaffold(
            topBar = { scrollBehavior ->
                AppBar(
                    title = stringResource(MR.strings.pref_category_about),
                    navigateUp = if (handleBack != null) handleBack::invoke else null,
                    scrollBehavior = scrollBehavior,
                )
            },
            snackbarHost = { HikariSnackbarHost(hostState = snackbarHostState) },
        ) { contentPadding ->
            ScrollbarLazyColumn(
                contentPadding = contentPadding,
            ) {
                item {
                    LogoHeader(
                        versionName = getVersionName(withBuildDate = true),
                    )
                }

                item {
                    SectionCard(
                        titleRes = MR.strings.pref_category_about,
                        highEmphasis = true,
                        showAccent = false,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.padding.medium)
                                .padding(vertical = MaterialTheme.padding.small),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            ) {
                                CommunityTile(
                                    label = stringResource(MR.strings.check_for_updates),
                                    icon = Icons.Outlined.SystemUpdate,
                                    horizontal = true,
                                    modifier = Modifier.weight(1f),
                                    trailingWidget = {
                                        AnimatedVisibility(visible = isCheckingUpdates) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp,
                                            )
                                        }
                                    },
                                    onClick = {
                                        if (!isCheckingUpdates) {
                                            scope.launch {
                                                isCheckingUpdates = true
                                                checkVersion(
                                                    context = context,
                                                    onAvailableUpdate = { result ->
                                                        val updateScreen = NewUpdateScreen(
                                                            versionName = result.release.version,
                                                            changelogInfo = result.release.info,
                                                            releaseLink = result.release.releaseLink,
                                                            downloadLink = result.release.downloadLink,
                                                        )
                                                        navigator.push(updateScreen)
                                                    },
                                                    onFinish = { isCheckingUpdates = false },
                                                    onError = { message ->
                                                        scope.launch { snackbarHostState.showSnackbar(message) }
                                                    },
                                                    onNoUpdate = {
                                                        scope.launch {
                                                            snackbarHostState.showSnackbar(
                                                                context.stringResource(MR.strings.update_check_no_new_updates),
                                                            )
                                                        }
                                                    },
                                                )
                                            }
                                        }
                                    },
                                )
                                CommunityTile(
                                    label = stringResource(MR.strings.licenses),
                                    icon = Icons.Outlined.Gavel,
                                    horizontal = true,
                                    modifier = Modifier.weight(1f),
                                    onClick = { navigator.push(OpenSourceLicensesScreen()) },
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            ) {
                                if (!BuildConfig.DEBUG) {
                                    CommunityTile(
                                        label = stringResource(MR.strings.whats_new),
                                        icon = Icons.Outlined.Info,
                                        horizontal = true,
                                        modifier = Modifier.weight(1f),
                                        url = RELEASE_URL,
                                    )
                                }
                                CommunityTile(
                                    label = stringResource(MR.strings.privacy_policy),
                                    icon = Icons.Outlined.Policy,
                                    horizontal = true,
                                    modifier = Modifier.weight(1f),
                                    url = "https://mihon.app/privacy/",
                                )
                            }
                        }
                    }
                }

                item {
                    SectionCard(
                        titleRes = MR.strings.label_more,
                        highEmphasis = true,
                        showAccent = false,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = MaterialTheme.padding.medium)
                                .padding(vertical = MaterialTheme.padding.small),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            ) {
                                CommunityTile(
                                    label = stringResource(MR.strings.website),
                                    icon = Icons.Outlined.Public,
                                    url = "https://mihon.app",
                                    modifier = Modifier.weight(1f),
                                )
                                CommunityTile(
                                    label = "Discord",
                                    icon = CustomIcons.Discord,
                                    url = "https://discord.gg/mihon",
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            ) {
                                CommunityTile(
                                    label = "X",
                                    icon = CustomIcons.X,
                                    url = "https://x.com/mihonapp",
                                    modifier = Modifier.weight(1f),
                                )
                                CommunityTile(
                                    label = "Facebook",
                                    icon = CustomIcons.Facebook,
                                    url = "https://facebook.com/mihonapp",
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.padding.small),
                            ) {
                                CommunityTile(
                                    label = "Reddit",
                                    icon = CustomIcons.Reddit,
                                    url = "https://www.reddit.com/r/mihonapp",
                                    modifier = Modifier.weight(1f),
                                )
                                CommunityTile(
                                    label = "GitHub",
                                    icon = CustomIcons.Github,
                                    url = "https://github.com/mihonapp",
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks version and shows a user prompt if an update is available.
     */
    private suspend fun checkVersion(
        context: Context,
        onAvailableUpdate: (GetApplicationRelease.Result.NewUpdate) -> Unit,
        onFinish: () -> Unit,
        onNoUpdate: () -> Unit,
        onError: (String) -> Unit,
    ) {
        val updateChecker = AppUpdateChecker()
        withUIContext {
            try {
                when (val result = withIOContext { updateChecker.checkForUpdate(context, forceCheck = true) }) {
                    is GetApplicationRelease.Result.NewUpdate -> {
                        onAvailableUpdate(result)
                    }

                    is GetApplicationRelease.Result.NoNewUpdate -> {
                        onNoUpdate()
                    }

                    is GetApplicationRelease.Result.OsTooOld -> {
                        onError(context.stringResource(MR.strings.update_check_eol))
                    }
                }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
                logcat(LogPriority.ERROR, e)
            } finally {
                onFinish()
            }
        }
    }

    fun getVersionName(withBuildDate: Boolean): String {
        return when {
            BuildConfig.DEBUG -> {
                "Debug ${BuildConfig.COMMIT_SHA}".let {
                    if (withBuildDate) {
                        "$it (${getFormattedBuildTime()})"
                    } else {
                        it
                    }
                }
            }

            else -> {
                "Stable ${BuildConfig.VERSION_NAME}".let {
                    if (withBuildDate) {
                        "$it (${getFormattedBuildTime()})"
                    } else {
                        it
                    }
                }
            }
        }
    }

    internal fun getFormattedBuildTime(): String {
        return try {
            LocalDateTime.ofInstant(
                Instant.parse(BuildConfig.BUILD_TIME),
                ZoneId.systemDefault(),
            )
                .toDateTimestampString(
                    UiPreferences.dateFormat(
                        Injekt.get<UiPreferences>().dateFormat.get(),
                    ),
                )
        } catch (_: Exception) {
            BuildConfig.BUILD_TIME
        }
    }
}
