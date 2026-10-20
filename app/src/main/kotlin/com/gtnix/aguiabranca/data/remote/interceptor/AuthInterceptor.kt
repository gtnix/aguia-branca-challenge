package com.gtnix.aguiabranca.data.remote.interceptor

import com.gtnix.aguiabranca.data.session.TokenStore
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenStore: TokenStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath
        if (path.endsWith("/auth/login") || path.endsWith("/health")) {
            return chain.proceed(original)
        }
        val token = tokenStore.getAccessToken() ?: return chain.proceed(original)
        val authenticated = original.newBuilder()
            .header(HEADER_AUTHORIZATION, "$SCHEME_BEARER $token")
            .build()
        return chain.proceed(authenticated)
    }

    private companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val SCHEME_BEARER = "Bearer"
    }
}
