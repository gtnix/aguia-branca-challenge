package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.CriterioRubrica
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.FrequenciaProblema
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Test

class IdeiaDtoMapperTest {

    @Test
    fun `mapeia contrato da API para dominio`() {
        val dto = IdeiaDto(
            id = "ideia-1",
            autorUserId = "user-operador",
            titulo = "Rastreamento",
            descricao = "Telemetria da frota",
            tipo = "PROBLEMA",
            area = "LOGISTICA",
            orientacaoId = "ori-1",
            status = "PENDENTE",
            impacto = 4,
            esforco = 2,
            scoreIa = 87,
            justificativaIa = "Alto alinhamento",
            scoreIaFallback = false,
            dataCriacao = "2026-01-15T10:00:00Z"
        )

        val domain = dto.toDomain()

        assertEquals("ideia-1", domain.id)
        assertEquals("user-operador", domain.autorId)
        assertEquals("user-operador", domain.autorNome)
        assertEquals(TipoIdeia.PROBLEMA, domain.tipo)
        assertEquals(AreaAtuacao.LOGISTICA, domain.area)
        assertEquals("ori-1", domain.orientacaoId)
        assertEquals(StatusIdeia.PENDENTE, domain.status)
        assertEquals(4, domain.impactoEstimado)
        assertEquals(87, domain.scoreIa)
        assertEquals("Alto alinhamento", domain.justificativaIa)
    }

    @Test
    fun `autorNome da API prevalece sobre o id`() {
        val dto = IdeiaDto(
            id = "ideia-1",
            autorUserId = "user-operador",
            autorNome = "Pedro Santos",
            titulo = "Rastreamento",
            status = "PENDENTE",
            orientacaoId = "ori-1"
        )

        assertEquals("Pedro Santos", dto.toDomain().autorNome)
    }

    @Test
    fun `mapeia analise da IA e contexto do colaborador`() {
        val dto = IdeiaDto(
            id = "ideia-1",
            titulo = "Manifesto digital",
            status = "PENDENTE",
            orientacaoId = "ori-1",
            problema = "Seis vias por viagem",
            publicoAfetado = " ",
            frequencia = "DIARIA",
            scoreIa = 72,
            analiseIa = AnaliseIaDto(
                status = "IA",
                faixa = "ALTA",
                criterios = listOf(
                    CriterioIaDto("ALINHAMENTO", 4, 30, "Cita custo logístico"),
                    CriterioIaDto("CRITERIO_NOVO", 3, 5, "ignorado")
                ),
                riscos = listOf("Exigência fiscal"),
                perguntas = listOf("Quantas folhas por dia?"),
                impactoSugerido = 4,
                esforcoSugerido = 2,
                estimativaOrigem = "GESTOR",
                concordaIa = true
            )
        )

        val domain = dto.toDomain()
        val analise = domain.analiseIa!!

        assertEquals("Seis vias por viagem", domain.problema)
        assertNull(domain.publicoAfetado)
        assertEquals(FrequenciaProblema.DIARIA, domain.frequencia)
        assertEquals(StatusAnaliseIa.IA, analise.status)
        assertEquals(FaixaIa.ALTA, analise.faixa)
        assertEquals(listOf(CriterioRubrica.ALINHAMENTO), analise.criterios.map { it.criterio })
        assertEquals(30, analise.criterios.single().peso)
        assertEquals(listOf("Exigência fiscal"), analise.riscos)
        assertEquals(4, analise.impactoSugerido)
        assertEquals(OrigemEstimativa.GESTOR, analise.estimativaOrigem)
        assertEquals(true, analise.concordaIa)
    }

    @Test
    fun `operador recebe ideia sem analise da IA`() {
        val dto = IdeiaDto(id = "ideia-1", titulo = "X", status = "PENDENTE", orientacaoId = "ori-1")

        assertNull(dto.toDomain().analiseIa)
    }

    @Test
    fun `requisicao do colaborador nao envia impacto nem esforco`() {
        val ideia = Ideia(
            id = "",
            titulo = "Manifesto",
            descricao = "QR code",
            tipo = TipoIdeia.IDEIA,
            area = AreaAtuacao.OPERACOES,
            autorId = "user-1",
            autorNome = "Pedro",
            orientacaoId = "ori-1",
            impactoEstimado = 5,
            esforcoEstimado = 1,
            frequencia = FrequenciaProblema.MENSAL,
            beneficio = ""
        )

        val request = ideia.toRequestDto()

        assertEquals("MENSAL", request.frequencia)
        assertNull(request.beneficio)
        val campos = IdeiaRequestDto::class.java.declaredFields.map { it.name }.toSet()
        assertFalse("impacto" in campos)
        assertFalse("esforco" in campos)
    }

    @Test
    fun `status desconhecido cai em PENDENTE`() {
        val dto = IdeiaDto(
            id = "ideia-1",
            titulo = "X",
            status = "STATUS_INEXISTENTE",
            orientacaoId = "ori-1"
        )

        assertEquals(StatusIdeia.PENDENTE, dto.toDomain().status)
    }
}
