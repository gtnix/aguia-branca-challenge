package com.gtnix.aguiabranca.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.screens.MainScreen
import com.gtnix.aguiabranca.presentation.screens.auth.LoginScreen

private const val TRANSITION_DURATION = 300

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
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Start,
                animationSpec = tween(TRANSITION_DURATION)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.End,
                animationSpec = tween(TRANSITION_DURATION)
            )
        }
    ) {
        // =====================================================================
        // LOGIN — uses fade instead of slide
        // =====================================================================
        composable(
            route = Destination.Login.route,
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            exitTransition = { fadeOut(tween(TRANSITION_DURATION)) }
        ) {
            LoginScreen(
                viewModel = hiltViewModel(),
                onLoginSuccess = { perfil ->
                    navController.navigate(Destination.Home.createRoute(perfil.name)) {
                        popUpTo(Destination.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // =====================================================================
        // MAIN — Contains FloatingNavBar with internal navigation
        // =====================================================================
        composable(
            route = Destination.Home.route,
            arguments = listOf(
                navArgument(Destination.Home.ARG_PERFIL) {
                    type = NavType.StringType
                }
            ),
            enterTransition = { fadeIn(tween(TRANSITION_DURATION)) },
            popEnterTransition = { fadeIn(tween(TRANSITION_DURATION)) }
        ) { backStackEntry ->
            val perfilName = backStackEntry.arguments?.getString(Destination.Home.ARG_PERFIL) ?: "OPERADOR"
            val perfil = PerfilUsuario.valueOf(perfilName)

            MainScreen(
                perfil = perfil,
                onLogout = {
                    navController.navigate(Destination.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Suppress("unused")
class NavigationActions(private val navController: NavHostController) {
    fun navigateToHome(perfil: String) {
        navController.navigate(Destination.Home.createRoute(perfil)) {
            popUpTo(Destination.Login.route) { inclusive = true }
        }
    }

    fun navigateToIdeias() = navController.navigate(Destination.Ideias.route)
    fun navigateToProjetos() = navController.navigate(Destination.Projetos.route)
    fun navigateBack() = navController.popBackStack()
}
