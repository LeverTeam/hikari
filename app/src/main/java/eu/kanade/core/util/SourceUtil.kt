package eu.kanade.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.source.service.SourceManager

@Composable
fun ifSourcesLoaded(): Boolean {
    val sourceManager = koinGet<SourceManager>()
    val isInitialized by remember { sourceManager.isInitialized }.collectAsState()
    return isInitialized
}
