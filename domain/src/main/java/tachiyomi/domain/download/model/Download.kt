package tachiyomi.domain.download.model

import androidx.compose.runtime.Immutable

@Immutable
data class Download(
    val chapterId: Long,
    val mangaId: Long,
    val status: DownloadState,
    val progress: Int,
)
