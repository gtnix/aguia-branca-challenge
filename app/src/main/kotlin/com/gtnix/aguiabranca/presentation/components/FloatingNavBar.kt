package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

data class FloatingNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
)

@Composable
fun FloatingNavBar(
    items: List<FloatingNavItem>,
    selectedRoute: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    centerAction: (() -> Unit)? = null,
    centerActionLabel: String? = null
) {
    val effectiveLabel = centerActionLabel ?: stringResource(R.string.nav_nova_ideia)
    val isDarkTheme = isSystemInDarkTheme()
    val pillShape = RoundedCornerShape(32.dp)
    
    val backgroundColor = if (isDarkTheme) {
        Color(0xFF1C1C1E).copy(alpha = 0.92f)
    } else {
        Color.White.copy(alpha = 0.88f)
    }
    
    val borderColor = if (isDarkTheme) {
        Color.White.copy(alpha = 0.08f)
    } else {
        Color.Black.copy(alpha = 0.04f)
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (centerAction != null) {
            FloatingActionButton(
                onClick = centerAction,
                modifier = Modifier
                    .size(56.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                    ),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 0.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = effectiveLabel,
                    modifier = Modifier.size(28.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        Box(
            modifier = Modifier
                .wrapContentWidth()
                .widthIn(max = 340.dp)
                .shadow(
                    elevation = if (isDarkTheme) 16.dp else 24.dp,
                    shape = pillShape,
                    ambientColor = if (isDarkTheme) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.12f),
                    spotColor = if (isDarkTheme) Color.Black.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.12f)
                )
                .clip(pillShape)
                .background(backgroundColor)
                .border(1.dp, borderColor, pillShape)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    NavBarItem(
                        item = item,
                        isSelected = item.route == selectedRoute,
                        onClick = { onItemSelected(item.route) }
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    item: FloatingNavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isDarkTheme = isSystemInDarkTheme()
    val itemPillShape = RoundedCornerShape(24.dp)
    
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "navItemScale"
    )
    
    val selectedBackground = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
    }
    
    val selectedIconTint = MaterialTheme.colorScheme.primary
    val unselectedIconTint = if (isDarkTheme) {
        Color.White.copy(alpha = 0.6f)
    } else {
        Color.Black.copy(alpha = 0.5f)
    }
    
    Column(
        modifier = modifier
            .scale(scale)
            .clip(itemPillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .background(
                if (isSelected) selectedBackground else Color.Transparent
            )
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(22.dp),
            tint = if (isSelected) selectedIconTint else unselectedIconTint
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) selectedIconTint else unselectedIconTint,
            maxLines = 1
        )
    }
}

@Preview(name = "Light Mode - 4 Items")
@Preview(name = "Dark Mode - 4 Items", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FloatingNavBarPreview4Items() {
    InovagabTheme {
        val items = listOf(
            FloatingNavItem("home", "Home", Icons.Outlined.Home, Icons.Filled.Home),
            FloatingNavItem("ideias", "Ideias", Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb),
            FloatingNavItem("projetos", "Projetos", Icons.Outlined.Folder, Icons.Filled.Folder),
            FloatingNavItem("perfil", "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
        )
        
        var selectedRoute by remember { mutableStateOf("home") }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            FloatingNavBar(
                items = items,
                selectedRoute = selectedRoute,
                onItemSelected = { selectedRoute = it },
                centerAction = { },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview(name = "Light Mode - 3 Items (Operador)")
@Preview(name = "Dark Mode - 3 Items (Operador)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FloatingNavBarPreview3Items() {
    InovagabTheme {
        val items = listOf(
            FloatingNavItem("home", "Home", Icons.Outlined.Home, Icons.Filled.Home),
            FloatingNavItem("ideias", "Ideias", Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb),
            FloatingNavItem("perfil", "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
        )
        
        var selectedRoute by remember { mutableStateOf("home") }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            FloatingNavBar(
                items = items,
                selectedRoute = selectedRoute,
                onItemSelected = { selectedRoute = it },
                centerAction = { },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Preview(name = "Light Mode - No FAB (Lider)")
@Preview(name = "Dark Mode - No FAB (Lider)", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FloatingNavBarPreviewNoFab() {
    InovagabTheme {
        val items = listOf(
            FloatingNavItem("home", "Home", Icons.Outlined.Home, Icons.Filled.Home),
            FloatingNavItem("ideias", "Ideias", Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb),
            FloatingNavItem("projetos", "Projetos", Icons.Outlined.Folder, Icons.Filled.Folder),
            FloatingNavItem("perfil", "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
        )
        
        var selectedRoute by remember { mutableStateOf("home") }
        
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            FloatingNavBar(
                items = items,
                selectedRoute = selectedRoute,
                onItemSelected = { selectedRoute = it },
                centerAction = null,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
