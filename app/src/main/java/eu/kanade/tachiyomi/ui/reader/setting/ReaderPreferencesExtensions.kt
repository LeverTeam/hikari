package eu.kanade.tachiyomi.ui.reader.setting

import android.os.Build
import androidx.compose.ui.graphics.BlendMode
import tachiyomi.domain.reader.service.ReaderPreferences
import tachiyomi.i18n.MR

val ReaderPreferences.Companion.ColorFilterMode
    get() = buildList {
        addAll(
            listOf(
                MR.strings.label_default to BlendMode.SrcOver,
                MR.strings.filter_mode_multiply to BlendMode.Modulate,
                MR.strings.filter_mode_screen to BlendMode.Screen,
            ),
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addAll(
                listOf(
                    MR.strings.filter_mode_overlay to BlendMode.Overlay,
                    MR.strings.filter_mode_lighten to BlendMode.Lighten,
                    MR.strings.filter_mode_darken to BlendMode.Darken,
                ),
            )
        }
    }
