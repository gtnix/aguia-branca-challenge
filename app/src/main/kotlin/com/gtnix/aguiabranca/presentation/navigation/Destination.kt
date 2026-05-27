package com.gtnix.aguiabranca.presentation.navigation

sealed class Destination(val route: String) {

    data object Login : Destination("login")

    data object TabHome : Destination("home")

    data object Home : Destination("home/{perfil}") {
        const val ARG_PERFIL = "perfil"
        fun createRoute(perfil: String): String = "home/$perfil"
    }

    data object Ideias : Destination("ideias")

    data object NovaIdeia : Destination("ideias/nova")

    data object IdeiaDetalhe : Destination("ideias/{ideiaId}") {
        const val ARG_IDEIA_ID = "ideiaId"
        fun createRoute(ideiaId: String): String = "ideias/$ideiaId"
    }

    data object Projetos : Destination("projetos")

    data object NovoProjeto : Destination("projetos/novo?ideiaId={ideiaId}") {
        const val ARG_IDEIA_ID = "ideiaId"
        fun createRoute(ideiaId: String? = null): String =
            if (ideiaId != null) "projetos/novo?ideiaId=$ideiaId" else "projetos/novo"
    }

    data object ProjetoDetalhe : Destination("projetos/{projetoId}") {
        const val ARG_PROJETO_ID = "projetoId"
        fun createRoute(projetoId: String): String = "projetos/$projetoId"
    }

    data object Orientacoes : Destination("orientacoes")

    data object NovaOrientacao : Destination("orientacoes/nova")

    data object OrientacaoDetalhe : Destination("orientacoes/{orientacaoId}") {
        const val ARG_ORIENTACAO_ID = "orientacaoId"
        fun createRoute(orientacaoId: String): String = "orientacoes/$orientacaoId"
    }

    data object EditarOrientacao : Destination("orientacoes/editar/{orientacaoId}") {
        const val ARG_ORIENTACAO_ID = "orientacaoId"
        fun createRoute(orientacaoId: String): String = "orientacoes/editar/$orientacaoId"
    }

    data object Perfil : Destination("perfil")

    data object Ranking : Destination("ranking")

    data object Radar : Destination("radar")

    data object LeaderDashboard : Destination("leader_dashboard")

    data object Notificacoes : Destination("notificacoes")
}
