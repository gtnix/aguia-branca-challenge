package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OrientacaoDtoMapperTest {

    @Test
    fun `mapeia contrato da API para dominio com historico`() {
        val dto = OrientacaoDto(
            id = "ori-1",
            titulo = "Eficiência operacional",
            descricao = "Reduzir custos",
            categoria = "EFICIENCIA_OPERACIONAL",
            campanha = "Águia 2026 — Inovação em Campo",
            dataCriacao = "2026-01-15T10:00:00Z",
            dataAtualizacao = "2026-01-16T10:00:00Z",
            ativo = true,
            criadoPorUserId = "user-lider"
        )

        val domain = dto.toDomain()

        assertEquals("ori-1", domain.id)
        assertEquals(CategoriaOrientacao.EFICIENCIA_OPERACIONAL, domain.categoria)
        assertEquals("Águia 2026 — Inovação em Campo", domain.campanha)
        assertEquals("user-lider", domain.criadoPor)
        assertTrue(domain.ativa)
        assertEquals(java.time.Instant.parse("2026-01-15T10:00:00Z").toEpochMilli(), domain.dataCriacao)
    }

    @Test
    fun `request envia categoria e campanha`() {
        val domain = dtoSample().toDomain()
        val request = domain.toRequestDto()

        assertEquals("EFICIENCIA_OPERACIONAL", request.categoria)
        assertEquals("Águia 2026 — Inovação em Campo", request.campanha)
        assertEquals("Eficiência operacional", request.titulo)
    }

    private fun dtoSample() = OrientacaoDto(
        id = "ori-1",
        titulo = "Eficiência operacional",
        descricao = "Reduzir custos",
        categoria = "EFICIENCIA_OPERACIONAL",
        campanha = "Águia 2026 — Inovação em Campo",
        dataCriacao = "2026-01-15T10:00:00Z",
        ativo = true,
        criadoPorUserId = "user-lider"
    )
}
