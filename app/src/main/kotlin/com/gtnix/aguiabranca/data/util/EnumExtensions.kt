package com.gtnix.aguiabranca.data.util

inline fun <reified T : Enum<T>> safeValueOf(name: String, default: T): T =
    try {
        enumValueOf<T>(name)
    } catch (_: Exception) {
        default
    }
