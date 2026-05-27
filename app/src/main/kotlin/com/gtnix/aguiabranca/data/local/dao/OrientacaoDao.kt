package com.gtnix.aguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtnix.aguiabranca.data.local.entity.OrientacaoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrientacaoDao {

    @Query("""
        SELECT * FROM orientacoes 
        WHERE ativa = 1 
        ORDER BY prioridade ASC, dataCriacao DESC
    """)
    fun listarAtivas(): Flow<List<OrientacaoEntity>>

    @Query("SELECT * FROM orientacoes ORDER BY ativa DESC, prioridade ASC")
    fun listarTodas(): Flow<List<OrientacaoEntity>>

    @Query("""
        SELECT * FROM orientacoes 
        WHERE categoria = :categoria AND ativa = 1
        ORDER BY prioridade ASC
    """)
    fun listarPorCategoria(categoria: String): Flow<List<OrientacaoEntity>>

    @Query("SELECT * FROM orientacoes WHERE criadoPor = :liderId ORDER BY dataCriacao DESC")
    fun listarPorCriador(liderId: String): Flow<List<OrientacaoEntity>>

    @Query("SELECT * FROM orientacoes WHERE id = :id")
    suspend fun buscarPorId(id: String): OrientacaoEntity?

    @Query("SELECT COUNT(*) FROM orientacoes WHERE ativa = 1")
    suspend fun contarAtivas(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(orientacao: OrientacaoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodas(orientacoes: List<OrientacaoEntity>)

    @Update
    suspend fun atualizar(orientacao: OrientacaoEntity)

    @Query("UPDATE orientacoes SET ativa = 0 WHERE id = :id")
    suspend fun desativar(id: String)

    @Query("UPDATE orientacoes SET ativa = 1 WHERE id = :id")
    suspend fun ativar(id: String)

    @Delete
    suspend fun excluir(orientacao: OrientacaoEntity)

    @Query("DELETE FROM orientacoes WHERE id = :id")
    suspend fun excluirPorId(id: String)

    @Query("DELETE FROM orientacoes")
    suspend fun excluirTodas()
}
