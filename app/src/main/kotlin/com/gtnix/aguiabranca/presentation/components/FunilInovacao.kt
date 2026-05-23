package com.gtnix.aguiabranca.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FunilInovacao(
    totalIdeias: Int,
    ideiasAprovadas: Int,
    projetosAtivos: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "Funil de Inovação: $totalIdeias ideias, $ideiasAprovadas aprovadas, $projetosAtivos em projeto"
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Funil de Inovação",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            FunilLevel(
                label = "Ideias",
                value = totalIdeias,
                widthFraction = 0.9f,
                color = MaterialTheme.colorScheme.primary
            )
            FunilLevel(
                label = "Aprovadas",
                value = ideiasAprovadas,
                widthFraction = 0.7f,
                color = MaterialTheme.colorScheme.tertiary
            )
            FunilLevel(
                label = "Em Projeto",
                value = projetosAtivos,
                widthFraction = 0.5f,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
private fun FunilLevel(
    label: String,
    value: Int,
    widthFraction: Float,
    color: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(40.dp)
            .background(color, MaterialTheme.shapes.extraSmall),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$label: $value",
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
