package com.gtnix.aguiabranca.presentation.navigation

/**
 * Destinos de Navegação - Rotas Type-Safe
 *
 * ## Conceito FIAP - Material 05A (Navegação)
 *
 * Usamos `sealed class` para definir todas as rotas possíveis do app.
 * Isso garante:
 *
 * 1. **Type-Safety**: Erros de digitação são pegos em compile-time
 * 2. **Exhaustive when**: Kotlin obriga tratar todos os casos
 * 3. **Documentação**: Todas as rotas estão centralizadas
 *
 * ### Estrutura de uma Rota
 *
 * ```
 * "home/{perfil}?area={area}"
 *  ├── path: "home"
 *  ├── required arg: perfil (no path)
 *  └── optional arg: area (query param)
 * ```
 *
 * ### Uso no código
 *
 * ```kotlin
 * // Navegar para Home com perfil
 * navController.navigate(Destination.Home.createRoute("GESTOR"))
 *
 * // Recuperar argumento na tela
 * composable(Destination.Home.route) { backStackEntry ->
 *     val perfil = backStackEntry.arguments?.getString("perfil")
 *     HomeScreen(perfil = perfil)
 * }
 * ```
 */
sealed class Destination(val route: String) {

    // =========================================================================
    // AUTENTICAÇÃO
    // =========================================================================

    /**
     * Tela de Login - Ponto de entrada do app.
     */
    data object Login : Destination("login")

    // =========================================================================
    // MAIN (Após login)
    // =========================================================================

    /**
     * Home/Dashboard - Tela principal após login.
     *
     * @param perfil Perfil do usuário (OPERADOR, GESTOR, LIDER)
     */
    data object Home : Destination("home/{perfil}") {
        const val ARG_PERFIL = "perfil"

        fun createRoute(perfil: String): String {
            return "home/$perfil"
        }
    }

    // =========================================================================
    // IDEIAS
    // =========================================================================

    /**
     * Lista de ideias.
     */
    data object Ideias : Destination("ideias")

    /**
     * Nova ideia - Formulário para submeter.
     */
    data object NovaIdeia : Destination("ideias/nova")

    /**
     * Detalhes de uma ideia.
     *
     * @param ideiaId ID da ideia
     */
    data object IdeiaDetalhe : Destination("ideias/{ideiaId}") {
        const val ARG_IDEIA_ID = "ideiaId"

        fun createRoute(ideiaId: String): String {
            return "ideias/$ideiaId"
        }
    }

    // =========================================================================
    // PROJETOS
    // =========================================================================

    /**
     * Lista de projetos.
     */
    data object Projetos : Destination("projetos")

    /**
     * Novo projeto - Formulário para criar.
     * Aceita ideiaId opcional para conversão de ideia em projeto.
     */
    data object NovoProjeto : Destination("projetos/novo?ideiaId={ideiaId}") {
        const val ARG_IDEIA_ID = "ideiaId"

        fun createRoute(ideiaId: String? = null): String {
            return if (ideiaId != null) "projetos/novo?ideiaId=$ideiaId"
            else "projetos/novo"
        }
    }

    /**
     * Detalhes de um projeto.
     *
     * @param projetoId ID do projeto
     */
    data object ProjetoDetalhe : Destination("projetos/{projetoId}") {
        const val ARG_PROJETO_ID = "projetoId"

        fun createRoute(projetoId: String): String {
            return "projetos/$projetoId"
        }
    }

    // =========================================================================
    // ORIENTAÇÕES ESTRATÉGICAS
    // =========================================================================

    /**
     * Lista de orientações estratégicas.
     */
    data object Orientacoes : Destination("orientacoes")

    /**
     * Nova orientação - Apenas LIDER.
     */
    data object NovaOrientacao : Destination("orientacoes/nova")

    // =========================================================================
    // PERFIL
    // =========================================================================

    /**
     * Perfil do usuário.
     */
    data object Perfil : Destination("perfil")

    // =========================================================================
    // INOVAÇÃO ABERTA
    // =========================================================================

    /**
     * Radar de Inovação - Startups parceiras recomendadas.
     * Acessível apenas para LIDER e GESTOR.
     */
    data object Radar : Destination("radar")

    // =========================================================================
    // LEADER DASHBOARD
    // =========================================================================

    /**
     * Dashboard Executivo do Líder - Resumo com IA, métricas e KPIs.
     * Acessível apenas para LIDER.
     */
    data object LeaderDashboard : Destination("leader_dashboard")
}

/**
 * Itens do Bottom Navigation Bar.
 *
 * Define quais destinos aparecem na barra inferior.
 */
enum class BottomNavItem(
    val destination: Destination,
    val label: String,
    val icon: String // Nome do ícone Material
) {
    HOME(Destination.Home, "Home", "home"),
    IDEIAS(Destination.Ideias, "Ideias", "lightbulb"),
    PROJETOS(Destination.Projetos, "Projetos", "folder"),
    PERFIL(Destination.Perfil, "Perfil", "person")
}
