package com.gtnix.aguiabranca.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gtnix.aguiabranca.presentation.screens.auth.LoginScreen
import com.gtnix.aguiabranca.presentation.screens.home.HomeScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.IdeiaDetalheScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.IdeiasScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.NovaIdeiaScreen
import com.gtnix.aguiabranca.presentation.screens.inovacao.RadarScreen
import com.gtnix.aguiabranca.presentation.screens.perfil.PerfilScreen
import com.gtnix.aguiabranca.presentation.screens.projetos.NovoProjetoScreen
import com.gtnix.aguiabranca.presentation.screens.projetos.ProjetoDetalheScreen
import com.gtnix.aguiabranca.presentation.screens.projetos.ProjetosScreen

/**
 * AppNavGraph - Configuração de Navegação
 *
 * ## Conceito FIAP - Material 05A (Navegação)
 *
 * O NavHost é o container que gerencia a pilha de navegação.
 * Cada `composable` define uma tela e sua rota.
 *
 * ### Estrutura
 *
 * ```
 * NavHost
 *     │
 *     ├── composable("login") → LoginScreen
 *     │
 *     ├── composable("home/{perfil}") → HomeScreen
 *     │       │
 *     │       └── navArgument("perfil") - Argumento obrigatório
 *     │
 *     ├── composable("ideias") → IdeiasScreen
 *     │
 *     └── composable("projetos") → ProjetosScreen
 * ```
 *
 * ### NavController
 *
 * Controla a navegação entre telas:
 * - `navigate(route)`: Vai para uma tela
 * - `popBackStack()`: Volta para tela anterior
 * - `currentBackStackEntry`: Tela atual
 *
 * ### Exemplo de navegação
 *
 * ```kotlin
 * // Na LoginScreen
 * Button(onClick = {
 *     navController.navigate(Destination.Home.createRoute("GESTOR")) {
 *         // Remove Login da pilha (não volta com back)
 *         popUpTo(Destination.Login.route) { inclusive = true }
 *     }
 * })
 * ```
 */
@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Destination.Login.route
) {
    // Guarda ações de navegação para passar às telas
    val navigationActions = remember(navController) {
        NavigationActions(navController)
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // =====================================================================
        // LOGIN
        // =====================================================================
        composable(route = Destination.Login.route) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = { perfil ->
                    // Navega para Home e remove Login da pilha
                    navController.navigate(Destination.Home.createRoute(perfil.name)) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // =====================================================================
        // HOME
        // =====================================================================
        composable(
            route = Destination.Home.route,
            arguments = listOf(
                navArgument(Destination.Home.ARG_PERFIL) {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val perfil = backStackEntry.arguments?.getString(Destination.Home.ARG_PERFIL) ?: "OPERADOR"

            HomeScreen(
                viewModel = hiltViewModel(),
                perfil = perfil,
                onNavigateToIdeias = {
                    navController.navigate(Destination.Ideias.route)
                },
                onNavigateToProjetos = {
                    navController.navigate(Destination.Projetos.route)
                },
                onNavigateToOrientacoes = {
                    navController.navigate(Destination.Orientacoes.route)
                },
                onNavigateToPerfil = {
                    navController.navigate(Destination.Perfil.route)
                },
                onNavigateToNovaIdeia = {
                    navController.navigate(Destination.NovaIdeia.route)
                },
                onNavigateToRadar = {
                    navController.navigate(Destination.Radar.route)
                }
            )
        }

        // =====================================================================
        // IDEIAS
        // =====================================================================
        composable(route = Destination.Ideias.route) {
            IdeiasScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNovaIdeia = {
                    navController.navigate(Destination.NovaIdeia.route)
                },
                onNavigateToDetalhe = { ideiaId ->
                    navController.navigate(Destination.IdeiaDetalhe.createRoute(ideiaId))
                }
            )
        }

        composable(route = Destination.NovaIdeia.route) {
            NovaIdeiaScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onIdeiaCreated = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Destination.IdeiaDetalhe.route,
            arguments = listOf(
                navArgument(Destination.IdeiaDetalhe.ARG_IDEIA_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            IdeiaDetalheScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNovoProjeto = { ideiaId ->
                    navController.navigate(Destination.NovoProjeto.createRoute(ideiaId))
                }
            )
        }

        // =====================================================================
        // PROJETOS
        // =====================================================================
        composable(route = Destination.Projetos.route) {
            ProjetosScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNovoProjeto = {
                    navController.navigate(Destination.NovoProjeto.createRoute())
                },
                onNavigateToDetalhe = { projetoId ->
                    navController.navigate(Destination.ProjetoDetalhe.createRoute(projetoId))
                }
            )
        }

        composable(
            route = Destination.ProjetoDetalhe.route,
            arguments = listOf(
                navArgument(Destination.ProjetoDetalhe.ARG_PROJETO_ID) {
                    type = NavType.StringType
                }
            )
        ) {
            ProjetoDetalheScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Destination.NovoProjeto.route,
            arguments = listOf(
                navArgument(Destination.NovoProjeto.ARG_IDEIA_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            NovoProjetoScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onProjetoCreated = {
                    navController.popBackStack()
                }
            )
        }

        // =====================================================================
        // ORIENTAÇÕES (Placeholder)
        // =====================================================================
        composable(route = Destination.Orientacoes.route) {
            // TODO: Implementar OrientacoesScreen
        }

        // =====================================================================
        // PERFIL
        // =====================================================================
        composable(route = Destination.Perfil.route) {
            PerfilScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // =====================================================================
        // RADAR DE INOVAÇÃO
        // =====================================================================
        composable(route = Destination.Radar.route) {
            RadarScreen(
                viewModel = hiltViewModel(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * Encapsula ações de navegação para facilitar uso nas telas.
 */
class NavigationActions(private val navController: NavHostController) {

    fun navigateToHome(perfil: String) {
        navController.navigate(Destination.Home.createRoute(perfil)) {
            popUpTo(Destination.Login.route) { inclusive = true }
        }
    }

    fun navigateToIdeias() {
        navController.navigate(Destination.Ideias.route)
    }

    fun navigateToProjetos() {
        navController.navigate(Destination.Projetos.route)
    }

    fun navigateBack() {
        navController.popBackStack()
    }
}
