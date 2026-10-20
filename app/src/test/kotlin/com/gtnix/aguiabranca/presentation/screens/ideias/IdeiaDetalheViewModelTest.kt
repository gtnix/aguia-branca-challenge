package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.SavedStateHandle
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.model.AnaliseIa
import com.gtnix.aguiabranca.domain.model.FaixaIa
import com.gtnix.aguiabranca.domain.model.OrigemEstimativa
import com.gtnix.aguiabranca.domain.model.StatusAnaliseIa
import com.gtnix.aguiabranca.domain.usecase.ideia.AprovarIdeiaUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.GerarRascunhoFeedbackUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.SalvarEstimativaUseCase
import com.gtnix.aguiabranca.domain.usecase.ideia.ScoreIdeiasUseCase
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeSessionManager
import com.gtnix.aguiabranca.fake.TestDataFactory
import com.gtnix.aguiabranca.presentation.navigation.Destination
import com.gtnix.aguiabranca.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

@OptIn(ExperimentalCoroutinesApi::class)
class IdeiaDetalheViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: IdeiaDetalheViewModel
    private lateinit var ideiaRepository: FakeIdeiaRepository
    private lateinit var orientacaoRepository: FakeOrientacaoRepository
    private lateinit var sessionManager: FakeSessionManager
    private val aprovarIdeiaUseCase = mockk<AprovarIdeiaUseCase>()

    private val ideia = TestDataFactory.createIdeia(
        id = "ideia-123",
        titulo = "Minha Ideia",
        status = StatusIdeia.PENDENTE
    )

    @Before
    fun setup() {
        ideiaRepository = FakeIdeiaRepository(listOf(ideia))
        orientacaoRepository = FakeOrientacaoRepository()
        sessionManager = FakeSessionManager()
    }

    private fun createViewModel(ideiaId: String = "ideia-123") {
        val savedStateHandle = SavedStateHandle(mapOf(Destination.IdeiaDetalhe.ARG_IDEIA_ID to ideiaId))
        viewModel = IdeiaDetalheViewModel(
            ideiaRepository,
            orientacaoRepository,
            aprovarIdeiaUseCase,
            ScoreIdeiasUseCase(ideiaRepository, sessionManager),
            SalvarEstimativaUseCase(ideiaRepository, sessionManager),
            GerarRascunhoFeedbackUseCase(ideiaRepository, sessionManager),
            sessionManager,
            savedStateHandle
        )
    }

    @Test
    fun `dado ideia existente quando carrega então exibe dados`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)

        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.ideia)
        assertEquals("Minha Ideia", state.ideia?.titulo)
        assertEquals(PerfilUsuario.GESTOR, state.perfil)
    }

    @Test
    fun `dado ideia inexistente quando carrega então ideia é null`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel(ideiaId = "inexistente")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNull(state.ideia)
    }

    @Test
    fun `dado gestor quando aprova ideia com sucesso então actionSuccess é true`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)
        coEvery { aprovarIdeiaUseCase(any(), any(), any(), any(), any()) } returns Result.Success(Unit)

        createViewModel()
        advanceUntilIdle()

        viewModel.aprovar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.actionSuccess)
    }

    @Test
    fun `dado gestor quando reprova sem feedback então exibe erro`() = runTest {
        val gestor = TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR)
        sessionManager.setUser(gestor)

        createViewModel()
        advanceUntilIdle()

        viewModel.reprovar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(R.string.error_feedback_required_reject, state.errorMessageRes)
    }

    @Test
    fun `dado operador quando tenta aprovar então não faz nada`() = runTest {
        val operador = TestDataFactory.createUsuario(perfil = PerfilUsuario.OPERADOR)
        sessionManager.setUser(operador)

        createViewModel()
        advanceUntilIdle()

        viewModel.aprovar()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.actionSuccess)
    }

    @Test
    fun `dado usuario quando altera feedback então atualiza state e limpa erro`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel()
        advanceUntilIdle()

        viewModel.onFeedbackChange("Ótima ideia!")

        val state = viewModel.uiState.value
        assertEquals("Ótima ideia!", state.feedback)
        assertNull(state.errorMessageRes)
    }

    @Test
    fun `dado usuario quando vota então jaVotou é true`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel()
        advanceUntilIdle()

        viewModel.upvote()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.jaVotou)
    }

    @Test
    fun `dado usuario que ja votou quando vota novamente então não incrementa`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario())

        createViewModel()
        advanceUntilIdle()

        viewModel.upvote()
        advanceUntilIdle()
        val upvotesAposUm = viewModel.uiState.value.ideia?.upvotes

        viewModel.upvote()
        advanceUntilIdle()

        assertEquals(upvotesAposUm, viewModel.uiState.value.ideia?.upvotes)
    }

    @Test
    fun `dado 403 quando carrega então mostra acesso negado`() = runTest {
        val repo = mockk<IdeiaRepository>()
        coEvery { repo.buscarPorId("ideia-123") } throws httpException(403)
        sessionManager.setUser(TestDataFactory.createUsuario())
        viewModel = IdeiaDetalheViewModel(
            repo,
            orientacaoRepository,
            aprovarIdeiaUseCase,
            ScoreIdeiasUseCase(repo, sessionManager),
            SalvarEstimativaUseCase(repo, sessionManager),
            GerarRascunhoFeedbackUseCase(repo, sessionManager),
            sessionManager,
            SavedStateHandle(mapOf(Destination.IdeiaDetalhe.ARG_IDEIA_ID to "ideia-123"))
        )
        advanceUntilIdle()

        assertEquals(R.string.error_ideia_access_denied, viewModel.uiState.value.errorMessageRes)
        assertNull(viewModel.uiState.value.ideia)
    }

    @Test
    fun `dado operador dono quando exclui então marca deleted`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(id = "user-1", perfil = PerfilUsuario.OPERADOR))
        createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.canDelete)
        viewModel.excluir()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.deleted)
        assertNull(ideiaRepository.getById("ideia-123"))
    }

    @Test
    fun `dado gestor quando carrega então nao pode excluir`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(id = "user-gestor", perfil = PerfilUsuario.GESTOR))
        createViewModel()
        advanceUntilIdle()

        assertTrue(!viewModel.uiState.value.canDelete)
    }

    @Test
    fun `dado operador dono de ideia convertida então nao pode excluir`() = runTest {
        val convertida = TestDataFactory.createIdeia(
            id = "ideia-123",
            status = StatusIdeia.CONVERTIDA_PROJETO
        )
        ideiaRepository = FakeIdeiaRepository(listOf(convertida))
        sessionManager.setUser(TestDataFactory.createUsuario(id = "user-1", perfil = PerfilUsuario.OPERADOR))
        createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.canDelete)
        viewModel.excluir()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.deleted)
        assertNotNull(ideiaRepository.getById("ideia-123"))
    }

    @Test
    fun `dado 400 ao excluir então mostra somente pendente`() = runTest {
        val repo = mockk<IdeiaRepository>()
        coEvery { repo.buscarPorId("ideia-123") } returns ideia
        coEvery { repo.excluir("ideia-123") } throws httpException(400)
        sessionManager.setUser(TestDataFactory.createUsuario(id = "user-1", perfil = PerfilUsuario.OPERADOR))
        viewModel = IdeiaDetalheViewModel(
            repo,
            orientacaoRepository,
            aprovarIdeiaUseCase,
            ScoreIdeiasUseCase(repo, sessionManager),
            SalvarEstimativaUseCase(repo, sessionManager),
            GerarRascunhoFeedbackUseCase(repo, sessionManager),
            sessionManager,
            SavedStateHandle(mapOf(Destination.IdeiaDetalhe.ARG_IDEIA_ID to "ideia-123"))
        )
        advanceUntilIdle()

        viewModel.excluir()
        advanceUntilIdle()

        assertEquals(R.string.ideia_excluir_somente_pendente, viewModel.uiState.value.errorMessageRes)
        assertFalse(viewModel.uiState.value.deleted)
    }

    @Test
    fun `sliders partem da sugestao da IA quando nao ha valor oficial`() = runTest {
        ideiaRepository = FakeIdeiaRepository(listOf(ideiaAnalisada()))
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(4f, state.impacto)
        assertEquals(2f, state.esforco)
    }

    @Test
    fun `valor oficial do gestor tem precedencia sobre a sugestao`() = runTest {
        ideiaRepository = FakeIdeiaRepository(
            listOf(ideiaAnalisada().copy(impactoEstimado = 2, esforcoEstimado = 5))
        )
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        createViewModel()
        advanceUntilIdle()

        assertEquals(2f, viewModel.uiState.value.impacto)
        assertEquals(5f, viewModel.uiState.value.esforco)
    }

    @Test
    fun `aprovar com estimativa ajustada salva a estimativa antes da decisao`() = runTest {
        ideiaRepository = FakeIdeiaRepository(listOf(ideiaAnalisada()))
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        coEvery { aprovarIdeiaUseCase(any(), any(), any(), any(), any()) } returns Result.Success(Unit)
        createViewModel()
        advanceUntilIdle()

        viewModel.onImpactoChange(5f)
        viewModel.aprovar()
        advanceUntilIdle()

        val salva = ideiaRepository.getById("ideia-123")!!
        assertEquals(5, salva.impactoEstimado)
        assertEquals(2, salva.esforcoEstimado)
        assertEquals(OrigemEstimativa.GESTOR, salva.analiseIa?.estimativaOrigem)
        coVerify { aprovarIdeiaUseCase("ideia-123", true, null, null, null) }
    }

    @Test
    fun `estimativa igual a oficial nao chama a API de novo`() = runTest {
        val repo = mockk<IdeiaRepository>(relaxed = true)
        val oficial = ideiaAnalisada().copy(impactoEstimado = 4, esforcoEstimado = 2)
        coEvery { repo.buscarPorId("ideia-123") } returns oficial
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        coEvery { aprovarIdeiaUseCase(any(), any(), any(), any(), any()) } returns Result.Success(Unit)
        viewModel = IdeiaDetalheViewModel(
            repo,
            orientacaoRepository,
            aprovarIdeiaUseCase,
            ScoreIdeiasUseCase(repo, sessionManager),
            SalvarEstimativaUseCase(repo, sessionManager),
            GerarRascunhoFeedbackUseCase(repo, sessionManager),
            sessionManager,
            SavedStateHandle(mapOf(Destination.IdeiaDetalhe.ARG_IDEIA_ID to "ideia-123"))
        )
        advanceUntilIdle()

        viewModel.aprovar()
        advanceUntilIdle()

        coVerify(exactly = 0) { repo.salvarEstimativa(any(), any(), any(), any()) }
        assertTrue(viewModel.uiState.value.actionSuccess)
    }

    @Test
    fun `lider nao salva estimativa`() = runTest {
        ideiaRepository = FakeIdeiaRepository(listOf(ideiaAnalisada()))
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.LIDER))
        createViewModel()
        advanceUntilIdle()

        viewModel.onImpactoChange(1f)
        viewModel.salvarEstimativa()
        advanceUntilIdle()

        assertEquals(0, ideiaRepository.getById("ideia-123")?.impactoEstimado)
        assertTrue(viewModel.uiState.value.canViewIa)
    }

    @Test
    fun `discordancia da IA vai junto com a decisao`() = runTest {
        ideiaRepository = FakeIdeiaRepository(listOf(ideiaAnalisada()))
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        coEvery { aprovarIdeiaUseCase(any(), any(), any(), any(), any()) } returns Result.Success(Unit)
        createViewModel()
        advanceUntilIdle()

        viewModel.onConcordaIaChange(false)
        viewModel.onMotivoDiscordanciaChange("Não considerou o custo de TI")
        viewModel.aprovar()
        advanceUntilIdle()

        coVerify {
            aprovarIdeiaUseCase("ideia-123", true, null, false, "Não considerou o custo de TI")
        }
    }

    @Test
    fun `sem score nao envia opiniao sobre a IA`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        coEvery { aprovarIdeiaUseCase(any(), any(), any(), any(), any()) } returns Result.Success(Unit)
        createViewModel()
        advanceUntilIdle()

        viewModel.onConcordaIaChange(true)
        viewModel.aprovar()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.temRecomendacaoIa)
        coVerify { aprovarIdeiaUseCase("ideia-123", true, null, null, null) }
    }

    @Test
    fun `tocar de novo no mesmo botao desfaz a opiniao`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        createViewModel()
        advanceUntilIdle()

        viewModel.onConcordaIaChange(false)
        viewModel.onMotivoDiscordanciaChange("motivo")
        viewModel.onConcordaIaChange(false)

        assertNull(viewModel.uiState.value.concordaIa)
        assertEquals("", viewModel.uiState.value.motivoDiscordancia)
    }

    @Test
    fun `rascunho da IA preenche o feedback editavel`() = runTest {
        sessionManager.setUser(TestDataFactory.createUsuario(perfil = PerfilUsuario.GESTOR))
        createViewModel()
        advanceUntilIdle()

        viewModel.onFeedbackChange("sem orçamento em 2026")
        viewModel.gerarRascunhoFeedback()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Obrigado por enviar \"Minha Ideia\". sem orçamento em 2026", state.feedback)
        assertTrue(state.rascunhoFallback)
        assertFalse(state.rascunhoEmAndamento)
        assertEquals(StatusIdeia.PENDENTE, ideiaRepository.getById("ideia-123")?.status)
    }

    private fun ideiaAnalisada() = ideia.copy(
        scoreIa = 78,
        analiseIa = AnaliseIa(
            status = StatusAnaliseIa.IA,
            faixa = FaixaIa.ALTA,
            impactoSugerido = 4,
            esforcoSugerido = 2
        )
    )

    private fun httpException(code: Int): HttpException {
        val body = "{}".toResponseBody("application/json".toMediaType())
        return HttpException(Response.error<Any>(code, body))
    }
}
