package com.gtnix.aguiabranca.presentation.screens.orientacoes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.CampanhaPatrocinada
import com.gtnix.aguiabranca.presentation.components.GlassCard
import com.gtnix.aguiabranca.presentation.components.InovagabTextField
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/** Campos do formulário em texto para o Líder digitar livremente; a validação acontece em [toDomain]. */
data class CampanhaFormState(
    val ativa: Boolean = false,
    val dias: String = "30",
    val bonusAsas: String = "50",
    val topN: String = "3",
    val encerrada: Boolean = false
) {
    /** Nulo quando desligada; lança [IllegalArgumentException] se os números estiverem fora da faixa. */
    fun toDomain(hoje: LocalDate = LocalDate.now()): CampanhaPatrocinada? {
        if (!ativa) return null
        val duracao = dias.toIntOrNull()
        val bonus = bonusAsas.toIntOrNull()
        val premiados = topN.toIntOrNull()
        require(duracao != null && duracao in DIAS_VALIDOS) { "dias" }
        require(bonus != null && bonus in BONUS_VALIDO) { "bonus" }
        require(premiados != null && premiados in TOP_N_VALIDO) { "topN" }
        return CampanhaPatrocinada(fim = hoje.plusDays(duracao.toLong()), bonusAsas = bonus, topN = premiados)
    }

    companion object {
        val DIAS_VALIDOS = 1..120
        val BONUS_VALIDO = 0..500
        val TOP_N_VALIDO = 1..10

        fun de(campanha: CampanhaPatrocinada?, hoje: LocalDate = LocalDate.now()): CampanhaFormState {
            if (campanha == null) return CampanhaFormState()
            val restantes = ChronoUnit.DAYS.between(hoje, campanha.fim).coerceAtLeast(1)
            return CampanhaFormState(
                ativa = !campanha.encerrada,
                dias = restantes.toString(),
                bonusAsas = campanha.bonusAsas.toString(),
                topN = campanha.topN.toString(),
                encerrada = campanha.encerrada
            )
        }
    }
}

@Composable
fun CampanhaPatrocinadaFields(
    state: CampanhaFormState,
    onChange: (CampanhaFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    val numero = KeyboardOptions(keyboardType = KeyboardType.Number)
    GlassCard(modifier = modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = stringResource(R.string.campanha_titulo),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.campanha_descricao),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (state.encerrada) {
                Text(
                    text = stringResource(R.string.campanha_encerrada),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
                return@Column
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.campanha_ativar),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = state.ativa, onCheckedChange = { onChange(state.copy(ativa = it)) })
            }
            if (state.ativa) {
                InovagabTextField(
                    value = state.dias,
                    onValueChange = { onChange(state.copy(dias = it.filter(Char::isDigit))) },
                    label = stringResource(R.string.campanha_fim_dias),
                    keyboardOptions = numero
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InovagabTextField(
                        value = state.bonusAsas,
                        onValueChange = { onChange(state.copy(bonusAsas = it.filter(Char::isDigit))) },
                        label = stringResource(R.string.campanha_bonus),
                        keyboardOptions = numero,
                        modifier = Modifier.weight(1f)
                    )
                    InovagabTextField(
                        value = state.topN,
                        onValueChange = { onChange(state.copy(topN = it.filter(Char::isDigit))) },
                        label = stringResource(R.string.campanha_top_n),
                        keyboardOptions = numero,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
