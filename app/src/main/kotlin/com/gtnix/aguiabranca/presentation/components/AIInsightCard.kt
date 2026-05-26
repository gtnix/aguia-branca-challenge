package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.presentation.theme.AISpark
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@Composable
fun AIInsightCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(16.dp)
    
    val backgroundColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val borderColor = AISpark.copy(alpha = if (isDarkTheme) 0.3f else 0.2f)
    
    Box(
        modifier = modifier
            .shadow(
                elevation = if (isDarkTheme) 8.dp else 4.dp,
                shape = shape,
                ambientColor = AISpark.copy(alpha = 0.15f),
                spotColor = AISpark.copy(alpha = 0.15f)
            )
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AISpark.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = AISpark,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}

@Composable
fun AIInsightCardSimple(
    text: String,
    modifier: Modifier = Modifier
) {
    AIInsightCard(modifier = modifier) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AIInsightCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                AIInsightCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Esta semana, a divisão de Logística registrou um aumento de 40% em ideias voltadas para redução de custos. Três novos projetos foram iniciados, com previsão de ROI consolidado de 18% a.a.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                AIInsightCardSimple(
                    text = "Detectamos uma oportunidade de economia de R\$ 50.000 no projeto atual.",
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
