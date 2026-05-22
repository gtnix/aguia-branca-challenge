package com.gtnix.aguiabranca.di

import com.gtnix.aguiabranca.data.repository.IdeiaRepositoryImpl
import com.gtnix.aguiabranca.data.repository.OrientacaoRepositoryImpl
import com.gtnix.aguiabranca.data.repository.ProjetoRepositoryImpl
import com.gtnix.aguiabranca.data.repository.UsuarioRepositoryImpl
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para Repositories
 *
 * ## Conceito FIAP - Dependency Injection
 *
 * Este módulo faz o "bind" entre interfaces e implementações.
 *
 * ### @Binds vs @Provides
 *
 * | @Provides                          | @Binds                              |
 * |------------------------------------|-------------------------------------|
 * | Método normal                      | Método abstrato                     |
 * | Pode ter lógica de criação         | Apenas mapeia interface → impl      |
 * | Retorna instância                  | Retorna a própria implementação     |
 *
 * Usamos @Binds porque:
 * 1. É mais eficiente (não gera código extra)
 * 2. As implementações já têm @Inject constructor
 *
 * ### Fluxo
 *
 * ```kotlin
 * // No ViewModel
 * class LoginViewModel @Inject constructor(
 *     private val repository: UsuarioRepository  // Interface!
 * )
 *
 * // Hilt verifica: "Como crio UsuarioRepository?"
 * // Encontra @Binds → Usa UsuarioRepositoryImpl
 * // UsuarioRepositoryImpl tem @Inject constructor(dao, mapper)
 * // Hilt cria dao e mapper automaticamente
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Quando alguém pedir UsuarioRepository, forneça UsuarioRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(
        impl: UsuarioRepositoryImpl
    ): UsuarioRepository

    /**
     * Quando alguém pedir OrientacaoRepository, forneça OrientacaoRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindOrientacaoRepository(
        impl: OrientacaoRepositoryImpl
    ): OrientacaoRepository

    /**
     * Quando alguém pedir IdeiaRepository, forneça IdeiaRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindIdeiaRepository(
        impl: IdeiaRepositoryImpl
    ): IdeiaRepository

    /**
     * Quando alguém pedir ProjetoRepository, forneça ProjetoRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindProjetoRepository(
        impl: ProjetoRepositoryImpl
    ): ProjetoRepository
}
