package com.gtnix.aguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity Room: Ideia
 *
 * ## Conceito FIAP - Material 07A
 *
 * ### Relacionamentos
 *
 * ```
 * ┌─────────┐       ┌────────────┐       ┌───────────┐
 * │ Usuario ├───────┤   Ideia    ├───────┤ Orientacao│
 * └─────────┘       └──────┬─────┘       └───────────┘
 *    autor              │
 *                       │ se aprovada
 *                       ▼
 *                  ┌─────────┐
 *                  │ Projeto │
 *                  └─────────┘
 * ```
 *
 * ### Índices para Performance
 *
 * Criamos índices nas colunas mais usadas em filtros:
 * - autorId: "Minhas Ideias"
 * - area: Ideias por área (visão GESTOR)
 * - status: Ideias pendentes de avaliação
 */
@Entity(
    tableName = "ideias",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["autorId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = OrientacaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["orientacaoId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = ProjetoEntity::class,
            parentColumns = ["id"],
            childColumns = ["projetoId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("autorId"),
        Index("area"),
        Index("status"),
        Index("orientacaoId"),
        Index("projetoId")
    ]
)
data class IdeiaEntity(
    @PrimaryKey
    val id: String,
    
    val titulo: String,
    
    val descricao: String,
    
    val tipo: String,
    
    val area: String,
    
    val status: String,
    
    val autorId: String,
    
    val autorNome: String,
    
    val orientacaoId: String?,
    
    val feedback: String?,
    
    val projetoId: String?,
    
    val dataCriacao: Long,
    
    val dataAvaliacao: Long?,
    
    val impactoEstimado: Int = 0,
    
    val esforcoEstimado: Int = 0,
    
    val upvotes: Int = 0,
    
    val temSimilaridade: Boolean = false
)
