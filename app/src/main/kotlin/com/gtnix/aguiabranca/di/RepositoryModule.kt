package com.gtnix.aguiabranca.di

import com.gtnix.aguiabranca.data.repository.DashboardRepositoryImpl
import com.gtnix.aguiabranca.data.repository.GamificacaoRepositoryImpl
import com.gtnix.aguiabranca.data.repository.IdeiaRepositoryImpl
import com.gtnix.aguiabranca.data.repository.OrientacaoRepositoryImpl
import com.gtnix.aguiabranca.data.repository.ProjetoRepositoryImpl
import com.gtnix.aguiabranca.data.repository.UsuarioRepositoryImpl
import com.gtnix.aguiabranca.data.session.SessionManagerImpl
import com.gtnix.aguiabranca.domain.repository.DashboardRepository
import com.gtnix.aguiabranca.domain.repository.GamificacaoRepository
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(impl: UsuarioRepositoryImpl): UsuarioRepository

    @Binds
    @Singleton
    abstract fun bindOrientacaoRepository(impl: OrientacaoRepositoryImpl): OrientacaoRepository

    @Binds
    @Singleton
    abstract fun bindIdeiaRepository(impl: IdeiaRepositoryImpl): IdeiaRepository

    @Binds
    @Singleton
    abstract fun bindProjetoRepository(impl: ProjetoRepositoryImpl): ProjetoRepository

    @Binds
    @Singleton
    abstract fun bindDashboardRepository(impl: DashboardRepositoryImpl): DashboardRepository

    @Binds
    @Singleton
    abstract fun bindGamificacaoRepository(impl: GamificacaoRepositoryImpl): GamificacaoRepository

    @Binds
    @Singleton
    abstract fun bindSessionManager(impl: SessionManagerImpl): SessionManager
}
