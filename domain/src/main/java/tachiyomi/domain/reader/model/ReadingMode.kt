package tachiyomi.domain.reader.model

enum class ReadingMode(val flagValue: Int) {
    DEFAULT(0x00000000),
    LEFT_TO_RIGHT(0x00000001),
    RIGHT_TO_LEFT(0x00000002),
    VERTICAL(0x00000003),
    WEBTOON(0x00000004),
    CONTINUOUS_VERTICAL(0x00000005),
    ;

    companion object {
        const val MASK = 0x00000007

        fun fromPreference(preference: Int?): ReadingMode = entries.find { it.flagValue == preference } ?: DEFAULT
    }
}
