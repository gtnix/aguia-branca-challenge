package com.gtnix.aguiabranca.data.util

import java.security.MessageDigest

object PasswordHasher {

    fun hash(password: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(password.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { byte -> "%02x".format(byte) }
    }
}
