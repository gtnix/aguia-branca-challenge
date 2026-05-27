package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.TipoConquista
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase.Companion.PONTOS_OPERADOR_BONUS_APROVADA
import com.gtnix.aguiabranca.domain.usecase.CalcularPontuacaoUseCase.Companion.PONTOS_OPERADOR_POR_IDEIA
import com.gtnix.aguiabranca.fake.FakeIdeiaRepository
import com.gtnix.aguiabranca.fake.FakeOrientacaoRepository
import com.gtnix.aguiabranca.fake.FakeProjetoRepository
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalcularPontuacaoUseCaseTest {

    private lateinit var useCase: CalcularPontuacaoUseCase
    private lateinit var fakeIdeiaRepository: FakeIdeiaRepository

    private val userId = "user-operador"

    @Before
    fun setup() {
        fakeIdeiaRepository = FakeIdeiaRepository()
        useCase = CalcularPontuacaoUseCase(
            fakeIdeiaRepository,
            FakeProjetoRepository(),
            FakeOrientacaoRepository(),
            FakeUsuarioRepository()
        )
    }

    @Test
    fun `conquista bem vindo sempre desbloqueada`() = runTest {
        val pontuacao = useCase.calcular(userId)

        assertTrue(pontuacao.conquistas.first { it.tipo == TipoConquista.BEM_VINDO }.desbloqueada)
    }

    @Test
    fun `usuario sem ideias retorna zero pontos e nivel iniciante`() = runTest {
        val pontuacao = useCase.calcular(userId)

        assertEquals(0, pontuacao.total)
        assertEquals(NivelUsuario.INICIANTE, pontuacao.nivel)
        assertFalse(pontuacao.conquistas.first { it.tipo == TipoConquista.PRIMEIRA_IDEIA }.desbloqueada)
    }

    @Test
    fun `cada ideia submetida vale 10 pontos`() = runTest {
        fakeIdeiaRepository.salvar(
            TestDataFactory.createIdeia(id = "ideia-1", autorId = userId)
        )
        fakeIdeiaRepository.salvar(
            TestDataFactory.createIdeia(id = "ideia-2", autorId = userId)
        )

        val pontuacao = useCase.calcular(userId)

        assertEquals(2 * PONTOS_OPERADOR_POR_IDEIA, pontuacao.total)
        assertTrue(pontuacao.conquistas.first { it.tipo == TipoConquista.PRIMEIRA_IDEIA }.desbloqueada)
    }

    @Test
    fun `ideia aprovada recebe bonus de 5 pontos`() = runTest {
        fakeIdeiaRepository.salvar(
            TestDataFactory.createIdeia(
                id = "ideia-1",
                autorId = userId,
                status = StatusIdeia.APROVADA
            )
        )

        val pontuacao = useCase.calcular(userId)

        assertEquals(PONTOS_OPERADOR_POR_IDEIA + PONTOS_OPERADOR_BONUS_APROVADA, pontuacao.total)
        assertTrue(pontuacao.conquistas.first { it.tipo == TipoConquista.PRIMEIRA_APROVACAO }.desbloqueada)
    }

    @Test
    fun `pontuacao de 45 retorna nivel em ascensao`() = runTest {
        repeat(3) { index ->
            fakeIdeiaRepository.salvar(
                TestDataFactory.createIdeia(
                    id = "ideia-$index",
                    autorId = userId
                )
            )
        }
        fakeIdeiaRepository.salvar(
            TestDataFactory.createIdeia(
                id = "ideia-3",
                autorId = userId,
                status = StatusIdeia.APROVADA
            )
        )

        val pontuacao = useCase.calcular(userId)

        assertEquals(45, pontuacao.total)
        assertEquals(NivelUsuario.EM_ASCENSAO, pontuacao.nivel)
    }

    @Test
    fun `pontuacao acima de 120 promove para nivel engajado`() = runTest {
        repeat(10) { index ->
            fakeIdeiaRepository.salvar(
                TestDataFactory.createIdeia(
                    id = "ideia-$index",
                    autorId = userId,
                    status = StatusIdeia.APROVADA
                )
            )
        }

        val pontuacao = useCase.calcular(userId)

        assertEquals(150, pontuacao.total)
        assertEquals(NivelUsuario.ENGAJADO, pontuacao.nivel)
    }
}
