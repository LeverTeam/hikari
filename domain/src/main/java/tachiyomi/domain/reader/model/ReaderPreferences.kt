package tachiyomi.domain.reader.model

import dev.icerock.moko.resources.StringResource
import tachiyomi.i18n.MR

enum class FlashColor {
    BLACK,
    WHITE,
    WHITE_BLACK,
}

enum class TappingInvertMode(
    val titleRes: StringResource,
    val shouldInvertHorizontal: Boolean = false,
    val shouldInvertVertical: Boolean = false,
) {
    NONE(MR.strings.tapping_inverted_none),
    HORIZONTAL(MR.strings.tapping_inverted_horizontal, shouldInvertHorizontal = true),
    VERTICAL(MR.strings.tapping_inverted_vertical, shouldInvertVertical = true),
    BOTH(MR.strings.tapping_inverted_both, shouldInvertHorizontal = true, shouldInvertVertical = true),
}

enum class ReaderHideThreshold(val threshold: Int) {
    HIGHEST(5),
    HIGH(13),
    LOW(31),
    LOWEST(47),
}
