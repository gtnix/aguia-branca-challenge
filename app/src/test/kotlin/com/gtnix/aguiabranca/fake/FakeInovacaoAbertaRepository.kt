package com.gtnix.aguiabranca.fake

import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.domain.repository.InovacaoAbertaRepository

class FakeInovacaoAbertaRepository(
    private val startups: List<StartupPartner> = emptyList(),
    private var shouldThrow: Boolean = false
) : InovacaoAbertaRepository {

    fun setShouldThrow(shouldThrow: Boolean) {
        this.shouldThrow = shouldThrow
    }

    override suspend fun buscarStartupsRecomendadas(): List<StartupPartner> {
        if (shouldThrow) throw RuntimeException("Erro ao buscar startups")
        return startups
    }
}
