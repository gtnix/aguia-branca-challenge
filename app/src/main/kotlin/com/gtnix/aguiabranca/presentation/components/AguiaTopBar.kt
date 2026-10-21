package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.presentation.theme.CardDimensions
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberAguiaTopBarScrollBehavior(): TopAppBarScrollBehavior {
    return TopAppBarDefaults.pinnedScrollBehavior()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AguiaTopBar(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    val showHairline = (scrollBehavior?.state?.overlappedFraction ?: 0f) > 0.01f
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            modifier = Modifier.fillMaxWidth(),
            title = {
                Column(
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .semantics { heading() }
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            },
            navigationIcon = {
                if (onBackClick != null) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back),
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            },
            actions = actions,
            scrollBehavior = scrollBehavior,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                scrolledContainerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.onBackground,
                actionIconContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
        if (showHairline) {
            HorizontalDivider(
                thickness = CardDimensions.ContentBorderWidth,
                color = MaterialTheme.colorScheme.outlineVariant.copy(
                    alpha = CardDimensions.ContentBorderLightAlpha
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Título Light")
@Preview(name = "Título Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AguiaTopBarPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AguiaTopBar(
                title = "Nova Orientação",
                onBackClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Subtítulo Light")
@Preview(name = "Subtítulo Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AguiaTopBarSubtitlePreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AguiaTopBar(
                title = "Nova Ideia",
                subtitle = "Descreva o problema e a solução proposta",
                onBackClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "Actions Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun AguiaTopBarActionsPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AguiaTopBar(
                title = "Ideias",
                onBackClick = {},
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.AutoAwesome,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    }
}
