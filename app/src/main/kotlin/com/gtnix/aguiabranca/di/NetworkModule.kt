package com.gtnix.aguiabranca.di

import com.gtnix.aguiabranca.data.remote.api.InovacaoApiService
import com.gtnix.aguiabranca.data.remote.api.MockInovacaoApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideInovacaoApiService(): InovacaoApiService {
        return MockInovacaoApi()
    }
}
