package com.gtnix.aguiabranca.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import com.gtnix.aguiabranca.domain.model.PerfilUsuario

data class BottomNavItemData(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AguiaBottomNav(
    currentRoute: String,
    userProfile: PerfilUsuario,
    onNavigate: (String) -> Unit
) {
    val navItems = remember(userProfile) {
        buildList {
            add(BottomNavItemData("home", "Home", Icons.Filled.Home, Icons.Outlined.Home))
            add(BottomNavItemData("ideias", "Ideias", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb))
            if (userProfile != PerfilUsuario.OPERADOR) {
                add(BottomNavItemData("projetos", "Projetos", Icons.Filled.Folder, Icons.Outlined.Folder))
            }
            add(BottomNavItemData("perfil", "Perfil", Icons.Filled.Person, Icons.Outlined.Person))
        }
    }

    NavigationBar {
        navItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = selected,
                onClick = { onNavigate(item.route) }
            )
        }
    }
}
