package com.gtnix.aguiabranca.domain.usecase.ideia

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Fonte única de verdade para o número de ideias pendentes de avaliação
 * dentro de uma área. Considera tanto [StatusIdeia.PENDENTE] quanto
 * [StatusIdeia.EM_ANALISE], pois ambos exigem ação do gestor.
 */
class ContarIdeiasPendentesPorAreaUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository
) {
    operator fun invoke(area: AreaAtuacao): Flow<Int> =
        ideiaRepository.listarPorArea(area).map { ideias -> ideias.contarPendentes() }

    fun contarEm(ideias: List<Ideia>, area: AreaAtuacao): Int =
        ideias.asSequence()
            .filter { it.area == area }
            .toList()
            .contarPendentes()

    private fun List<Ideia>.contarPendentes(): Int = count {
        it.status == StatusIdeia.PENDENTE || it.status == StatusIdeia.EM_ANALISE
    }
}
