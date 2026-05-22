package com.gtnix.aguiabranca.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity Room: Projeto
 *
 * ## Conceito FIAP - Material 07A
 *
 * ### Armazenando Listas
 *
 * O Room não suporta listas diretamente. Para `membrosIds` temos opções:
 *
 * 1. **TypeConverter**: Converte List<String> para String JSON
 * 2. **Tabela de junção**: Cria tabela projeto_membros (mais correto)
 *
 * Optamos por TypeConverter para simplificar o Sprint 1.
 * Em produção, recomenda-se tabela de junção para integridade.
 */
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
    
    /** Lista de IDs serializada como JSON */
    val membrosIds: String,
    
    val dataCriacao: Long,
    
    val dataInicio: Long?,
    
    val dataPrevistaConclusao: Long?,
    
    val dataConclusao: Long?,
    
    val progresso: Int,
    
    val resultados: String?
)
