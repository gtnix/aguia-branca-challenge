package com.gtnix.aguiabranca.presentation.components

import com.gtnix.aguiabranca.presentation.theme.LocalInovagabColors
import android.content.res.Configuration
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.NavBarDimensions

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
    val gab = LocalInovagabColors.current
    val pillShape = RoundedCornerShape(32.dp)
    val shadowColor = Color.Black.copy(alpha = 0.2f)

    Row(
        modifier = modifier
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = NavBarDimensions.PillPaddingBottom)
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(NavBarDimensions.DockGap),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .wrapContentWidth()
                .shadow(
                    elevation = 20.dp,
                    shape = pillShape,
                    ambientColor = shadowColor,
                    spotColor = shadowColor
                )
                .clip(pillShape)
                .background(gab.navFill)
                .border(1.dp, gab.navBorder, pillShape)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.Center,
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

        if (centerAction != null) {
            NavBarActionButton(
                onClick = centerAction,
                contentDescription = centerActionLabel ?: stringResource(R.string.nav_nova_ideia)
            )
        }
    }
}

@Composable
private fun NavBarActionButton(
    onClick: () -> Unit,
    contentDescription: String
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val shadowColor = Color.Black.copy(alpha = 0.2f)

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMedium),
        label = "fabScale"
    )

    Surface(
        modifier = Modifier
            .size(NavBarDimensions.FabSize)
            .scale(scale)
            .shadow(
                elevation = 20.dp,
                shape = CircleShape,
                ambientColor = shadowColor,
                spotColor = shadowColor
            ),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primary,
        onClick = {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        interactionSource = interactionSource
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = contentDescription,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
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
    val itemPillShape = RoundedCornerShape(24.dp)

    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        label = "navItemScale"
    )

    val selectedIconTint = MaterialTheme.colorScheme.primary
    val unselectedIconTint = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .scale(scale)
            .width(60.dp)
            .height(52.dp)
            .clip(itemPillShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isSelected) item.selectedIcon else item.icon,
            contentDescription = item.label,
            modifier = Modifier.size(22.dp),
            tint = if (isSelected) selectedIconTint else unselectedIconTint
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = item.label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = if (isSelected) selectedIconTint else unselectedIconTint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(4.dp)
                    .background(selectedIconTint, CircleShape)
            )
        }
    }
}

@Preview(name = "Operador 3 abas Light")
@Preview(name = "Operador 3 abas Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FloatingNavBarPreview3Items() {
    InovagabTheme {
        val items = listOf(
            FloatingNavItem("home", "Home", Icons.Outlined.Home, Icons.Filled.Home),
            FloatingNavItem("ideias", "Ideias", Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb),
            FloatingNavItem("perfil", "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
        )
        FloatingNavBarPreviewHost(items)
    }
}

@Preview(name = "Gestor 4 abas Light")
@Preview(name = "Gestor 4 abas Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun FloatingNavBarPreview4Items() {
    InovagabTheme {
        val items = listOf(
            FloatingNavItem("home", "Home", Icons.Outlined.Home, Icons.Filled.Home),
            FloatingNavItem("ideias", "Ideias", Icons.Outlined.Lightbulb, Icons.Filled.Lightbulb),
            FloatingNavItem("projetos", "Projetos", Icons.Outlined.Folder, Icons.Filled.Folder),
            FloatingNavItem("perfil", "Perfil", Icons.Outlined.Person, Icons.Filled.Person)
        )
        FloatingNavBarPreviewHost(items)
    }
}

@Composable
private fun FloatingNavBarPreviewHost(items: List<FloatingNavItem>) {
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
