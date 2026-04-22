package tachiyomi.domain.reader.model

enum class ReaderOrientation(val flagValue: Int) {
    DEFAULT(0x00000000),
    FREE(0x00000008),
    PORTRAIT(0x00000010),
    LANDSCAPE(0x00000018),
    LOCKED_PORTRAIT(0x00000020),
    LOCKED_LANDSCAPE(0x00000028),
    REVERSE_PORTRAIT(0x00000030),
    ;

    companion object {
        const val MASK = 0x00000038

        fun fromPreference(preference: Int?): ReaderOrientation = entries.find { it.flagValue == preference } ?: DEFAULT
    }
}
