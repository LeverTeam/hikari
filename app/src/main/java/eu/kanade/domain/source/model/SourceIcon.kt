package eu.kanade.domain.source.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import eu.kanade.tachiyomi.extension.ExtensionManager
import tachiyomi.core.common.util.koinGet
import tachiyomi.domain.source.model.Source

val Source.icon: ImageBitmap?
    @Composable
    get() = koinGet<ExtensionManager>().getAppIconForSource(id)?.toBitmap()?.asImageBitmap()
