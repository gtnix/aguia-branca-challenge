package com.gtnix.aguiabranca.domain.repository

import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Usuario
import kotlinx.coroutines.flow.Flow

/**
 * Interface do Repositório de Usuários.
 *
 * ## Conceito FIAP - Repository Pattern (Material 10A)
 *
 * Esta é uma **interface** que define o **contrato** do repositório.
 * A implementação concreta fica na camada `data/`.
 *
 * ### Por que usar interface?
 *
 * 1. **Desacoplamento**: Domain não conhece Room ou API
 * 2. **Testabilidade**: Podemos criar um FakeRepository para testes
 * 3. **Flexibilidade**: Podemos trocar a implementação sem mudar o domínio
 *
 * ### Tipos de Retorno
 *
 * - `suspend fun`: Operação assíncrona que retorna uma vez
 * - `Flow<T>`: Fluxo reativo que emite quando dados mudam
 *
 * ### Exemplo de Uso
 *
 * ```kotlin
 * // No ViewModel
 * class LoginViewModel(
 *     private val repository: UsuarioRepository  // Interface!
 * ) {
 *     fun login(email: String, senha: String) {
 *         viewModelScope.launch {
 *             val usuario = repository.buscarPorEmail(email)
 *             // ...
 *         }
 *     }
 * }
 * ```
 */
interface UsuarioRepository {

    /**
     * Busca um usuário pelo e-mail.
     * Usado no login para autenticação.
     */
    suspend fun buscarPorEmail(email: String): Usuario?

    /**
     * Busca um usuário pelo ID.
     */
    suspend fun buscarPorId(id: String): Usuario?

    /**
     * Retorna um Flow de todos os usuários.
     * O Flow emite uma nova lista sempre que houver mudanças.
     */
    fun listarTodos(): Flow<List<Usuario>>

    /**
     * Lista usuários por perfil.
     * Ex: listar todos os gestores.
     */
    fun listarPorPerfil(perfil: PerfilUsuario): Flow<List<Usuario>>

    /**
     * Lista usuários de uma área específica.
     */
    fun listarPorArea(area: AreaAtuacao): Flow<List<Usuario>>

    /**
     * Insere ou atualiza um usuário.
     */
    suspend fun salvar(usuario: Usuario)

    /**
     * Remove um usuário.
     */
    suspend fun excluir(id: String)

    /**
     * Verifica credenciais de login.
     * Retorna o usuário se credenciais válidas, null caso contrário.
     */
    suspend fun autenticar(email: String, senha: String): Usuario?
}
