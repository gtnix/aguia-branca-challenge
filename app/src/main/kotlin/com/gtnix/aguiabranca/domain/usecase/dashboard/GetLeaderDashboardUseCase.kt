package com.gtnix.aguiabranca.domain.usecase.dashboard

import com.gtnix.aguiabranca.domain.model.LeaderDashboardSnapshot
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

class GetLeaderDashboardUseCase @Inject constructor(
    private val dashboardRepository: DashboardRepository
) {
    operator fun invoke(orientacaoId: String? = null): Flow<Result<LeaderDashboardSnapshot>> = flow {
        emit(dashboardRepository.obterResumo(orientacaoId))
    }.catch { e ->
        emit(
            Result.Error(
                Exception(
                    if (e is IOException) {
                        "Sem conexão. Esta ação exige rede na Sprint 2."
                    } else {
                        e.message
                    }
                )
            )
        )
    }
}
