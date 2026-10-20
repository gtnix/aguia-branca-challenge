package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.AvaliacaoRequestDto
import com.gtnix.aguiabranca.data.remote.dto.EstimativaRequestDto
import com.gtnix.aguiabranca.data.remote.dto.FeedbackUtilRequestDto
import com.gtnix.aguiabranca.data.remote.dto.IdeiaDto
import com.gtnix.aguiabranca.data.remote.dto.IdeiaRequestDto
import com.gtnix.aguiabranca.data.remote.dto.PrioridadeRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface IdeiaApiService {

    @GET("ideias")
    suspend fun listar(
        @Query("status") status: String? = null,
        @Query("area") area: String? = null,
        @Query("orientacaoId") orientacaoId: String? = null
    ): List<IdeiaDto>

    @GET("ideias/{id}")
    suspend fun obter(@Path("id") id: String): IdeiaDto

    @POST("ideias")
    suspend fun criar(@Body request: IdeiaRequestDto): IdeiaDto

    @PUT("ideias/{id}")
    suspend fun atualizar(
        @Path("id") id: String,
        @Body request: IdeiaRequestDto
    ): IdeiaDto

    @DELETE("ideias/{id}")
    suspend fun excluir(@Path("id") id: String): Response<Void>

    @PATCH("ideias/{id}/avaliacao")
    suspend fun avaliar(
        @Path("id") id: String,
        @Body request: AvaliacaoRequestDto
    ): IdeiaDto

    @POST("ideias/{id}/apoio")
    suspend fun apoiar(@Path("id") id: String): IdeiaDto

    @POST("ideias/{id}/feedback-util")
    suspend fun marcarFeedbackUtil(
        @Path("id") id: String,
        @Body request: FeedbackUtilRequestDto
    ): IdeiaDto

    @PATCH("ideias/{id}/prioridade")
    suspend fun priorizar(
        @Path("id") id: String,
        @Body request: PrioridadeRequestDto
    ): IdeiaDto

    @PATCH("ideias/{id}/estimativa")
    suspend fun salvarEstimativa(
        @Path("id") id: String,
        @Body request: EstimativaRequestDto
    ): IdeiaDto
}
