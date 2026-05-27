package com.gtnix.aguiabranca.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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
import com.gtnix.aguiabranca.presentation.navigation.MainNavPolicy
import com.gtnix.aguiabranca.presentation.screens.home.HomeContent
import com.gtnix.aguiabranca.presentation.screens.ideias.IdeiaDetalheScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.IdeiasScreen
import com.gtnix.aguiabranca.presentation.screens.ideias.NovaIdeiaScreen
import com.gtnix.aguiabranca.presentation.screens.inovacao.RadarScreen
import com.gtnix.aguiabranca.presentation.screens.orientacoes.EditarOrientacaoScreen
import com.gtnix.aguiabranca.presentation.screens.orientacoes.NovaOrientacaoScreen
import com.gtnix.aguiabranca.presentation.screens.orientacoes.OrientacaoDetalheScreen
import com.gtnix.aguiabranca.presentation.screens.orientacoes.OrientacoesScreen
import com.gtnix.aguiabranca.presentation.screens.notificacoes.NotificacoesScreen
import com.gtnix.aguiabranca.presentation.screens.perfil.PerfilScreen
import com.gtnix.aguiabranca.presentation.screens.ranking.RankingScreen
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
    val currentRoute = navBackStackEntry?.destination?.route ?: Destination.TabHome.route
    
    val showBottomBar = MainNavPolicy.showBottomBar(currentRoute)
    val fabAction = MainNavPolicy.fabActionFor(currentRoute, perfil)
    
    val homeLabel = stringResource(R.string.nav_home)
    val ideiasLabel = stringResource(R.string.nav_ideias)
    val projetosLabel = stringResource(R.string.nav_projetos)
    val perfilLabel = stringResource(R.string.nav_perfil)
    
    val navItems = buildList {
        add(FloatingNavItem(Destination.TabHome.route, homeLabel, Icons.Outlined.Home, Icons.Filled.Home))
        add(FloatingNavItem(Destination.Ideias.route, ideiasLabel, Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb))
        if (perfil != PerfilUsuario.OPERADOR) {
            add(FloatingNavItem(Destination.Projetos.route, projetosLabel, Icons.Outlined.Folder, Icons.Filled.Folder))
        }
        add(FloatingNavItem(Destination.Perfil.route, perfilLabel, Icons.Outlined.Person, Icons.Filled.Person))
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val contentPadding = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection),
            top = paddingValues.calculateTopPadding(),
            end = paddingValues.calculateEndPadding(layoutDirection),
            bottom = 0.dp
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            NavHost(
                navController = navController,
                startDestination = Destination.TabHome.route,
                modifier = Modifier.fillMaxSize()
            ) {
            composable(Destination.TabHome.route) {
                HomeContent(
                    viewModel = hiltViewModel(),
                    perfil = perfil.name,
                    onNavigateToOrientacoes = {
                        navController.navigate(Destination.Orientacoes.route)
                    },
                    onNavigateToOrientacaoDetalhe = { orientacaoId ->
                        navController.navigate(Destination.OrientacaoDetalhe.createRoute(orientacaoId))
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
                        navController.navigate(Destination.Ideias.route) {
                            popUpTo(Destination.TabHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProjetos = {
                        navController.navigate(Destination.Projetos.route) {
                            popUpTo(Destination.TabHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToIdeiaDetalhe = { ideiaId ->
                        navController.navigate(Destination.IdeiaDetalhe.createRoute(ideiaId))
                    },
                    onNavigateToRanking = {
                        navController.navigate(Destination.Ranking.route)
                    },
                    onNavigateToNovaOrientacao = {
                        navController.navigate(Destination.NovaOrientacao.route)
                    },
                    onNavigateToNotificacoes = {
                        navController.navigate(Destination.Notificacoes.route)
                    },
                    onNavigateToPerfilTab = {
                        navController.navigate(Destination.Perfil.route) {
                            popUpTo(Destination.TabHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            
            composable(Destination.Ideias.route) {
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
            
            composable(Destination.Projetos.route) {
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
            
            composable(Destination.Perfil.route) {
                PerfilScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = null,
                    onNavigateToRanking = {
                        navController.navigate(Destination.Ranking.route)
                    },
                    onLogout = onLogout
                )
            }

            composable(route = Destination.Ranking.route) {
                RankingScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
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
                    },
                    onNavigateToEditar = { orientacaoId ->
                        navController.navigate(Destination.EditarOrientacao.createRoute(orientacaoId))
                    }
                )
            }
            
            composable(
                route = Destination.OrientacaoDetalhe.route,
                arguments = listOf(
                    navArgument(Destination.OrientacaoDetalhe.ARG_ORIENTACAO_ID) {
                        type = NavType.StringType
                    }
                )
            ) {
                OrientacaoDetalheScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(route = Destination.NovaOrientacao.route) {
                NovaOrientacaoScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onOrientacaoCreated = { navController.popBackStack() }
                )
            }
            
            composable(
                route = Destination.EditarOrientacao.route,
                arguments = listOf(
                    navArgument(Destination.EditarOrientacao.ARG_ORIENTACAO_ID) {
                        type = NavType.StringType
                    }
                )
            ) {
                EditarOrientacaoScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() },
                    onOrientacaoUpdated = { navController.popBackStack() }
                )
            }
            
            composable(route = Destination.Radar.route) {
                RadarScreen(
                    viewModel = hiltViewModel(),
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(route = Destination.Notificacoes.route) {
                NotificacoesScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(route = Destination.LeaderDashboard.route) {
                LeaderDashboardScreen(
                    viewModel = hiltViewModel(),
                    onNavigateToHome = { navController.popBackStack() },
                    onNavigateToIdeias = {
                        navController.navigate(Destination.Ideias.route) {
                            popUpTo(Destination.TabHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToProjetos = {
                        navController.navigate(Destination.Projetos.route) {
                            popUpTo(Destination.TabHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToPerfil = {
                        navController.navigate(Destination.Perfil.route) {
                            popUpTo(Destination.TabHome.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            }

            if (showBottomBar) {
                FloatingNavBar(
                    items = navItems,
                    selectedRoute = when {
                        currentRoute.startsWith(Destination.TabHome.route) -> Destination.TabHome.route
                        currentRoute.startsWith(Destination.Ideias.route) -> Destination.Ideias.route
                        currentRoute.startsWith(Destination.Projetos.route) -> Destination.Projetos.route
                        currentRoute.startsWith(Destination.Perfil.route) -> Destination.Perfil.route
                        else -> Destination.TabHome.route
                    },
                    onItemSelected = { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                popUpTo(Destination.TabHome.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    centerAction = when (fabAction) {
                        MainNavPolicy.FabAction.NOVA_IDEIA -> {
                            { navController.navigate(Destination.NovaIdeia.route) }
                        }
                        MainNavPolicy.FabAction.NOVO_PROJETO -> {
                            { navController.navigate(Destination.NovoProjeto.createRoute()) }
                        }
                        MainNavPolicy.FabAction.NOVA_ORIENTACAO -> {
                            { navController.navigate(Destination.NovaOrientacao.route) }
                        }
                        MainNavPolicy.FabAction.NONE -> null
                    },
                    centerActionLabel = when (fabAction) {
                        MainNavPolicy.FabAction.NOVO_PROJETO -> stringResource(R.string.cd_add_project)
                        MainNavPolicy.FabAction.NOVA_ORIENTACAO -> stringResource(R.string.cd_publicar_orientacao)
                        else -> null
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
