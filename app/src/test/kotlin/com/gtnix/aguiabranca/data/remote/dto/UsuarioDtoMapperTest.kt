package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioDtoMapperTest {

    @Test
    fun `mapeia contrato da API para dominio`() {
        val dto = UsuarioDto(
            id = "user-gestor",
            nome = "Ana Oliveira",
            email = "ana.oliveira@aguiabranca.com.br",
            perfil = "GESTOR",
            area = "OPERACOES",
            divisao = "LOGISTICA"
        )

        val usuario = dto.toDomain()

        assertEquals("user-gestor", usuario.id)
        assertEquals(PerfilUsuario.GESTOR, usuario.perfil)
        assertEquals(AreaAtuacao.OPERACOES, usuario.area)
        assertEquals(DivisaoNegocio.LOGISTICA, usuario.divisao)
    }
}
