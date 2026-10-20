package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.DashboardNarrativeDto
import com.gtnix.aguiabranca.data.remote.dto.MetricasIaDto
import com.gtnix.aguiabranca.data.remote.dto.RascunhoFeedbackRequestDto
import com.gtnix.aguiabranca.data.remote.dto.RascunhoFeedbackResponseDto
import com.gtnix.aguiabranca.data.remote.dto.ScoreIdeasRequestDto
import com.gtnix.aguiabranca.data.remote.dto.ScoreIdeasResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AiApiService {

    @POST("ai/score-ideas")
    suspend fun scoreIdeas(@Body request: ScoreIdeasRequestDto): ScoreIdeasResponseDto

    @POST("ai/ideias/{id}/rascunho-feedback")
    suspend fun rascunhoFeedback(
        @Path("id") ideiaId: String,
        @Body request: RascunhoFeedbackRequestDto
    ): RascunhoFeedbackResponseDto

    @GET("ai/dashboard-narrative")
    suspend fun getDashboardNarrative(
        @Query("orientacaoId") orientacaoId: String? = null
    ): DashboardNarrativeDto

    @GET("ai/metricas")
    suspend fun getMetricas(
        @Query("orientacaoId") orientacaoId: String? = null
    ): MetricasIaDto
}
