package com.gtnix.aguiabranca.domain.session

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserSession @Inject constructor() {
    var perfil: PerfilUsuario = PerfilUsuario.OPERADOR
    var userId: String = "demo-user"
    var userName: String = "Usuário Demo"
    var area: AreaAtuacao = AreaAtuacao.OPERACOES

    fun clear() {
        perfil = PerfilUsuario.OPERADOR
        userId = "demo-user"
        userName = "Usuário Demo"
        area = AreaAtuacao.OPERACOES
    }
}
