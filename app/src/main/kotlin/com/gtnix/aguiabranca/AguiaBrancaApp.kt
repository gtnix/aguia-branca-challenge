package com.gtnix.aguiabranca

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application do Aguia Branca Challenge.
 *
 * ## Conceito FIAP - Dependency Injection com Hilt
 *
 * A anotação @HiltAndroidApp faz com que o Hilt gere automaticamente
 * toda a infraestrutura de injeção de dependências.
 *
 * ### Por que usar Hilt?
 *
 * 1. **Desacoplamento**: ViewModel não precisa saber como criar um Repository
 * 2. **Testabilidade**: Podemos substituir dependências por mocks em testes
 * 3. **Manutenção**: Mudanças na criação de objetos ficam centralizadas
 *
 * ### Como funciona?
 *
 * O Hilt analisa as anotações em tempo de compilação e gera código que:
 * - Cria instâncias dos objetos marcados com @Inject
 * - Gerencia o ciclo de vida (Singleton, ViewModelScoped, etc.)
 * - Conecta automaticamente dependências em cascata
 *
 * @see [di.DatabaseModule] para configuração do Room
 * @see [di.RepositoryModule] para configuração dos Repositories
 */
@HiltAndroidApp
class AguiaBrancaApp : Application()
