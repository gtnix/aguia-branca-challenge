package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.AreaDesempenhoDto
import com.gtnix.aguiabranca.data.remote.dto.DashboardByProjectDto
import com.gtnix.aguiabranca.data.remote.dto.DashboardByStrategyDto
import com.gtnix.aguiabranca.data.remote.dto.DashboardSummaryDto
import com.gtnix.aguiabranca.data.remote.dto.FunnelStepDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DashboardApiService {

    @GET("dashboard/summary")
    suspend fun summary(@Query("orientacaoId") orientacaoId: String? = null): DashboardSummaryDto

    @GET("dashboard/funnel")
    suspend fun funnel(): List<FunnelStepDto>

    @GET("dashboard/by-area")
    suspend fun byArea(): List<AreaDesempenhoDto>

    @GET("dashboard/by-strategy/{orientacaoId}")
    suspend fun byStrategy(@Path("orientacaoId") orientacaoId: String): DashboardByStrategyDto

    @GET("dashboard/by-project/{projetoId}")
    suspend fun byProject(@Path("projetoId") projetoId: String): DashboardByProjectDto
}
