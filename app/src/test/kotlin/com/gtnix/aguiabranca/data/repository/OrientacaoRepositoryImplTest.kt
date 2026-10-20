package com.gtnix.aguiabranca.data.repository

import com.gtnix.aguiabranca.data.remote.NetworkMonitor
import com.gtnix.aguiabranca.data.remote.api.OrientacaoApiService
import com.gtnix.aguiabranca.data.remote.dto.OrientacaoDto
import com.gtnix.aguiabranca.data.remote.dto.OrientacaoRequestDto
import com.gtnix.aguiabranca.domain.model.CategoriaOrientacao
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.fail
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class OrientacaoRepositoryImplTest {

    private val api = mockk<OrientacaoApiService>()
    private val networkMonitor = mockk<NetworkMonitor>()

    @Test
    fun `listarTodas busca na API incluindo inativas`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.listar(apenasAtivas = false) } returns listOf(
            sampleDto(id = "a", ativo = true),
            sampleDto(id = "b", ativo = false)
        )
        val repository = OrientacaoRepositoryImpl(api, networkMonitor)

        val result = repository.listarTodas().first()

        assertEquals(2, result.size)
        assertFalse(result.first { it.id == "b" }.ativa)
    }

    @Test
    fun `listarAtivas pede apenas ativas na API`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.listar(apenasAtivas = true) } returns listOf(sampleDto(id = "a", ativo = true))
        val repository = OrientacaoRepositoryImpl(api, networkMonitor)

        val result = repository.listarAtivas().first()

        assertEquals(1, result.size)
        coVerify { api.listar(apenasAtivas = true) }
    }

    @Test
    fun `salvar sem id faz POST`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.criar(any()) } returns sampleDto(id = "novo")
        coEvery { api.listar(apenasAtivas = false) } returns listOf(sampleDto(id = "novo"))
        val repository = OrientacaoRepositoryImpl(api, networkMonitor)

        repository.salvar(
            OrientacaoEstrategica(
                id = "",
                titulo = "Nova",
                descricao = "Desc",
                categoria = CategoriaOrientacao.EFICIENCIA_OPERACIONAL,
                criadoPor = "user-lider",
                campanha = "Águia 2026"
            )
        )

        coVerify {
            api.criar(
                OrientacaoRequestDto(
                    titulo = "Nova",
                    descricao = "Desc",
                    categoria = "EFICIENCIA_OPERACIONAL",
                    campanha = "Águia 2026"
                )
            )
        }
    }

    @Test
    fun `mutacao sem rede falha`() = runTest {
        every { networkMonitor.isOnline() } returns false
        val repository = OrientacaoRepositoryImpl(api, networkMonitor)

        try {
            repository.excluir("ori-1")
            fail("deveria exigir rede")
        } catch (_: IOException) {
            // esperado
        }
    }

    @Test
    fun `excluir chama DELETE e atualiza lista`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.excluir("ori-1") } returns Response.success(null)
        coEvery { api.listar(apenasAtivas = false) } returns emptyList()
        val repository = OrientacaoRepositoryImpl(api, networkMonitor)

        repository.excluir("ori-1")

        coVerify { api.excluir("ori-1") }
    }

    @Test
    fun `excluir propaga erro HTTP`() = runTest {
        every { networkMonitor.isOnline() } returns true
        coEvery { api.excluir("ori-1") } returns Response.error(404, okhttp3.ResponseBody.create(null, ""))
        val repository = OrientacaoRepositoryImpl(api, networkMonitor)

        try {
            repository.excluir("ori-1")
            fail("deveria lançar HttpException")
        } catch (_: retrofit2.HttpException) {
            // esperado
        }
    }

    private fun sampleDto(id: String, ativo: Boolean = true) = OrientacaoDto(
        id = id,
        titulo = "Título",
        descricao = "Desc",
        categoria = "EFICIENCIA_OPERACIONAL",
        campanha = "Águia 2026",
        dataCriacao = "2026-01-15T10:00:00Z",
        ativo = ativo,
        criadoPorUserId = "user-lider"
    )
}
