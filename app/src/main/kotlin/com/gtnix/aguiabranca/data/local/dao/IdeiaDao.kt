package com.gtnix.aguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtnix.aguiabranca.data.local.entity.IdeiaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface IdeiaDao {

    @Query("SELECT * FROM ideias ORDER BY dataCriacao DESC")
    fun listarTodas(): Flow<List<IdeiaEntity>>

    @Query("SELECT * FROM ideias WHERE autorId = :autorId ORDER BY dataCriacao DESC")
    fun listarPorAutor(autorId: String): Flow<List<IdeiaEntity>>

    @Query("SELECT * FROM ideias WHERE area = :area ORDER BY dataCriacao DESC")
    fun listarPorArea(area: String): Flow<List<IdeiaEntity>>

    @Query("SELECT * FROM ideias WHERE status = :status ORDER BY dataCriacao DESC")
    fun listarPorStatus(status: String): Flow<List<IdeiaEntity>>

    @Query("SELECT * FROM ideias WHERE orientacaoId = :orientacaoId ORDER BY dataCriacao DESC")
    fun listarPorOrientacao(orientacaoId: String): Flow<List<IdeiaEntity>>

    @Query("""
        SELECT * FROM ideias 
        WHERE area = :area AND status = 'PENDENTE'
        ORDER BY dataCriacao ASC
    """)
    fun listarPendentesArea(area: String): Flow<List<IdeiaEntity>>

    @Query("SELECT * FROM ideias WHERE id = :id")
    suspend fun buscarPorId(id: String): IdeiaEntity?

    @Query("SELECT COUNT(*) FROM ideias")
    suspend fun contarTotal(): Int

    @Query("SELECT COUNT(*) FROM ideias WHERE status = :status")
    suspend fun contarPorStatus(status: String): Int

    @Query("SELECT COUNT(*) FROM ideias WHERE autorId = :autorId")
    suspend fun contarPorAutor(autorId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(ideia: IdeiaEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodas(ideias: List<IdeiaEntity>)

    @Update
    suspend fun atualizar(ideia: IdeiaEntity)

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

    @Query("""
        UPDATE ideias 
        SET projetoId = :projetoId, 
            status = 'CONVERTIDA_PROJETO'
        WHERE id = :ideiaId
    """)
    suspend fun vincularProjeto(ideiaId: String, projetoId: String)

    @Delete
    suspend fun excluir(ideia: IdeiaEntity)

    @Query("UPDATE ideias SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun incrementUpvotes(id: String)

    @Query("DELETE FROM ideias WHERE id = :id")
    suspend fun excluirPorId(id: String)

    @Query("DELETE FROM ideias")
    suspend fun excluirTodas()
}
