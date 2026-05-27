package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.util.bounceClick

@Composable
fun LiderHeroCard(
    orientacoesAtivas: Int,
    onPublicarClick: () -> Unit,
    onVerOrientacoesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(24.dp)
    val temOrientacao = orientacoesAtivas > 0

    val gradientColors = if (isDarkTheme) {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.surface
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.primary.copy(alpha = 0.03f),
            MaterialTheme.colorScheme.surface
        )
    }

    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    } else {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(brush = Brush.verticalGradient(colors = gradientColors))
            .border(1.dp, borderColor, shape)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (temOrientacao) Icons.Default.Flag else Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            orientacoesAtivas > 1 -> stringResource(
                                R.string.home_lider_hero_headline_plural,
                                orientacoesAtivas
                            )
                            orientacoesAtivas == 1 -> stringResource(
                                R.string.home_lider_hero_headline_single
                            )
                            else -> stringResource(R.string.home_lider_hero_headline_empty)
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (temOrientacao) {
                            stringResource(R.string.home_lider_hero_subheadline)
                        } else {
                            stringResource(R.string.home_lider_hero_subheadline_empty)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // CTA primária: publicar orientação.
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .bounceClick(onClick = onPublicarClick),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primary
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.home_lider_hero_cta_publicar),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // CTA secundária: ver orientações existentes.
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .bounceClick(onClick = onVerOrientacoesClick)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                        RoundedCornerShape(14.dp)
                    ),
                shape = RoundedCornerShape(14.dp),
                color = androidx.compose.ui.graphics.Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Flag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (temOrientacao) {
                            stringResource(R.string.home_lider_hero_cta_ver, orientacoesAtivas)
                        } else {
                            stringResource(R.string.home_lider_hero_cta_ver_empty)
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Preview(name = "Light", showBackground = true)
@Preview(name = "Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LiderHeroCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LiderHeroCard(
                orientacoesAtivas = 3,
                onPublicarClick = {},
                onVerOrientacoesClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(name = "Vazio", showBackground = true)
@Composable
private fun LiderHeroCardEmptyPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LiderHeroCard(
                orientacoesAtivas = 0,
                onPublicarClick = {},
                onVerOrientacoesClick = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
