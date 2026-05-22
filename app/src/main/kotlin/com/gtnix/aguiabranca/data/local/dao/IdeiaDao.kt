package com.gtnix.aguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Ideias
 *
 * ## Conceito FIAP - Material 07A
 *
 * Este DAO demonstra queries com múltiplos filtros e contagem para dashboard.
 */
@Dao
interface IdeiaDao {

    // ========================================================================
    // QUERIES DE LEITURA
    // ========================================================================

    /**
     * Lista todas as ideias ordenadas por data de criação (mais recentes primeiro).
     */
    @Query("SELECT * FROM ideias ORDER BY dataCriacao DESC")
    fun listarTodas(): Flow<List<IdeiaEntity>>

    /**
     * Lista ideias de um autor específico.
     * Usado em "Minhas Ideias".
     */
    @Query("SELECT * FROM ideias WHERE autorId = :autorId ORDER BY dataCriacao DESC")
    fun listarPorAutor(autorId: String): Flow<List<IdeiaEntity>>

    /**
     * Lista ideias por área.
     * Usado por GESTOR para ver ideias da sua área.
     */
    @Query("SELECT * FROM ideias WHERE area = :area ORDER BY dataCriacao DESC")
    fun listarPorArea(area: String): Flow<List<IdeiaEntity>>

    /**
     * Lista ideias por status.
     * Ex: todas pendentes de avaliação.
     */
    @Query("SELECT * FROM ideias WHERE status = :status ORDER BY dataCriacao DESC")
    fun listarPorStatus(status: String): Flow<List<IdeiaEntity>>

    /**
     * Lista ideias alinhadas a uma orientação estratégica.
     */
    @Query("SELECT * FROM ideias WHERE orientacaoId = :orientacaoId ORDER BY dataCriacao DESC")
    fun listarPorOrientacao(orientacaoId: String): Flow<List<IdeiaEntity>>

    /**
     * Lista ideias pendentes de uma área (para avaliação).
     */
    @Query("""
        SELECT * FROM ideias 
        WHERE area = :area AND status = 'PENDENTE'
        ORDER BY dataCriacao ASC
    """)
    fun listarPendentesArea(area: String): Flow<List<IdeiaEntity>>

    // ========================================================================
    // QUERIES DE BUSCA E CONTAGEM
    // ========================================================================

    @Query("SELECT * FROM ideias WHERE id = :id")
    suspend fun buscarPorId(id: String): IdeiaEntity?

    @Query("SELECT COUNT(*) FROM ideias")
    suspend fun contarTotal(): Int

    @Query("SELECT COUNT(*) FROM ideias WHERE status = :status")
    suspend fun contarPorStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM ideias WHERE autorId = :autorId")
    suspend fun contarPorAutor(autorId: String): Int

    // ========================================================================
    // OPERAÇÕES DE ESCRITA
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(ideia: IdeiaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodas(ideias: List<IdeiaEntity>)

    @Update
    suspend fun atualizar(ideia: IdeiaEntity)

    /**
     * Atualiza status e feedback de uma ideia.
     */
    @Query("""
        UPDATE ideias 
        SET status = :novoStatus, 
            feedback = :feedback,
            dataAvaliacao = :dataAvaliacao
        WHERE id = :id
    """)
    suspend fun atualizarStatus(
        id: String,
        novoStatus: String,
        feedback: String?,
        dataAvaliacao: Long
    )

    /**
     * Vincula ideia a um projeto.
     */
    @Query("""
        UPDATE ideias 
        SET projetoId = :projetoId, 
            status = 'CONVERTIDA_PROJETO'
        WHERE id = :ideiaId
    """)
    suspend fun vincularProjeto(ideiaId: String, projetoId: String)

    @Delete
    suspend fun excluir(ideia: IdeiaEntity)

    @Query("DELETE FROM ideias WHERE id = :id")
    suspend fun excluirPorId(id: String)

    @Query("DELETE FROM ideias")
    suspend fun excluirTodas()
}
