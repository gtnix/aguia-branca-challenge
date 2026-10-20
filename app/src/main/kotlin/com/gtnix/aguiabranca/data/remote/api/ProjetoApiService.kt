package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.ProgressoRequestDto
import com.gtnix.aguiabranca.data.remote.dto.ProjetoDto
import com.gtnix.aguiabranca.data.remote.dto.ProjetoRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProjetoApiService {

    @GET("projetos")
    suspend fun listar(): List<ProjetoDto>

    @GET("projetos/{id}")
    suspend fun obter(@Path("id") id: String): ProjetoDto

    @POST("projetos")
    suspend fun criar(@Body request: ProjetoRequestDto): ProjetoDto

    @PUT("projetos/{id}")
    suspend fun atualizar(
        @Path("id") id: String,
        @Body request: ProjetoRequestDto
    ): ProjetoDto

    @DELETE("projetos/{id}")
    suspend fun excluir(@Path("id") id: String): Response<Void>

    @PATCH("projetos/{id}/progresso")
    suspend fun atualizarProgresso(
        @Path("id") id: String,
        @Body request: ProgressoRequestDto
    ): ProjetoDto
}
