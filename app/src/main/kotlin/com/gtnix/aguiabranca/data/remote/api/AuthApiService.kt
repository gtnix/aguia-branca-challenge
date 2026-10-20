package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.data.remote.dto.LoginRequestDto
import com.gtnix.aguiabranca.data.remote.dto.LoginResponseDto
import com.gtnix.aguiabranca.data.remote.dto.UsuarioDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto

    @GET("auth/me")
    suspend fun me(): UsuarioDto
}
