package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.AiApiService
import com.gtnix.aguiabranca.data.remote.api.DashboardApiService
import com.gtnix.aguiabranca.data.remote.dto.toDomain
import com.gtnix.aguiabranca.domain.model.DashboardByProject
import com.gtnix.aguiabranca.domain.model.DashboardByStrategy
import com.gtnix.aguiabranca.domain.model.DashboardInsight
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.model.MetricasIa
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.domain.util.mutationFailure
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepositoryImpl @Inject constructor(
    private val api: DashboardApiService,
    private val aiApi: AiApiService,
    private val networkMonitor: NetworkMonitor
) : DashboardRepository {

    override suspend fun obterResumo(orientacaoId: String?): Result<LeaderDashboardSnapshot> {
        return runCatchingApi { api.summary(orientacaoId?.takeIf { it.isNotBlank() }).toDomain() }
    }

    override suspend fun obterPorEstrategia(orientacaoId: String): Result<DashboardByStrategy> {
        return runCatchingApi { api.byStrategy(orientacaoId).toDomain() }
    }

    override suspend fun obterPorProjeto(projetoId: String): Result<DashboardByProject> {
        return runCatchingApi { api.byProject(projetoId).toDomain() }
    }

    override suspend fun obterInsightIa(orientacaoId: String?): Result<DashboardInsight> {
        return runCatchingApi {
            aiApi.getDashboardNarrative(orientacaoId?.takeIf { it.isNotBlank() }).toDomain()
        }
    }

    override suspend fun obterMetricasIa(orientacaoId: String?): Result<MetricasIa> {
        return runCatchingApi {
            aiApi.getMetricas(orientacaoId?.takeIf { it.isNotBlank() }).toDomain()
        }
    }

    private inline fun <T> runCatchingApi(block: () -> T): Result<T> {
        if (!networkMonitor.isOnline()) {
            return Result.Error(IOException("Sem conexão. Esta ação exige rede na Sprint 2."))
        }
        return try {
            Result.Success(block())
        } catch (e: HttpException) {
            mutationFailure(e, FORBIDDEN_MESSAGE)
        } catch (e: IOException) {
            Result.Error(Exception("Sem conexão. Esta ação exige rede na Sprint 2."))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private companion object {
        const val FORBIDDEN_MESSAGE = "Apenas o perfil Líder pode consultar o dashboard executivo."
    }
}
