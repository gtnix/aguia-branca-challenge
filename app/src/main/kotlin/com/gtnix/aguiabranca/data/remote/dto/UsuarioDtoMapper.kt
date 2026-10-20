package com.gtnix.aguiabranca.data.remote.dto

import com.gtnix.aguiabranca.data.util.safeValueOf
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.DivisaoNegocio
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario

fun UsuarioDto.toDomain(): Usuario = Usuario(
    id = id,
    nome = nome,
    email = email,
    perfil = safeValueOf(perfil, PerfilUsuario.OPERADOR),
    area = safeValueOf(area.orEmpty(), AreaAtuacao.OPERACOES),
    divisao = safeValueOf(divisao.orEmpty(), DivisaoNegocio.LOGISTICA)
)
