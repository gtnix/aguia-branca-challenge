package com.gtnix.aguiabranca.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gtnix.aguiabranca.data.local.entity.ProjetoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de Projetos
 *
 * ## Conceito FIAP - Material 07A
 *
 * Este DAO inclui queries para dashboard com agregações.
 */
@Dao
interface ProjetoDao {

    // ========================================================================
    // QUERIES DE LEITURA
    // ========================================================================

    /**
     * Lista todos os projetos ordenados por status e data.
     */
    @Query("""
        SELECT * FROM projetos 
        ORDER BY 
            CASE status 
                WHEN 'EM_ANDAMENTO' THEN 1
                WHEN 'PLANEJADO' THEN 2
                WHEN 'PAUSADO' THEN 3
                WHEN 'CONCLUIDO' THEN 4
                WHEN 'CANCELADO' THEN 5
            END,
            dataCriacao DESC
    """)
    fun listarTodos(): Flow<List<ProjetoEntity>>

    /**
     * Lista projetos por área.
     */
    @Query("SELECT * FROM projetos WHERE area = :area ORDER BY dataCriacao DESC")
    fun listarPorArea(area: String): Flow<List<ProjetoEntity>>

    /**
     * Lista projetos por status.
     */
    @Query("SELECT * FROM projetos WHERE status = :status ORDER BY dataCriacao DESC")
    fun listarPorStatus(status: String): Flow<List<ProjetoEntity>>

    /**
     * Lista projetos de um responsável.
     */
    @Query("SELECT * FROM projetos WHERE responsavelId = :responsavelId ORDER BY dataCriacao DESC")
    fun listarPorResponsavel(responsavelId: String): Flow<List<ProjetoEntity>>

    /**
     * Lista projetos onde o usuário é membro.
     * Usa LIKE para buscar no JSON de membrosIds.
     */
    @Query("""
        SELECT * FROM projetos 
        WHERE responsavelId = :usuarioId OR membrosIds LIKE '%' || :usuarioId || '%'
        ORDER BY dataCriacao DESC
    """)
    fun listarPorUsuario(usuarioId: String): Flow<List<ProjetoEntity>>

    /**
     * Lista projetos alinhados a uma orientação.
     */
    @Query("SELECT * FROM projetos WHERE orientacaoId = :orientacaoId ORDER BY dataCriacao DESC")
    fun listarPorOrientacao(orientacaoId: String): Flow<List<ProjetoEntity>>

    // ========================================================================
    // QUERIES DE BUSCA E CONTAGEM
    // ========================================================================

    @Query("SELECT * FROM projetos WHERE id = :id")
    suspend fun buscarPorId(id: String): ProjetoEntity?

    @Query("SELECT COUNT(*) FROM projetos")
    suspend fun contarTotal(): Int

    @Query("SELECT COUNT(*) FROM projetos WHERE status = :status")
    suspend fun contarPorStatus(status: String): Int

    /**
     * Calcula média de progresso dos projetos em andamento.
     */
    @Query("SELECT AVG(progresso) FROM projetos WHERE status = 'EM_ANDAMENTO'")
    suspend fun mediaProgressoEmAndamento(): Float?

    // ========================================================================
    // OPERAÇÕES DE ESCRITA
    // ========================================================================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(projeto: ProjetoEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirTodos(projetos: List<ProjetoEntity>)

    @Update
    suspend fun atualizar(projeto: ProjetoEntity)

    /**
     * Atualiza status do projeto.
     */
    @Query("UPDATE projetos SET status = :novoStatus WHERE id = :id")
    suspend fun atualizarStatus(id: String, novoStatus: String)

    /**
     * Atualiza progresso do projeto.
     */
    @Query("UPDATE projetos SET progresso = :progresso WHERE id = :id")
    suspend fun atualizarProgresso(id: String, progresso: Int)

    /**
     * Conclui um projeto.
     */
    @Query("""
        UPDATE projetos 
        SET status = 'CONCLUIDO', 
            progresso = 100,
            dataConclusao = :dataConclusao,
            resultados = :resultados
        WHERE id = :id
    """)
    suspend fun concluir(id: String, dataConclusao: Long, resultados: String?)

    /**
     * Atualiza lista de membros.
     */
    @Query("UPDATE projetos SET membrosIds = :membrosIds WHERE id = :id")
    suspend fun atualizarMembros(id: String, membrosIds: String)

    @Delete
    suspend fun excluir(projeto: ProjetoEntity)

    @Query("DELETE FROM projetos WHERE id = :id")
    suspend fun excluirPorId(id: String)

    @Query("DELETE FROM projetos")
    suspend fun excluirTodos()
}
