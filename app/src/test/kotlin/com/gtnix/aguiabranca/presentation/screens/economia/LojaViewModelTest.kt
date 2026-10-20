package com.gtnix.aguiabranca.presentation.screens.economia

import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.Recompensa
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeGamificacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LojaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var gamificacao: FakeGamificacaoRepository
    private lateinit var sessionManager: FakeSessionManager

    private fun recompensa(id: String, custo: Int, pode: Boolean, motivo: String? = null) = Recompensa(
        id = id,
        titulo = id,
        descricao = "",
        categoria = "Benefício",
        parceiro = "",
        custoAsas = custo,
        estoque = 3,
        nivelMinimo = NivelUsuario.INICIANTE,
        ativo = true,
        podeResgatar = pode,
        motivoBloqueio = motivo
    )

    @Before
    fun setup() {
        gamificacao = FakeGamificacaoRepository()
        sessionManager = FakeSessionManager()
        sessionManager.setUser(TestDataFactory.createUsuario(id = "pedro", perfil = PerfilUsuario.OPERADOR))
        gamificacao.painelResult = Result.Success(
            Pontuacao(total = 70, nivel = NivelUsuario.EM_ASCENSAO, progressoProximoNivel = 0.2f, pontosParaProximoNivel = 80, asas = 40)
        )
        gamificacao.recompensasResult = Result.Success(
            listOf(
                recompensa("rec-folga", custo = 120, pode = false, motivo = "Faltam 80 Asas"),
                recompensa("rec-cafe", custo = 20, pode = true),
                recompensa("rec-livro", custo = 35, pode = true)
            )
        )
    }

    @Test
    fun `dado catalogo quando carregar então mostra saldo e disponíveis primeiro`() = runTest {
        val vm = LojaViewModel(gamificacao, sessionManager)
        advanceUntilIdle()

        val state = vm.uiState.value
        assertEquals(40, state.asas)
        assertEquals(listOf("rec-cafe", "rec-livro", "rec-folga"), state.recompensasOrdenadas.map { it.id })
    }

    @Test
    fun `dado recompensa bloqueada quando pedir confirmação então ignora`() = runTest {
        val vm = LojaViewModel(gamificacao, sessionManager)
        advanceUntilIdle()

        vm.pedirConfirmacao(vm.uiState.value.recompensas.first { it.id == "rec-folga" })

        assertNull(vm.uiState.value.confirmando)
    }

    @Test
    fun `dado confirmação quando resgatar então chama servidor e recarrega`() = runTest {
        val vm = LojaViewModel(gamificacao, sessionManager)
        advanceUntilIdle()

        vm.pedirConfirmacao(vm.uiState.value.recompensas.first { it.id == "rec-cafe" })
        vm.confirmarResgate()
        advanceUntilIdle()

        assertEquals(listOf("rec-cafe"), gamificacao.resgatesSolicitados)
        assertTrue(vm.uiState.value.resgateConcluido)
        assertNull(vm.uiState.value.confirmando)
    }

    @Test
    fun `dado saldo insuficiente no servidor quando resgatar então mostra motivo do 409`() = runTest {
        gamificacao.resgatarResult = Result.Error(Exception("Faltam 5 Asas"))
        val vm = LojaViewModel(gamificacao, sessionManager)
        advanceUntilIdle()

        vm.pedirConfirmacao(vm.uiState.value.recompensas.first { it.id == "rec-livro" })
        vm.confirmarResgate()
        advanceUntilIdle()

        assertEquals("Faltam 5 Asas", vm.uiState.value.mensagem)
    }
}
