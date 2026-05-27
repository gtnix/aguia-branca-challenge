package com.gtnix.aguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "orientacoes",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["criadoPor"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("criadoPor"),
        Index("ativa"),
        Index("categoria")
    ]
)
data class OrientacaoEntity(
    @PrimaryKey
    val id: String,
    
    val titulo: String,
    
    val descricao: String,
    
    val categoria: String,
    
    val prioridade: Int,
    
    val ativa: Boolean,
    
    val criadoPor: String?,
    
    val dataCriacao: Long,
    
    val dataExpiracao: Long?
)
