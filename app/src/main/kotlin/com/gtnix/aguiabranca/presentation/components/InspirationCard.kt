package com.gtnix.aguiabranca.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import java.util.Calendar

private data class InspirationQuote(val text: String, val author: String)

private val operadorQuotes = listOf(
    InspirationQuote(
        "A inovação distingue um líder de um seguidor.",
        "Steve Jobs"
    ),
    InspirationQuote(
        "Ideias são fáceis. Implementação é difícil.",
        "Guy Kawasaki"
    ),
    InspirationQuote(
        "Não tenha medo de desistir do bom para perseguir o ótimo.",
        "John D. Rockefeller"
    ),
    InspirationQuote(
        "Cada ideia sua pode transformar o futuro da empresa.",
        "Águia Branca"
    ),
    InspirationQuote(
        "Inovar é transformar o impossível em possível.",
        "Inovagab"
    )
)

private val gestorQuotes = listOf(
    InspirationQuote(
        "A qualidade é responsabilidade de todos, mas começa com quem lidera.",
        "W. Edwards Deming"
    ),
    InspirationQuote(
        "Um mentor empodera a pessoa a ver uma esperança dentro dela mesma.",
        "Nelson Mandela"
    ),
    InspirationQuote(
        "Seu papel é essencial para transformar ideias em realidade.",
        "Águia Branca"
    ),
    InspirationQuote(
        "Liderança é influência, nada mais, nada menos.",
        "John C. Maxwell"
    ),
    InspirationQuote(
        "Grandes mentores criam grandes inovadores.",
        "Inovagab"
    )
)

private val liderQuotes = listOf(
    InspirationQuote(
        "A visão sem ação é apenas um sonho. A ação sem visão é apenas passatempo.",
        "Joel A. Barker"
    ),
    InspirationQuote(
        "O futuro pertence àqueles que veem possibilidades antes que se tornem óbvias.",
        "John Sculley"
    ),
    InspirationQuote(
        "A inovação acontece quando todos participam.",
        "Águia Branca"
    ),
    InspirationQuote(
        "Estratégia é escolher o que não fazer.",
        "Michael Porter"
    ),
    InspirationQuote(
        "Transformar a empresa começa com transformar a mentalidade.",
        "Inovagab"
    )
)

private fun quotesForPerfil(perfil: PerfilUsuario): List<InspirationQuote> = when (perfil) {
    PerfilUsuario.OPERADOR -> operadorQuotes
    PerfilUsuario.GESTOR -> gestorQuotes
    PerfilUsuario.LIDER -> liderQuotes
}

private fun inspirationQuoteForPerfil(perfil: PerfilUsuario): Pair<String, String> {
    val quotes = quotesForPerfil(perfil)
    val dayOfYear = Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
    val quote = quotes[dayOfYear % quotes.size]
    return quote.text to quote.author
}

@Composable
fun InspirationCard(
    perfil: PerfilUsuario,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val shape = RoundedCornerShape(20.dp)
    val (quoteText, quoteAuthor) = remember(perfil) { inspirationQuoteForPerfil(perfil) }

    val gradientColors = if (isDarkTheme) {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f),
            MaterialTheme.colorScheme.surface
        )
    } else {
        listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.07f),
            MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f),
            MaterialTheme.colorScheme.surface
        )
    }

    val iconGradient = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (!isDarkTheme) {
                    Modifier.shadow(
                        elevation = 2.dp,
                        shape = shape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                    )
                } else {
                    Modifier
                }
            )
            .clip(shape)
            .background(Brush.verticalGradient(gradientColors))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconGradient),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "\"$quoteText\"",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                fontStyle = FontStyle.Italic
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "— $quoteAuthor",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(name = "Light Mode")
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InspirationCardPreview() {
    InovagabTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            InspirationCard(
                perfil = PerfilUsuario.OPERADOR,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
