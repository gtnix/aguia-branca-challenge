package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.EncerramentoCampanhaDto
import com.gtnix.aguiabranca.data.remote.dto.OrientacaoDto
import com.gtnix.aguiabranca.data.remote.dto.OrientacaoRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface OrientacaoApiService {

    @GET("orientacoes")
    suspend fun listar(@Query("apenasAtivas") apenasAtivas: Boolean = true): List<OrientacaoDto>

    @GET("orientacoes/{id}")
    suspend fun obter(@Path("id") id: String): OrientacaoDto

    @POST("orientacoes")
    suspend fun criar(@Body request: OrientacaoRequestDto): OrientacaoDto

    @PUT("orientacoes/{id}")
    suspend fun atualizar(
        @Path("id") id: String,
        @Body request: OrientacaoRequestDto
    ): OrientacaoDto

    @PUT("orientacoes/{id}/ativo")
    suspend fun definirAtivo(
        @Path("id") id: String,
        @Query("ativo") ativo: Boolean
    ): OrientacaoDto

    @POST("orientacoes/{id}/campanha/encerrar")
    suspend fun encerrarCampanha(@Path("id") id: String): EncerramentoCampanhaDto

    @DELETE("orientacoes/{id}")
    suspend fun excluir(@Path("id") id: String): Response<Void>
}
