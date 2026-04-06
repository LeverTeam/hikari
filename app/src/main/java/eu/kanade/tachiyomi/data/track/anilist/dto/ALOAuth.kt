package eu.kanade.tachiyomi.data.track.anilist.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ALOAuth(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Long,
    val expires: Long = System.currentTimeMillis() + expiresIn * 1000,
)

fun ALOAuth.isExpired() = System.currentTimeMillis() > (expires - 60 * 1000)
