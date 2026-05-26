package com.gtnix.aguiabranca.presentation.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.components.FloatingNavBar
import com.gtnix.aguiabranca.presentation.components.FloatingNavItem
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.presentation.screens.home.HomeContent
import com.gtnix.aguiabranca.presentation.screens.ideias.IdeiaDetalheScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.IdeiasScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.NovaIdeiaScreen
import com.gtnix.aguiabranca.presentation.screens.inovacao.RadarScreen
import com.gtnix.aguiabranca.presentation.screens.orientacoes.NovaOrientacaoScreen
import com.gtnix.aguiabranca.presentation.screens.orientacoes.OrientacoesScreen
import com.gtnix.aguiabranca.presentation.screens.perfil.PerfilScreen
import com.gtnix.aguiabranca.presentation.screens.projetos.NovoProjetoScreen
import com.gtnix.aguiabranca.presentation.screens.projetos.ProjetoDetalheScreen
import com.gtnix.aguiabranca.presentation.screens.projetos.ProjetosScreen
import com.gtnix.aguiabranca.presentation.screens.leader.LeaderDashboardScreen

@Composable
fun MainScreen(
    perfil: PerfilUsuario,
    onLogout: () -> Unit,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"
    
    val mainRoutes = setOf("home", "ideias", "projetos", "perfil", "leader_dashboard")
    val showBottomBar = mainRoutes.any { currentRoute.startsWith(it) && !currentRoute.contains("/") }
    
    val homeLabel = stringResource(R.string.nav_home)
    val ideiasLabel = stringResource(R.string.nav_ideias)
    val projetosLabel = stringResource(R.string.nav_projetos)
    val perfilLabel = stringResource(R.string.nav_perfil)
    
    val navItems = buildList {
        add(FloatingNavItem("home", homeLabel, Icons.Outlined.Home, Icons.Filled.Home))
        add(FloatingNavItem("ideias", ideiasLabel, Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb))
        if (perfil != PerfilUsuario.OPERADOR) {
            add(FloatingNavItem("projetos", projetosLabel, Icons.Outlined.Folder, Icons.Filled.Folder))
        }
        add(FloatingNavItem("perfil", perfilLabel, Icons.Outlined.Person, Icons.Filled.Person))
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                FloatingNavBar(
                    items = navItems,
                    selectedRoute = when {
                        currentRoute.startsWith("home") -> "home"
                        currentRoute.startsWith("leader_dashboard") -> "home"
                        currentRoute.startsWith("ideias") -> "ideias"
                        currentRoute.startsWith("projetos") -> "projetos"
                        currentRoute.startsWith("perfil") -> "perfil"
                        else -> "home"
                    },
                    onItemSelected = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    centerAction = if (perfil != PerfilUsuario.LIDER) {
                        { navController.navigate(Destination.NovaIdeia.route) }
                    } else null
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") {
                HomeContent(
                    viewModel = hiltViewModel(),
                    perfil = perfil.name,
                    onNavigateToOrientacoes = {
                        navController.navigate(Destination.Orientacoes.route)
                    },
                    onNavigateToRadar = {
                        navController.navigate(Destination.Radar.route)
                    },
                    onNavigateToLeaderDashboard = {
                        navController.navigate(Destination.LeaderDashboard.route)
                    },
                    onNavigateToNovaIdeia = {
                        navController.navigate(Destination.NovaIdeia.route)
                    },
                    onNavigateToIdeias = {
                        navController.navigate("ideias") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProjetos = {
                        navController.navigate("projetos") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            composable("ideias") {
                IdeiasScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = null,
                    onNavigateToNovaIdeia = {
                        navController.navigate(Destination.NovaIdeia.route)
                    },
                    onNavigateToDetalhe = { ideiaId ->
                        navController.navigate(Destination.IdeiaDetalhe.createRoute(ideiaId))
                    }
                )
            }
            
            composable("projetos") {
                ProjetosScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = null,
                    onNavigateToNovoProjeto = {
                        navController.navigate(Destination.NovoProjeto.createRoute())
                    },
                    onNavigateToDetalhe = { projetoId ->
                        navController.navigate(Destination.ProjetoDetalhe.createRoute(projetoId))
                    }
                )
            }
            
            composable("perfil") {
                PerfilScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = null,
                    onLogout = onLogout
                )
            }
            
            composable(route = Destination.NovaIdeia.route) {
                NovaIdeiaScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onIdeiaCreated = { navController.popBackStack() }
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
                    onProjetoCreated = { navController.popBackStack() }
                )
            }
            
            composable(route = Destination.Orientacoes.route) {
                OrientacoesScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToNova = {
                        navController.navigate(Destination.NovaOrientacao.route)
                    }
                )
            }
            
            composable(route = Destination.NovaOrientacao.route) {
                NovaOrientacaoScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onOrientacaoCreated = { navController.popBackStack() }
                )
            }
            
            composable(route = Destination.Radar.route) {
                RadarScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(route = Destination.LeaderDashboard.route) {
                LeaderDashboardScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onNavigateToIdeias = {
                        navController.navigate("ideias") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProjetos = {
                        navController.navigate("projetos") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToPerfil = {
                        navController.navigate("perfil") {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}
