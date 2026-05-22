package com.gtnix.aguiabranca.di

import android.app.Application
import androidx.room.Room
import com.gtnix.aguiabranca.data.local.dao.IdeiaDao
import com.gtnix.aguiabranca.data.local.dao.OrientacaoDao
import com.gtnix.aguiabranca.data.local.dao.ProjetoDao
import com.gtnix.aguiabranca.data.local.dao.UsuarioDao
import com.gtnix.aguiabranca.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para Database
 *
 * ## Conceito FIAP - Dependency Injection com Hilt
 *
 * Este módulo configura como o banco de dados e os DAOs são criados.
 *
 * ### @Module
 *
 * Indica que esta classe contém configurações de dependências.
 *
 * ### @InstallIn(SingletonComponent::class)
 *
 * As dependências vivem enquanto o app estiver vivo (Singleton).
 *
 * ### @Provides
 *
 * Diz ao Hilt "quando alguém pedir X, use este método para criar".
 *
 * ### @Singleton
 *
 * Garante que só existe UMA instância do objeto.
 *
 * ### Fluxo de Criação
 *
 * ```
 * Alguém pede UsuarioDao
 *     │
 *     ▼
 * Hilt verifica: "Como crio UsuarioDao?"
 *     │
 *     ▼
 * Encontra provideUsuarioDao(db: AppDatabase)
 *     │
 *     ▼
 * "Preciso de AppDatabase" → provideDatabase(app)
 *     │
 *     ▼
 * "Preciso de Application" → O próprio Hilt fornece
 *     │
 *     ▼
 * Cria AppDatabase → Cria UsuarioDao → Retorna
 * ```
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Provê a instância do AppDatabase.
     *
     * @param app Application injetada automaticamente pelo Hilt
     * @return Instância única do banco de dados
     */
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        // Em desenvolvimento, recria o banco se o schema mudar
        // ⚠️ Em produção, use migrations apropriadas!
        .fallbackToDestructiveMigration()
        .build()
    }

    /**
     * Provê o UsuarioDao.
     */
    @Provides
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao {
        return database.usuarioDao()
    }

    /**
     * Provê o OrientacaoDao.
     */
    @Provides
    fun provideOrientacaoDao(database: AppDatabase): OrientacaoDao {
        return database.orientacaoDao()
    }

    /**
     * Provê o IdeiaDao.
     */
    @Provides
    fun provideIdeiaDao(database: AppDatabase): IdeiaDao {
        return database.ideiaDao()
    }

    /**
     * Provê o ProjetoDao.
     */
    @Provides
    fun provideProjetoDao(database: AppDatabase): ProjetoDao {
        return database.projetoDao()
    }
}
