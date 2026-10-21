package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.AsaAzul
import com.gtnix.aguiabranca.presentation.theme.FundoPagina
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LightBackground
import com.gtnix.aguiabranca.presentation.theme.LimaMarca
import com.gtnix.aguiabranca.presentation.theme.MarinhoInstitucional

/**
 * Assinatura de marca do InovaGAB.
 *
 * A marca principal é o logo oficial do Grupo Águia Branca; o nome do produto
 * aparece como wordmark, no mesmo desenho da navegação do site institucional.
 * O símbolo antigo do produto (asa/rota) não é mais usado como logo.
 */
enum class InovagabBrandLogoVariant {
    OnLightSurface,
    OnDarkSurface,
    Auto
}

enum class InovagabBrandLogoSize {
    Header,
    Hero,
    Stage
}

@Composable
fun InovagabBrandLogo(
    modifier: Modifier = Modifier,
    variant: InovagabBrandLogoVariant = InovagabBrandLogoVariant.Auto,
    size: InovagabBrandLogoSize = InovagabBrandLogoSize.Header,
    markAsHeading: Boolean = false
) {
    val resolvedVariant = when (variant) {
        InovagabBrandLogoVariant.Auto -> {
            if (MaterialTheme.colorScheme.background.luminance() < 0.35f) {
                InovagabBrandLogoVariant.OnDarkSurface
            } else {
                InovagabBrandLogoVariant.OnLightSurface
            }
        }
        else -> variant
    }

    val cdLogo = stringResource(R.string.cd_logo)
    val semanticsModifier = modifier
        .semantics { contentDescription = cdLogo }
        .then(if (markAsHeading) Modifier.semantics { heading() } else Modifier)

    when (resolvedVariant) {
        InovagabBrandLogoVariant.OnLightSurface -> {
            if (size == InovagabBrandLogoSize.Header) {
                Box(modifier = semanticsModifier) {
                    Image(
                        painter = painterResource(id = R.drawable.grupo_aguia_branca),
                        contentDescription = null,
                        modifier = Modifier
                            .height(26.dp)
                            .widthIn(max = 120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                Column(
                    modifier = semanticsModifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.grupo_aguia_branca),
                        contentDescription = null,
                        modifier = Modifier
                            .height(if (size == InovagabBrandLogoSize.Stage) 52.dp else 44.dp)
                            .widthIn(max = 260.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = brandWordmark(
                            inovaColor = MarinhoInstitucional,
                            gabColor = AsaAzul
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = if (size == InovagabBrandLogoSize.Stage) 28.sp else 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.brand_plataforma_grupo),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        InovagabBrandLogoVariant.OnDarkSurface -> {
            if (size == InovagabBrandLogoSize.Header) {
                Box(modifier = semanticsModifier) {
                    Image(
                        painter = painterResource(id = R.drawable.grupo_aguia_branca_branco),
                        contentDescription = null,
                        modifier = Modifier
                            .height(26.dp)
                            .widthIn(max = 120.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            } else {
                Column(
                    modifier = semanticsModifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.grupo_aguia_branca_branco),
                        contentDescription = null,
                        modifier = Modifier
                            .height(if (size == InovagabBrandLogoSize.Stage) 52.dp else 44.dp)
                            .widthIn(max = 260.dp),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = brandWordmark(
                            inovaColor = FundoPagina,
                            gabColor = LimaMarca
                        ),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = if (size == InovagabBrandLogoSize.Stage) 28.sp else 24.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.brand_plataforma_grupo),
                        style = MaterialTheme.typography.labelSmall,
                        color = FundoPagina.copy(alpha = 0.62f)
                    )
                }
            }
        }
        InovagabBrandLogoVariant.Auto -> error("Auto must be resolved before rendering")
    }
}

private fun brandWordmark(
    inovaColor: Color,
    gabColor: Color
) = buildAnnotatedString {
    withStyle(SpanStyle(color = inovaColor, fontWeight = FontWeight.SemiBold)) {
        append("Inova")
    }
    withStyle(SpanStyle(color = gabColor, fontWeight = FontWeight.Bold)) {
        append("GAB")
    }
}

@Preview(name = "Header on light")
@Composable
private fun InovagabBrandLogoHeaderLightPreview() {
    InovagabTheme {
        Surface(color = LightBackground) {
            InovagabBrandLogo(
                variant = InovagabBrandLogoVariant.OnLightSurface,
                size = InovagabBrandLogoSize.Header
            )
        }
    }
}

@Preview(name = "Login stage on light")
@Composable
private fun InovagabBrandLogoStageLightPreview() {
    InovagabTheme(darkTheme = false) {
        Surface(color = LightBackground) {
            InovagabBrandLogo(
                variant = InovagabBrandLogoVariant.OnLightSurface,
                size = InovagabBrandLogoSize.Stage,
                markAsHeading = true
            )
        }
    }
}

@Preview(name = "Header Auto dark theme", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabBrandLogoHeaderAutoDarkPreview() {
    InovagabTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            InovagabBrandLogo(variant = InovagabBrandLogoVariant.Auto)
        }
    }
}
