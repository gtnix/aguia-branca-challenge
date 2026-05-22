package com.gtnix.aguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtnix.aguiabranca.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Usuários
 *
 * ## Conceito FIAP - Material 07A (Room Database)
 *
 * DAO (Data Access Object) é uma **interface** que define as operações
 * de banco de dados. O Room gera a implementação automaticamente!
 *
 * ### Anotações Principais
 *
 * - `@Query`: SQL personalizado
 * - `@Insert`: Inserção (pode configurar conflito)
 * - `@Update`: Atualização
 * - `@Delete`: Remoção
 *
 * ### Flow para Reatividade (Material 10A)
 *
 * Quando o retorno é `Flow<T>`, o Room:
 * 1. Observa a tabela automaticamente
 * 2. Emite nova lista quando dados mudam
 * 3. A UI atualiza automaticamente
 *
 * ```kotlin
 * // No ViewModel
 * val usuarios: StateFlow<List<Usuario>> = usuarioDao
 *     .listarTodos()
 *     .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
 * ```
 *
 * ### suspend para Operações Únicas
 *
 * Funções `suspend` são executadas em coroutines e não bloqueiam a Main Thread.
 */
@Dao
interface UsuarioDao {

    // ========================================================================
    // QUERIES DE LEITURA (Flow para reatividade)
    // ========================================================================

    /**
     * Lista todos os usuários ordenados por nome.
     * Retorna Flow que emite quando houver mudanças.
     */
    @Query("SELECT * FROM usuarios ORDER BY nome ASC")
    fun listarTodos(): Flow<List<UsuarioEntity>>

    /**
     * Lista usuários por perfil.
     */
    @Query("SELECT * FROM usuarios WHERE perfil = :perfil ORDER BY nome ASC")
    fun listarPorPerfil(perfil: String): Flow<List<UsuarioEntity>>

    /**
     * Lista usuários de uma área específica.
     */
    @Query("SELECT * FROM usuarios WHERE area = :area ORDER BY nome ASC")
    fun listarPorArea(area: String): Flow<List<UsuarioEntity>>

    // ========================================================================
    // QUERIES DE BUSCA ÚNICA (suspend)
    // ========================================================================

    /**
     * Busca usuário por ID.
     */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun buscarPorId(id: String): UsuarioEntity?

    /**
     * Busca usuário por e-mail.
     * Usado no login.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun buscarPorEmail(email: String): UsuarioEntity?

    /**
     * Autentica usuário verificando email e senha.
     */
    @Query("SELECT * FROM usuarios WHERE email = :email AND senhaHash = :senhaHash")
    suspend fun autenticar(email: String, senhaHash: String): UsuarioEntity?

    /**
     * Conta total de usuários.
     */
    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    // ========================================================================
    // OPERAÇÕES DE ESCRITA (suspend)
    // ========================================================================

    /**
     * Insere um usuário.
     * OnConflictStrategy.REPLACE: Se já existir, substitui.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UsuarioEntity)

    /**
     * Insere múltiplos usuários.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(usuarios: List<UsuarioEntity>)

    /**
     * Atualiza um usuário existente.
     */
    @Update
    suspend fun atualizar(usuario: UsuarioEntity)

    /**
     * Remove um usuário.
     */
    @Delete
    suspend fun excluir(usuario: UsuarioEntity)

    /**
     * Remove usuário por ID.
     */
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun excluirPorId(id: String)

    /**
     * Remove todos os usuários (útil para testes).
     */
    @Query("DELETE FROM usuarios")
    suspend fun excluirTodos()
}
