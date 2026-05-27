package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

/**
 * Compact brand mark for the home header.
 *
 * Renders the vector logo (navy circle + spark) next to a single-line
 * "Águia Branca" wordmark. Designed to feel like a companion element,
 * not a hero — leaves the spotlight for the personalized greeting below.
 */
@Composable
fun InovagabBrandLogo(
    modifier: Modifier = Modifier
) {
    val cdLogo = stringResource(R.string.cd_logo)
    Row(
        modifier = modifier.semantics { contentDescription = cdLogo },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_inovagab_logo),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = stringResource(R.string.home_logo),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = 0.2.sp
        )
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InovagabBrandLogoPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InovagabBrandLogo()
        }
    }
}
