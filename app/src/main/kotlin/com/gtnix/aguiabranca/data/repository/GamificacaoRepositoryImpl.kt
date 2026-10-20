package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.GamificacaoApiService
import com.gtnix.aguiabranca.data.remote.api.RecompensaApiService
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.domain.model.DivisaoFilter
import com.gtnix.aguiabranca.domain.model.EquipeRanking
import com.gtnix.aguiabranca.domain.model.Extrato
import com.gtnix.aguiabranca.domain.model.IndicacoesInovaGab
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.model.Resgate
import com.gtnix.aguiabranca.domain.model.ResultadoRanking
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificacaoRepositoryImpl @Inject constructor(
    private val api: GamificacaoApiService,
    private val recompensaApi: RecompensaApiService,
    private val networkMonitor: NetworkMonitor
) : GamificacaoRepository {

    override suspend fun painel(perfil: PerfilUsuario): Result<Pontuacao> =
        runCatchingApi { api.me().toDomain(perfil) }

    override suspend fun extrato(): Result<Extrato> = runCatchingApi { api.extrato().toDomain() }

    override suspend fun marcarNovidadesVistas(): Result<Unit> = runCatchingApi {
        val response = api.marcarNovidadesVistas()
        if (!response.isSuccessful) throw HttpException(response)
    }

    override suspend fun ranking(perfil: PerfilUsuario, escopo: DivisaoFilter): Result<ResultadoRanking> =
        runCatchingApi { api.ranking(perfil = perfil.name, escopo = escopo.toApi()).toDomain(perfil) }

    override suspend fun rankingEquipes(escopo: DivisaoFilter): Result<List<EquipeRanking>> =
        runCatchingApi { api.rankingEquipes(escopo.toApi()).map { it.toDomain() } }

    override suspend fun indicacoesInovagab(temporada: String?): Result<IndicacoesInovaGab> =
        runCatchingApi { api.indicacoesInovagab(temporada).toDomain() }

    override suspend fun recompensas(incluirInativas: Boolean): Result<List<Recompensa>> =
        runCatchingApi { recompensaApi.listar(incluirInativas).map { it.toDomain() } }

    override suspend fun resgatar(recompensaId: String): Result<Resgate> =
        runCatchingApi { recompensaApi.resgatar(recompensaId).toDomain() }

    override suspend fun meusResgates(): Result<List<Resgate>> =
        runCatchingApi { recompensaApi.meusResgates().map { it.toDomain() } }

    private fun DivisaoFilter.toApi(): String = when (this) {
        DivisaoFilter.MINHA_DIVISAO -> "DIVISAO"
        DivisaoFilter.GRUPO_COMPLETO -> "GRUPO"
    }

    private inline fun <T> runCatchingApi(block: () -> T): Result<T> {
        if (!networkMonitor.isOnline()) {
            return Result.Error(IOException(OFFLINE_MESSAGE))
        }
        return try {
            Result.Success(block())
        } catch (e: HttpException) {
            mutationFailure(e, FORBIDDEN_MESSAGE)
        } catch (e: IOException) {
            Result.Error(Exception(OFFLINE_MESSAGE))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private companion object {
        const val OFFLINE_MESSAGE = "Sem conexão. Esta ação exige rede na Sprint 2."
        const val FORBIDDEN_MESSAGE = "Você não tem permissão para esta ação."
    }
}
