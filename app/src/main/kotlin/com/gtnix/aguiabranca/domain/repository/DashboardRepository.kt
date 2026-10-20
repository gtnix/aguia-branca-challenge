package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.DashboardByProject
import com.gtnix.aguiabranca.domain.model.DashboardByStrategy
import com.gtnix.aguiabranca.domain.model.DashboardInsight
import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.model.MetricasIa
import com.gtnix.aguiabranca.domain.util.Result

interface DashboardRepository {
    suspend fun obterResumo(orientacaoId: String? = null): Result<LeaderDashboardSnapshot>
    suspend fun obterPorEstrategia(orientacaoId: String): Result<DashboardByStrategy>
    suspend fun obterPorProjeto(projetoId: String): Result<DashboardByProject>
    suspend fun obterInsightIa(orientacaoId: String? = null): Result<DashboardInsight>
    suspend fun obterMetricasIa(orientacaoId: String? = null): Result<MetricasIa>
}
