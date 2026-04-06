package eu.kanade.tachiyomi.data.track.anilist

import eu.kanade.tachiyomi.data.track.anilist.dto.ALOAuth
import eu.kanade.tachiyomi.data.track.anilist.dto.isExpired
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class AnilistInterceptor(val anilist: Anilist) : Interceptor {

    private var oauth: ALOAuth? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (oauth == null) {
            oauth = anilist.loadOAuth()
        }

        if (oauth == null || oauth!!.isExpired()) {
            anilist.logout()
            throw IOException("Token expired")
        }

        val authRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${oauth!!.accessToken}")
            .header("Accept", "application/json")
            .build()

        return chain.proceed(authRequest)
    }

    fun setAuth(oauth: ALOAuth?) {
        this.oauth = oauth
        anilist.saveOAuth(oauth)
    }
}
