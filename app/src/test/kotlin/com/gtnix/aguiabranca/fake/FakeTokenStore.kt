package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.data.session.TokenStore

class FakeTokenStore(
    private var token: String? = null
) : TokenStore {

    var cleared: Boolean = false
        private set

    override fun getAccessToken(): String? = token

    override fun save(accessToken: String, expiresInSeconds: Long) {
        token = accessToken
        cleared = false
    }

    override fun clear() {
        token = null
        cleared = true
    }
}
