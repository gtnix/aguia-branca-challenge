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

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao = database.usuarioDao()

    @Provides
    fun provideOrientacaoDao(database: AppDatabase): OrientacaoDao = database.orientacaoDao()

    @Provides
    fun provideIdeiaDao(database: AppDatabase): IdeiaDao = database.ideiaDao()

    @Provides
    fun provideProjetoDao(database: AppDatabase): ProjetoDao = database.projetoDao()
}
