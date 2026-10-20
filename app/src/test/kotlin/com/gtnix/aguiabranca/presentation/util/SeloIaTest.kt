package com.gtnix.aguiabranca.presentation.util

import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AnaliseIa
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.fake.TestDataFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SeloIaTest {

    private val analisada = TestDataFactory.createIdeia().copy(
        scoreIa = 82,
        analiseIa = AnaliseIa(status = StatusAnaliseIa.IA, faixa = FaixaIa.ALTA)
    )

    @Test
    fun `operador nao ve selo nem quando o score existe`() {
        assertNull(seloIa(analisada, PerfilUsuario.OPERADOR))
        assertNull(seloIa(analisada, null))
    }

    @Test
    fun `gestor e lider veem faixa e origem`() {
        for (perfil in listOf(PerfilUsuario.GESTOR, PerfilUsuario.LIDER)) {
            val selo = seloIa(analisada, perfil)!!
            assertEquals(FaixaIa.ALTA, selo.faixa)
            assertFalse(selo.isEstimativa)
            assertEquals(R.string.ia_selo_ia_alta, selo.labelRes())
        }
    }

    @Test
    fun `fallback aparece como estimativa e nao como IA`() {
        val estimada = analisada.copy(
            scoreIa = 55,
            analiseIa = AnaliseIa(status = StatusAnaliseIa.ESTIMATIVA, faixa = FaixaIa.MEDIA)
        )

        val selo = seloIa(estimada, PerfilUsuario.GESTOR)!!

        assertTrue(selo.isEstimativa)
        assertEquals(R.string.ia_selo_estimativa_media, selo.labelRes())
    }

    @Test
    fun `dados insuficientes nao inventam faixa`() {
        val semDados = analisada.copy(
            scoreIa = null,
            analiseIa = AnaliseIa(status = StatusAnaliseIa.DADOS_INSUFICIENTES)
        )

        val selo = seloIa(semDados, PerfilUsuario.GESTOR)!!

        assertNull(selo.faixa)
        assertEquals(R.string.ia_selo_dados_insuficientes, selo.labelRes())
    }

    @Test
    fun `estimativa salva pelo gestor sem analise nao gera selo`() {
        val soEstimativa = analisada.copy(
            scoreIa = null,
            analiseIa = AnaliseIa(estimativaOrigem = OrigemEstimativa.GESTOR)
        )

        assertNull(seloIa(soEstimativa, PerfilUsuario.GESTOR))
    }

    @Test
    fun `score legado sem analise usa a flag de fallback`() {
        val legado = analisada.copy(scoreIa = 30, analiseIa = null, scoreIaFallback = true)

        val selo = seloIa(legado, PerfilUsuario.GESTOR)!!

        assertEquals(FaixaIa.BAIXA, selo.faixa)
        assertTrue(selo.isEstimativa)
    }

    @Test
    fun `cortes de faixa seguem o backend`() {
        assertEquals(FaixaIa.ALTA, faixaDoScore(70))
        assertEquals(FaixaIa.MEDIA, faixaDoScore(69))
        assertEquals(FaixaIa.MEDIA, faixaDoScore(40))
        assertEquals(FaixaIa.BAIXA, faixaDoScore(39))
    }
}
