package com.gtnix.aguiabranca.presentation.navigation

import com.gtnix.aguiabranca.domain.model.PerfilUsuario

object MainNavPolicy {

    private val TOP_LEVEL_ROUTES = setOf(
        Destination.TabHome.route,
        Destination.Ideias.route,
        Destination.Projetos.route,
        Destination.Perfil.route
    )

    fun showBottomBar(route: String): Boolean {
        return TOP_LEVEL_ROUTES.any { route.startsWith(it) && !route.contains("/") }
    }

    enum class FabAction {
        NOVA_IDEIA,
        NOVO_PROJETO,
        NOVA_ORIENTACAO,
        NONE
    }

    fun fabActionFor(route: String, perfil: PerfilUsuario): FabAction {
        if (!showBottomBar(route)) return FabAction.NONE
        return when (perfil) {
            PerfilUsuario.OPERADOR -> FabAction.NOVA_IDEIA
            PerfilUsuario.GESTOR -> FabAction.NOVO_PROJETO
            PerfilUsuario.LIDER -> FabAction.NOVA_ORIENTACAO
        }
    }
}
