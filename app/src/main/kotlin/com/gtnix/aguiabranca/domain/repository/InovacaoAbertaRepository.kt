package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.StartupPartner

interface InovacaoAbertaRepository {
    suspend fun buscarStartupsRecomendadas(): List<StartupPartner>
}
