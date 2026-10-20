package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.util.Result
import com.gtnix.aguiabranca.fake.FakeGamificacaoRepository
import com.gtnix.aguiabranca.fake.FakeUsuarioRepository
import com.gtnix.aguiabranca.fake.TestDataFactory
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class CalcularPontuacaoUseCaseTest {

    private lateinit var gamificacao: FakeGamificacaoRepository
    private lateinit var usuarios: FakeUsuarioRepository
    private lateinit var useCase: CalcularPontuacaoUseCase

    private val painelServidor = Pontuacao(
        total = 70,
        nivel = NivelUsuario.EM_ASCENSAO,
        progressoProximoNivel = 0.2f,
        pontosParaProximoNivel = 80,
        asas = 40,
        xpTemporada = 70
    )

    @Before
    fun setup() {
        gamificacao = FakeGamificacaoRepository()
        usuarios = FakeUsuarioRepository()
        useCase = CalcularPontuacaoUseCase(gamificacao, usuarios)
    }

    @Test
    fun `dado painel do servidor quando calcular então devolve sem recalcular localmente`() = runTest {
        gamificacao.painelResult = Result.Success(painelServidor)

        val resultado = useCase.calcular("user-1", PerfilUsuario.OPERADOR)

        assertSame(painelServidor, resultado)
        assertEquals(listOf(PerfilUsuario.OPERADOR), gamificacao.painelChamadas)
    }

    @Test
    fun `dado usuario em cache quando calcular sem perfil então usa o perfil do cadastro`() = runTest {
        usuarios.salvar(TestDataFactory.createUsuario(id = "gestor-1", perfil = PerfilUsuario.GESTOR))
        gamificacao.painelResult = Result.Success(painelServidor)

        useCase.calcular("gestor-1")

        assertEquals(listOf(PerfilUsuario.GESTOR), gamificacao.painelChamadas)
    }

    @Test
    fun `dado usuario desconhecido quando calcular sem perfil então assume operador`() = runTest {
        useCase.calcular("ninguem")

        assertEquals(listOf(PerfilUsuario.OPERADOR), gamificacao.painelChamadas)
    }

    @Test
    fun `dado erro do servidor quando calcular então propaga a exceção`() = runTest {
        val erro = IllegalStateException("offline")
        gamificacao.painelResult = Result.Error(erro)

        try {
            useCase.calcular("user-1", PerfilUsuario.OPERADOR)
            fail("deveria lançar")
        } catch (e: IllegalStateException) {
            assertSame(erro, e)
        }
    }
}
