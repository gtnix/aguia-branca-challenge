package com.gtnix.aguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "projetos",
    foreignKeys = [
        ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["responsavelId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = OrientacaoEntity::class,
            parentColumns = ["id"],
            childColumns = ["orientacaoId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = IdeiaEntity::class,
            parentColumns = ["id"],
            childColumns = ["ideiaOrigemId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index("responsavelId"),
        Index("area"),
        Index("status"),
        Index("orientacaoId"),
        Index("ideiaOrigemId")
    ]
)
data class ProjetoEntity(
    @PrimaryKey
    val id: String,
    
    val nome: String,
    
    val objetivo: String,
    
    val descricao: String,
    
    val area: String,
    
    val status: String,
    
    val ideiaOrigemId: String?,
    
    val orientacaoId: String?,
    
    val responsavelId: String?,
    
    val responsavelNome: String,
    
    val membrosIds: String,
    
    val dataCriacao: Long,
    
    val dataInicio: Long?,
    
    val dataPrevistaConclusao: Long?,
    
    val dataConclusao: Long?,
    
    val progresso: Int,
    
    val resultados: String?,
    
    val investimentoEstimado: Double = 0.0,
    
    val investimentoRealizado: Double = 0.0,
    
    val retornoEstimadoMensal: Double = 0.0,
    
    val retornoRealizadoMensal: Double = 0.0
)
