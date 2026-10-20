package com.gtnix.aguiabranca.data.remote.interceptor

import com.gtnix.aguiabranca.data.session.SessionEventBus
import com.gtnix.aguiabranca.data.session.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UnauthorizedInterceptor @Inject constructor(
    private val tokenStore: TokenStore,
    private val sessionEventBus: SessionEventBus
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        val path = request.url.encodedPath
        if (response.code == 401 && !path.endsWith("/auth/login")) {
            tokenStore.clear()
            sessionEventBus.emitExpired()
        }
        return response
    }
}
