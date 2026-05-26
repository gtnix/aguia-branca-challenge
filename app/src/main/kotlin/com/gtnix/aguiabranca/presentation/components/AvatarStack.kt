package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

data class AvatarData(
    val initials: String,
    val backgroundColor: Color? = null
)

@Composable
fun AvatarStack(
    avatars: List<AvatarData>,
    modifier: Modifier = Modifier,
    maxVisible: Int = 3,
    avatarSize: Dp = 32.dp,
    overlapOffset: Dp = (-10).dp,
    borderWidth: Dp = 2.dp
) {
    val isDarkTheme = isSystemInDarkTheme()
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surface
    } else {
        Color.White
    }
    
    val visibleAvatars = avatars.take(maxVisible)
    val remainingCount = (avatars.size - maxVisible).coerceAtLeast(0)
    
    val defaultColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.secondary,
        MaterialTheme.colorScheme.tertiary,
        MaterialTheme.colorScheme.primaryContainer,
        MaterialTheme.colorScheme.secondaryContainer
    )
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        visibleAvatars.forEachIndexed { index, avatar ->
            val offsetX = if (index > 0) overlapOffset * index else 0.dp
            val zIndex = (visibleAvatars.size - index).toFloat()
            
            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .zIndex(zIndex)
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(borderWidth, borderColor, CircleShape)
                    .background(
                        avatar.backgroundColor ?: defaultColors[index % defaultColors.size],
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = avatar.initials.take(2).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
        
        if (remainingCount > 0) {
            val offsetX = overlapOffset * visibleAvatars.size
            
            Box(
                modifier = Modifier
                    .offset(x = offsetX)
                    .zIndex(0f)
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(borderWidth, borderColor, CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+$remainingCount",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AvatarStackPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AvatarStack(
                    avatars = listOf(
                        AvatarData("AS"),
                        AvatarData("JC"),
                        AvatarData("MR"),
                        AvatarData("PL"),
                        AvatarData("TC")
                    )
                )
                
                AvatarStack(
                    avatars = listOf(
                        AvatarData("AS"),
                        AvatarData("JC")
                    ),
                    avatarSize = 40.dp
                )
                
                AvatarStack(
                    avatars = listOf(
                        AvatarData("A"),
                        AvatarData("B"),
                        AvatarData("C"),
                        AvatarData("D"),
                        AvatarData("E"),
                        AvatarData("F"),
                        AvatarData("G")
                    ),
                    maxVisible = 4
                )
            }
        }
    }
}
