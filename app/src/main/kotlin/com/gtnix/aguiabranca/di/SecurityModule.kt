package com.gtnix.aguiabranca.di

import com.gtnix.aguiabranca.data.session.EncryptedTokenStore
import com.gtnix.aguiabranca.data.session.TokenStore
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindTokenStore(impl: EncryptedTokenStore): TokenStore
}
