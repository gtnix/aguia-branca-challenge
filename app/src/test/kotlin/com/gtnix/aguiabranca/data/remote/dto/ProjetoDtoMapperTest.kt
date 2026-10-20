package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import org.junit.Assert.assertEquals
import org.junit.Test

class ProjetoDtoMapperTest {

    @Test
    fun `mapeia roi do servidor para o dominio`() {
        val dto = ProjetoDto(
            id = "p1",
            titulo = "Telemetria",
            objetivo = "Reduzir combustível",
            descricao = "Desc",
            area = "OPERACOES",
            orientacaoId = "ori-1",
            ideiaId = "ideia-1",
            status = "EM_ANDAMENTO",
            investimento = 100_000.0,
            retorno = 10_000.0,
            roi = 20.0,
            progressoPercentual = 40,
            dataCriacao = "2026-01-15T10:00:00Z",
            dataAtualizacao = "2026-01-16T10:00:00Z"
        )

        val domain = dto.toDomain()

        assertEquals("p1", domain.id)
        assertEquals(AreaAtuacao.OPERACOES, domain.area)
        assertEquals(StatusProjeto.EM_ANDAMENTO, domain.status)
        assertEquals("ideia-1", domain.ideiaOrigemId)
        assertEquals(20.0, domain.roiServidor)
        assertEquals(20.0, domain.roi, 0.01)
        assertEquals(100_000.0, domain.investimentoRealizado, 0.01)
        assertEquals(10_000.0, domain.retornoRealizadoMensal, 0.01)
    }
}
