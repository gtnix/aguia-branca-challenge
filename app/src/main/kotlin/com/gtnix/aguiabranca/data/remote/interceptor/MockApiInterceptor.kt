package com.gtnix.aguiabranca.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockApiInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        if (path.endsWith("/startups/recomendadas")) {
            return Response.Builder()
                .code(200)
                .message("OK")
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .body(MOCK_STARTUPS_JSON.toResponseBody(JSON_MEDIA_TYPE))
                .build()
        }

        return chain.proceed(request)
    }

    private companion object {
        val JSON_MEDIA_TYPE = "application/json".toMediaType()

        val MOCK_STARTUPS_JSON = """
            [
              {
                "id": "1",
                "nome": "LogTech Solutions",
                "setor": "Logística Inteligente",
                "descricao": "Plataforma de otimização de rotas com IA que reduz emissões de CO₂ em até 30% através de algoritmos de roteirização sustentável para frotas de transporte rodoviário.",
                "matchScore": 92
              },
              {
                "id": "2",
                "nome": "GreenRoute",
                "setor": "Mobilidade Sustentável",
                "descricao": "Sistema de monitoramento em tempo real de pegada de carbono por viagem, com dashboards ESG integrados e relatórios automáticos para compliance ambiental.",
                "matchScore": 87
              },
              {
                "id": "3",
                "nome": "FleetAI",
                "setor": "Gestão de Frotas",
                "descricao": "Solução de manutenção preditiva para frotas utilizando sensores IoT e machine learning, reduzindo custos operacionais e aumentando a vida útil dos veículos.",
                "matchScore": 78
              }
            ]
        """.trimIndent()
    }
}
