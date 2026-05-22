package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.api.InovacaoApiService
import com.gtnix.aguiabranca.domain.model.StartupPartner
import com.gtnix.aguiabranca.domain.repository.InovacaoAbertaRepository
import javax.inject.Inject

class InovacaoAbertaRepositoryImpl @Inject constructor(
    private val api: InovacaoApiService
) : InovacaoAbertaRepository {

    override suspend fun buscarStartupsRecomendadas(): List<StartupPartner> {
        return api.getStartupsRecomendadas()
    }
}
