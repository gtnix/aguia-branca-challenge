package com.gtnix.aguiabranca.di

import com.gtnix.aguiabranca.BuildConfig
import com.gtnix.aguiabranca.data.remote.api.AiApiService
import com.gtnix.aguiabranca.data.remote.api.AuthApiService
import com.gtnix.aguiabranca.data.remote.api.DashboardApiService
import com.gtnix.aguiabranca.data.remote.api.GamificacaoApiService
import com.gtnix.aguiabranca.data.remote.api.IdeiaApiService
import com.gtnix.aguiabranca.data.remote.api.OrientacaoApiService
import com.gtnix.aguiabranca.data.remote.api.ProjetoApiService
import com.gtnix.aguiabranca.data.remote.api.RecompensaApiService
import com.gtnix.aguiabranca.data.remote.interceptor.AuthInterceptor
import com.gtnix.aguiabranca.data.remote.interceptor.UnauthorizedInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(unauthorizedInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrientacaoApiService(retrofit: Retrofit): OrientacaoApiService {
        return retrofit.create(OrientacaoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideIdeiaApiService(retrofit: Retrofit): IdeiaApiService {
        return retrofit.create(IdeiaApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAiApiService(retrofit: Retrofit): AiApiService {
        return retrofit.create(AiApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProjetoApiService(retrofit: Retrofit): ProjetoApiService {
        return retrofit.create(ProjetoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideDashboardApiService(retrofit: Retrofit): DashboardApiService {
        return retrofit.create(DashboardApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGamificacaoApiService(retrofit: Retrofit): GamificacaoApiService {
        return retrofit.create(GamificacaoApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecompensaApiService(retrofit: Retrofit): RecompensaApiService {
        return retrofit.create(RecompensaApiService::class.java)
    }
}
