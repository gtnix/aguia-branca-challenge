package com.gtnix.aguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios ORDER BY nome ASC")
    fun listarTodos(): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE perfil = :perfil ORDER BY nome ASC")
    fun listarPorPerfil(perfil: String): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE area = :area ORDER BY nome ASC")
    fun listarPorArea(area: String): Flow<List<UsuarioEntity>>

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios WHERE email = :email AND senhaHash = :senhaHash")
    suspend fun autenticar(email: String, senhaHash: String): UsuarioEntity?

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UsuarioEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(usuarios: List<UsuarioEntity>)

    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun excluir(usuario: UsuarioEntity)

    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun excluirPorId(id: String)

    @Query("DELETE FROM usuarios")
    suspend fun excluirTodos()
}
