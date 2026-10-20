package com.gtnix.aguiabranca.data.remote.interceptor

import com.gtnix.aguiabranca.data.session.SessionEventBus
import com.gtnix.aguiabranca.fake.FakeTokenStore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthInterceptorTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `adiciona Bearer quando ha token`() {
        val tokenStore = FakeTokenStore("jwt-abc")
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .build()
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        client.newCall(Request.Builder().url(server.url("ideias")).build()).execute()

        assertEquals("Bearer jwt-abc", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `nao adiciona Authorization no login`() {
        val tokenStore = FakeTokenStore("jwt-abc")
        val client = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .build()
        server.enqueue(MockResponse().setResponseCode(200).setBody("{}"))

        client.newCall(Request.Builder().url(server.url("auth/login")).build()).execute()

        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun `401 limpa token e emite sessao expirada`() = runTest {
        val tokenStore = FakeTokenStore("jwt-abc")
        val bus = SessionEventBus()
        var expired = false
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            bus.sessionExpired.collect { expired = true }
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(UnauthorizedInterceptor(tokenStore, bus))
            .build()
        server.enqueue(MockResponse().setResponseCode(401).setBody("{\"error\":\"Não autenticado\"}"))

        client.newCall(Request.Builder().url(server.url("auth/me")).build()).execute()

        assertTrue(tokenStore.cleared)
        assertTrue(expired)
        assertNull(tokenStore.getAccessToken())
    }
}
