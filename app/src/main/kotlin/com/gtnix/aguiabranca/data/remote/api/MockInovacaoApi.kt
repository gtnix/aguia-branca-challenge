package com.gtnix.aguiabranca.data.remote.api

import com.gtnix.aguiabranca.domain.model.StartupPartner
import kotlinx.coroutines.delay

class MockInovacaoApi : InovacaoApiService {

    override suspend fun getStartupsRecomendadas(): List<StartupPartner> {
        delay(1500)

        return listOf(
            StartupPartner(
                id = "startup-1",
                nome = "LogTech Solutions",
                setor = "Logística Inteligente",
                descricao = "Plataforma de otimização de rotas com IA que reduz emissões de CO₂ em até 30% através de algoritmos de roteirização sustentável para frotas de transporte rodoviário.",
                matchScore = 92
            ),
            StartupPartner(
                id = "startup-2",
                nome = "GreenRoute",
                setor = "Mobilidade Sustentável",
                descricao = "Sistema de monitoramento em tempo real de pegada de carbono por viagem, com dashboards ESG integrados e relatórios automáticos para compliance ambiental.",
                matchScore = 87
            ),
            StartupPartner(
                id = "startup-3",
                nome = "FleetAI",
                setor = "Gestão de Frotas",
                descricao = "Solução de manutenção preditiva para frotas utilizando sensores IoT e machine learning, reduzindo custos operacionais e aumentando a vida útil dos veículos.",
                matchScore = 78
            )
        )
    }
}
