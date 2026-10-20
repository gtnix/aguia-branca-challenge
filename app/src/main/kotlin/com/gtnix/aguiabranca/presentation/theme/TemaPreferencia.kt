package com.gtnix.aguiabranca.presentation.theme

enum class TemaPreferencia {
    CLARO,
    ESCURO,
    SISTEMA
}

fun resolveDarkTheme(tema: TemaPreferencia, systemDark: Boolean): Boolean = when (tema) {
    TemaPreferencia.CLARO -> false
    TemaPreferencia.ESCURO -> true
    TemaPreferencia.SISTEMA -> systemDark
}
