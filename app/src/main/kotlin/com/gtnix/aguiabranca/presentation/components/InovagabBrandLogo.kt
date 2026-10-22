package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import com.gtnix.aguiabranca.presentation.theme.LightBackground

/**
 * Assinatura de marca do InovaGAB.
 *
 * Header, Hero e Stage usam o logo oficial do Grupo Águia Branca
 * em escalas diferentes. O wordmark do produto não entra no login;
 * a barra da home usa Header. O símbolo antigo do produto (asa/rota)
 * não é mais usado como logo.
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

    val drawable = when (resolvedVariant) {
        InovagabBrandLogoVariant.OnLightSurface -> R.drawable.grupo_aguia_branca
        InovagabBrandLogoVariant.OnDarkSurface -> R.drawable.grupo_aguia_branca_branco
        InovagabBrandLogoVariant.Auto -> error("Auto must be resolved before rendering")
    }
    val logoHeight = when (size) {
        InovagabBrandLogoSize.Header -> 26.dp
        InovagabBrandLogoSize.Hero -> 44.dp
        InovagabBrandLogoSize.Stage -> 52.dp
    }
    val maxWidth = when (size) {
        InovagabBrandLogoSize.Header -> 120.dp
        InovagabBrandLogoSize.Hero -> 220.dp
        InovagabBrandLogoSize.Stage -> 260.dp
    }

    Box(modifier = semanticsModifier) {
        Image(
            painter = painterResource(id = drawable),
            contentDescription = null,
            modifier = Modifier
                .height(logoHeight)
                .widthIn(max = maxWidth),
            contentScale = ContentScale.Fit
        )
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

@Preview(name = "Login stage on dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabBrandLogoStageDarkPreview() {
    InovagabTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            InovagabBrandLogo(
                variant = InovagabBrandLogoVariant.Auto,
                size = InovagabBrandLogoSize.Stage,
                markAsHeading = true
            )
        }
    }
}
