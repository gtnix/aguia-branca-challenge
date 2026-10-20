package com.gtnix.aguiabranca.presentation.screens.economia

import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeGamificacaoRepository
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogoRecompensasViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val gamificacao = FakeGamificacaoRepository()

    @Test
    fun `dado tela aberta então pede só os prêmios ativos`() = runTest {
        CatalogoRecompensasViewModel(gamificacao)
        advanceUntilIdle()

        assertEquals(listOf(false), gamificacao.recompensasChamadas)
    }

    @Test
    fun `dado catálogo com inativo então lista só os ativos`() = runTest {
        gamificacao.recompensasResult = Result.Success(
            listOf(recompensa("r1", ativo = true), recompensa("r2", ativo = false))
        )

        val vm = CatalogoRecompensasViewModel(gamificacao)
        advanceUntilIdle()

        assertFalse(vm.uiState.value.isLoading)
        assertEquals(listOf("r1"), vm.uiState.value.catalogo.map { it.id })
    }

    @Test
    fun `dado erro ao carregar então mostra a mensagem e depois limpa`() = runTest {
        gamificacao.recompensasResult = Result.Error(Exception("Sem conexão"))

        val vm = CatalogoRecompensasViewModel(gamificacao)
        advanceUntilIdle()

        assertEquals("Sem conexão", vm.uiState.value.mensagem)
        assertTrue(vm.uiState.value.catalogo.isEmpty())

        vm.consumirMensagem()
        assertNull(vm.uiState.value.mensagem)
    }

    private fun recompensa(id: String, ativo: Boolean) = Recompensa(
        id = id,
        titulo = "Prêmio $id",
        descricao = "",
        categoria = "VALE",
        parceiro = "",
        custoAsas = 60,
        estoque = 5,
        nivelMinimo = NivelUsuario.INICIANTE,
        ativo = ativo,
        podeResgatar = false,
        motivoBloqueio = null
    )
}
