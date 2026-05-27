package com.gtnix.aguiabranca.presentation.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel()
) {
    val startState by appViewModel.startState.collectAsStateWithLifecycle()

    if (startState is AppStartState.Loading) {
        Box(modifier = modifier.fillMaxSize())
        return
    }

    val startDestination = when (val state = startState) {
        is AppStartState.Authenticated -> Destination.Home.createRoute(state.perfil.name)
        AppStartState.Unauthenticated -> Destination.Login.route
        AppStartState.Loading -> Destination.Login.route
    }

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
