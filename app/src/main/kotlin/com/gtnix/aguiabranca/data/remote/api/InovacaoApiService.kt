package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.domain.model.StartupPartner
import retrofit2.http.GET

interface InovacaoApiService {

    @GET("startups/recomendadas")
    suspend fun getStartupsRecomendadas(): List<StartupPartner>
}
