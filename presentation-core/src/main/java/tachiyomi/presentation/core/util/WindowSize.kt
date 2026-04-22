package tachiyomi.presentation.core.util

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration

const val TABLET_UI_REQUIRED_SCREEN_WIDTH_DP = 720

const val TABLET_UI_MIN_SCREEN_WIDTH_PORTRAIT_DP = 700

const val TABLET_UI_MIN_SCREEN_WIDTH_LANDSCAPE_DP = 600

fun Configuration.isTabletUi(): Boolean {
    return smallestScreenWidthDp >= TABLET_UI_REQUIRED_SCREEN_WIDTH_DP
}

@Composable
@ReadOnlyComposable
fun isTabletUi(): Boolean {
    return LocalConfiguration.current.isTabletUi()
}
