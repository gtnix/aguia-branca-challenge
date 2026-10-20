package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
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

enum class InovagabBrandLogoVariant {
    OnLightSurface,
    /** Symbol + Compose wordmark for dark headers (no plate). */
    OnDarkSurface,
    /** Lockup on light surfaces; symbol + wordmark on dark. */
    Auto
}

enum class InovagabBrandLogoSize {
    Header,
    Hero,
    /** Login stage: large symbol stacked above wordmark on navy. */
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
    val semanticsModifier = modifier.semantics { contentDescription = cdLogo }
        .then(
            if (markAsHeading) Modifier.semantics { heading() } else Modifier
        )

    when (resolvedVariant) {
        InovagabBrandLogoVariant.OnLightSurface -> {
            val logoHeight = when (size) {
                InovagabBrandLogoSize.Header -> 32.dp
                InovagabBrandLogoSize.Hero,
                InovagabBrandLogoSize.Stage -> 72.dp
            }
            val logoMaxWidth = when (size) {
                InovagabBrandLogoSize.Header -> 168.dp
                InovagabBrandLogoSize.Hero,
                InovagabBrandLogoSize.Stage -> 320.dp
            }
            Box(modifier = semanticsModifier) {
                Image(
                    painter = painterResource(id = R.drawable.ic_inovagab_lockup),
                    contentDescription = null,
                    modifier = Modifier
                        .height(logoHeight)
                        .widthIn(max = logoMaxWidth),
                    contentScale = ContentScale.Fit
                )
            }
        }
        InovagabBrandLogoVariant.OnDarkSurface -> {
            val wordmark = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = FundoPagina,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Inova")
                }
                withStyle(
                    SpanStyle(
                        color = AsaAzul,
                        fontWeight = FontWeight.Bold
                    )
                ) {
                    append("GAB")
                }
            }
            when (size) {
                InovagabBrandLogoSize.Stage -> {
                    Column(
                        modifier = semanticsModifier,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_inovagab_logo),
                            contentDescription = null,
                            modifier = Modifier.height(112.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = wordmark,
                            style = MaterialTheme.typography.headlineSmall.copy(fontSize = 28.sp)
                        )
                    }
                }
                InovagabBrandLogoSize.Header,
                InovagabBrandLogoSize.Hero -> {
                    val symbolHeight = when (size) {
                        InovagabBrandLogoSize.Header -> 28.dp
                        InovagabBrandLogoSize.Hero -> 48.dp
                        InovagabBrandLogoSize.Stage -> 48.dp
                    }
                    val wordmarkStyle = when (size) {
                        InovagabBrandLogoSize.Header -> MaterialTheme.typography.titleMedium
                        InovagabBrandLogoSize.Hero -> MaterialTheme.typography.headlineSmall
                        InovagabBrandLogoSize.Stage -> MaterialTheme.typography.headlineSmall
                    }
                    Row(
                        modifier = semanticsModifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_inovagab_logo),
                            contentDescription = null,
                            modifier = Modifier.height(symbolHeight),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(
                            modifier = Modifier.width(
                                if (size == InovagabBrandLogoSize.Hero) 10.dp else 6.dp
                            )
                        )
                        Text(
                            text = wordmark,
                            style = wordmarkStyle.copy(
                                fontSize = when (size) {
                                    InovagabBrandLogoSize.Header -> wordmarkStyle.fontSize
                                    InovagabBrandLogoSize.Hero -> 26.sp
                                    InovagabBrandLogoSize.Stage -> 26.sp
                                }
                            )
                        )
                    }
                }
            }
        }
        InovagabBrandLogoVariant.Auto -> error("Auto must be resolved before rendering")
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

@Preview(name = "Hero on light (login)")
@Composable
private fun InovagabBrandLogoHeroLightPreview() {
    InovagabTheme(darkTheme = false) {
        Surface(color = LightBackground) {
            InovagabBrandLogo(
                variant = InovagabBrandLogoVariant.OnLightSurface,
                size = InovagabBrandLogoSize.Hero,
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
