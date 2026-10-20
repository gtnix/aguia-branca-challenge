package com.gtnix.aguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: String,
    
    val nome: String,
    
    val email: String,
    
    /** Senha hash - NUNCA armazenar senha em texto puro! */
    val senhaHash: String,
    
    val perfil: String,
    val area: String,
    val divisao: String = "LOGISTICA",
    
    val fotoPerfil: String?,
    
    val dataCadastro: Long
)
