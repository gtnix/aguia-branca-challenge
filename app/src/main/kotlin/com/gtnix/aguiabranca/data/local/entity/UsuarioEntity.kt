package com.gtnix.aguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity Room: Usuario
 *
 * ## Conceito FIAP - Material 07A (Room Database)
 *
 * Esta classe representa a **tabela** `usuarios` no banco SQLite.
 * A anotação @Entity transforma a data class em uma tabela.
 *
 * ### Diferença: Entity Room vs Entity Domínio
 *
 * | Aspecto          | Room Entity              | Domain Entity           |
 * |------------------|--------------------------|-------------------------|
 * | Localização      | data/local/entity/       | domain/model/           |
 * | Anotações        | @Entity, @PrimaryKey     | Nenhuma                 |
 * | Tipos complexos  | Strings (convertidos)    | Enums, objetos          |
 * | Propósito        | Persistência             | Regras de negócio       |
 *
 * ### Por que separar?
 *
 * Se amanhã precisarmos mudar de Room para Realm, ou adicionar
 * mais campos no banco, a camada de domínio não é afetada.
 *
 * @see [com.gtnix.aguiabranca.domain.model.Usuario] Entity de domínio
 * @see [com.gtnix.aguiabranca.data.mapper.UsuarioMapper] Conversão
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey
    val id: String,
    
    val nome: String,
    
    val email: String,
    
    /** Senha hash - NUNCA armazenar senha em texto puro! */
    val senhaHash: String,
    
    /** Perfil armazenado como String para simplicidade */
    val perfil: String,
    
    /** Área armazenada como String */
    val area: String,
    
    val fotoPerfil: String?,
    
    val dataCadastro: Long
)
