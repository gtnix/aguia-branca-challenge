package com.gtnix.aguiabranca.presentation.screens.orientacoes

import com.gtnix.aguiabranca.domain.model.CampanhaPatrocinada
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class CampanhaFormStateTest {

    private val hoje = LocalDate.of(2026, 10, 19)

    @Test
    fun `campanha desligada não envia campos`() {
        assertNull(CampanhaFormState(ativa = false).toDomain(hoje))
    }

    @Test
    fun `campanha ligada calcula fim a partir de hoje`() {
        val campanha = CampanhaFormState(ativa = true, dias = "30", bonusAsas = "50", topN = "3").toDomain(hoje)

        assertEquals(CampanhaPatrocinada(LocalDate.of(2026, 11, 18), 50, 3), campanha)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `top N fora da faixa é rejeitado`() {
        CampanhaFormState(ativa = true, topN = "0").toDomain(hoje)
    }

    @Test
    fun `editar campanha aberta preserva a data de fim`() {
        val original = CampanhaPatrocinada(LocalDate.of(2026, 11, 2), bonusAsas = 40, topN = 5)

        val form = CampanhaFormState.de(original, hoje)

        assertTrue(form.ativa)
        assertEquals(original.fim, form.toDomain(hoje)?.fim)
    }

    @Test
    fun `campanha encerrada abre desligada e marcada como encerrada`() {
        val form = CampanhaFormState.de(
            CampanhaPatrocinada(LocalDate.of(2026, 10, 1), 30, 3, ativa = false, encerrada = true),
            hoje
        )

        assertFalse(form.ativa)
        assertTrue(form.encerrada)
    }
}
