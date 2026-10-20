package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.EquipeRankingDto
import com.gtnix.aguiabranca.data.remote.dto.ExtratoDto
import com.gtnix.aguiabranca.data.remote.dto.GamificacaoMeDto
import com.gtnix.aguiabranca.data.remote.dto.IndicacoesInovaGabDto
import com.gtnix.aguiabranca.data.remote.dto.RankingDto
import com.gtnix.aguiabranca.data.remote.dto.RecompensaDto
import com.gtnix.aguiabranca.data.remote.dto.ResgateDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface GamificacaoApiService {

    @GET("gamificacao/me")
    suspend fun me(): GamificacaoMeDto

    @GET("gamificacao/extrato")
    suspend fun extrato(): ExtratoDto

    @POST("gamificacao/novidades/vistas")
    suspend fun marcarNovidadesVistas(): Response<Void>

    @GET("gamificacao/ranking")
    suspend fun ranking(
        @Query("perfil") perfil: String,
        @Query("escopo") escopo: String,
        @Query("temporada") temporada: String? = null,
        @Query("pagina") pagina: Int = 0,
        @Query("tamanho") tamanho: Int = 200
    ): RankingDto

    @GET("gamificacao/ranking/equipes")
    suspend fun rankingEquipes(@Query("escopo") escopo: String): List<EquipeRankingDto>

    @GET("gamificacao/indicacoes-inovagab")
    suspend fun indicacoesInovagab(@Query("temporada") temporada: String? = null): IndicacoesInovaGabDto
}

interface RecompensaApiService {

    @GET("recompensas")
    suspend fun listar(@Query("incluirInativas") incluirInativas: Boolean = false): List<RecompensaDto>

    @POST("recompensas/{id}/resgatar")
    suspend fun resgatar(@Path("id") id: String): ResgateDto

    @GET("resgates/meus")
    suspend fun meusResgates(): List<ResgateDto>
}
