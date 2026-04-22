package tachiyomi.domain.source.model

data class Source(
    val id: Long,
    val lang: String,
    val name: String,
    val supportsLatest: Boolean,
    val isStub: Boolean,
    val pin: Pins = Pins.unpinned,
    val isUsedLast: Boolean = false,
) {

    val visualName: String
        get() = when {
            lang.isEmpty() -> name
            else -> "$name (${lang.uppercase()})"
        }

    val key: () -> String = {
        when {
            isUsedLast -> "$id-lastused"
            else -> "$id"
        }
    }
}

object LocalSource {
    const val ID = 0L
}

fun Source.isLocal(): Boolean = id == LocalSource.ID

fun eu.kanade.tachiyomi.source.Source.isLocal(): Boolean = id == LocalSource.ID
