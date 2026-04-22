package eu.kanade.tachiyomi.ui.reader.setting


import androidx.annotation.DrawableRes
import dev.icerock.moko.resources.StringResource
import eu.kanade.tachiyomi.R
import eu.kanade.tachiyomi.ui.reader.ReaderActivity
import eu.kanade.tachiyomi.ui.reader.viewer.Viewer
import eu.kanade.tachiyomi.ui.reader.viewer.pager.L2RPagerViewer
import eu.kanade.tachiyomi.ui.reader.viewer.pager.R2LPagerViewer
import eu.kanade.tachiyomi.ui.reader.viewer.pager.VerticalPagerViewer
import eu.kanade.tachiyomi.ui.reader.viewer.webtoon.WebtoonViewer
import tachiyomi.domain.reader.model.ReadingMode
import tachiyomi.i18n.MR

val ReadingMode.flag: Int
    get() = flagValue

val ReadingMode.stringRes: StringResource
    get() = when (this) {
        ReadingMode.DEFAULT -> MR.strings.label_default
        ReadingMode.LEFT_TO_RIGHT -> MR.strings.left_to_right_viewer
        ReadingMode.RIGHT_TO_LEFT -> MR.strings.right_to_left_viewer
        ReadingMode.VERTICAL -> MR.strings.vertical_viewer
        ReadingMode.WEBTOON -> MR.strings.webtoon_viewer
        ReadingMode.CONTINUOUS_VERTICAL -> MR.strings.vertical_plus_viewer
    }

@get:DrawableRes
val ReadingMode.iconRes: Int
    get() = when (this) {
        ReadingMode.DEFAULT -> R.drawable.ic_reader_default_24dp
        ReadingMode.LEFT_TO_RIGHT -> R.drawable.ic_reader_ltr_24dp
        ReadingMode.RIGHT_TO_LEFT -> R.drawable.ic_reader_rtl_24dp
        ReadingMode.VERTICAL -> R.drawable.ic_reader_vertical_24dp
        ReadingMode.WEBTOON -> R.drawable.ic_reader_webtoon_24dp
        ReadingMode.CONTINUOUS_VERTICAL -> R.drawable.ic_reader_continuous_vertical_24dp
    }

val ReadingMode.direction: ReadingModeDirection?
    get() = when (this) {
        ReadingMode.DEFAULT -> null
        ReadingMode.LEFT_TO_RIGHT -> ReadingModeDirection.Horizontal
        ReadingMode.RIGHT_TO_LEFT -> ReadingModeDirection.Horizontal
        ReadingMode.VERTICAL -> ReadingModeDirection.Vertical
        ReadingMode.WEBTOON -> ReadingModeDirection.Vertical
        ReadingMode.CONTINUOUS_VERTICAL -> ReadingModeDirection.Vertical
    }

val ReadingMode.type: ReadingModeType?
    get() = when (this) {
        ReadingMode.DEFAULT -> null
        ReadingMode.LEFT_TO_RIGHT -> ReadingModeType.Pager
        ReadingMode.RIGHT_TO_LEFT -> ReadingModeType.Pager
        ReadingMode.VERTICAL -> ReadingModeType.Pager
        ReadingMode.WEBTOON -> ReadingModeType.Webtoon
        ReadingMode.CONTINUOUS_VERTICAL -> ReadingModeType.Webtoon
    }

fun ReadingMode.toViewer(activity: ReaderActivity): Viewer {
    return when (this) {
        ReadingMode.LEFT_TO_RIGHT -> L2RPagerViewer(activity)
        ReadingMode.RIGHT_TO_LEFT -> R2LPagerViewer(activity)
        ReadingMode.VERTICAL -> VerticalPagerViewer(activity)
        ReadingMode.WEBTOON -> WebtoonViewer(activity)
        ReadingMode.CONTINUOUS_VERTICAL -> WebtoonViewer(activity, isContinuous = false)
        ReadingMode.DEFAULT -> throw IllegalStateException("Preference value must be resolved")
    }
}

fun ReadingMode.Companion.isPagerType(preference: Int): Boolean {
    val mode = fromPreference(preference)
    return mode.type == ReadingModeType.Pager
}

fun ReadingMode.Companion.toViewer(preference: Int, activity: ReaderActivity): Viewer {
    return fromPreference(preference).toViewer(activity)
}

enum class ReadingModeDirection {
    Horizontal,
    Vertical,
}

enum class ReadingModeType {
    Pager,
    Webtoon,
}
