package com.gtnix.aguiabranca.data.session

interface TokenStore {
    fun getAccessToken(): String?
    fun save(accessToken: String, expiresInSeconds: Long)
    fun clear()
}
