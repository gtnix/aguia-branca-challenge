package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.LimaMarca
import com.gtnix.aguiabranca.presentation.theme.MarinhoInstitucional
import com.gtnix.aguiabranca.presentation.theme.MarinhoSuperficie

/**
 * Hero de marca do InovaGAB.
 *
 * A mesma linguagem do site institucional: gradiente do azul da asa para o marinho,
 * brilho discreto, tipografia Poppins e CTA branco. Não usa vidro/frost: é superfície sólida.
 */
@Composable
fun InspirationHeroCard(
    headline: String,
    subheadline: String,
    modifier: Modifier = Modifier,
    ctaText: String? = null,
    onCtaClick: (() -> Unit)? = null,
    kicker: String? = null
) {
    val shape = RoundedCornerShape(CardDimensions.CornerRadiusLarge)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(AsaAzul, MarinhoSuperficie)
                )
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 46.dp, y = (-58).dp)
                .size(210.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.16f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 36.dp, y = 48.dp)
                .size(150.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            LimaMarca.copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            if (kicker != null) {
                Text(
                    text = kicker,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = LimaMarca,
                    letterSpacing = 1.1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            Text(
                text = headline,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                lineHeight = 25.sp,
                letterSpacing = (-0.3).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subheadline,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.82f)
            )

            if (ctaText != null && onCtaClick != null) {
                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onCtaClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = MarinhoInstitucional
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ctaText,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
