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
        return when (perfil) {
            PerfilUsuario.OPERADOR -> when (route) {
                Destination.TabHome.route,
                Destination.Ideias.route -> FabAction.NOVA_IDEIA
                else -> FabAction.NONE
            }
            PerfilUsuario.GESTOR -> when (route) {
                Destination.TabHome.route,
                Destination.Projetos.route -> FabAction.NOVO_PROJETO
                else -> FabAction.NONE
            }
            PerfilUsuario.LIDER -> when (route) {
                Destination.TabHome.route -> FabAction.NOVA_ORIENTACAO
                else -> FabAction.NONE
            }
        }
    }
}
