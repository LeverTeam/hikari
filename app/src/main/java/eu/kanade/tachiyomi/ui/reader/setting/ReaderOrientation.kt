package eu.kanade.tachiyomi.ui.reader.setting


import android.content.pm.ActivityInfo
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenLockLandscape
import androidx.compose.material.icons.filled.ScreenLockPortrait
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.ui.graphics.vector.ImageVector
import dev.icerock.moko.resources.StringResource
import tachiyomi.domain.reader.model.ReaderOrientation
import tachiyomi.i18n.MR

val ReaderOrientation.stringRes: StringResource
    get() = when (this) {
        ReaderOrientation.DEFAULT -> MR.strings.label_default
        ReaderOrientation.FREE -> MR.strings.rotation_free
        ReaderOrientation.PORTRAIT -> MR.strings.rotation_portrait
        ReaderOrientation.LANDSCAPE -> MR.strings.rotation_landscape
        ReaderOrientation.LOCKED_PORTRAIT -> MR.strings.rotation_force_portrait
        ReaderOrientation.LOCKED_LANDSCAPE -> MR.strings.rotation_force_landscape
        ReaderOrientation.REVERSE_PORTRAIT -> MR.strings.rotation_reverse_portrait
    }

val ReaderOrientation.icon: ImageVector
    get() = when (this) {
        ReaderOrientation.DEFAULT -> Icons.Default.ScreenRotation
        ReaderOrientation.FREE -> Icons.Default.ScreenRotation
        ReaderOrientation.PORTRAIT -> Icons.Default.StayCurrentPortrait
        ReaderOrientation.LANDSCAPE -> Icons.Default.StayCurrentLandscape
        ReaderOrientation.LOCKED_PORTRAIT -> Icons.Default.ScreenLockPortrait
        ReaderOrientation.LOCKED_LANDSCAPE -> Icons.Default.ScreenLockLandscape
        ReaderOrientation.REVERSE_PORTRAIT -> Icons.Default.StayCurrentPortrait
    }

val ReaderOrientation.activityFlags: Int
    get() = when (this) {
        ReaderOrientation.DEFAULT -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        ReaderOrientation.FREE -> ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        ReaderOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        ReaderOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        ReaderOrientation.LOCKED_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        ReaderOrientation.LOCKED_LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        ReaderOrientation.REVERSE_PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT
    }

val ReaderOrientation.flag: Int
    get() = flagValue
