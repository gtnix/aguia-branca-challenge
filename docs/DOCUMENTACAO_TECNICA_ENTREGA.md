# Documentação Técnica — Challenge Grupo Águia Branca

**Plataforma de Inovação Corporativa Inovagab**

| Campo | Valor |
|-------|-------|
| Challenge FIAP | 612289 |
| Grupo | 82 |
| Disciplina | Frameworks Java — Fase 3 |
| Data | Maio/2026 |

**Autores:** André Luiz Oliveira da Silva (RM 565836) · Giuliana Abe Takara (RM 562736)

---

## 1. Identificação da entrega

| Item | Detalhe |
|------|---------|
| **Atividade** | Challenge — Grupo Águia Branca (FIAP Assign ID 612289) |
| **Disciplina** | Frameworks Java — Fase 3, Análise e Desenvolvimento de Sistemas (On-Line) |
| **Grupo** | 82 |
| **Integrantes** | André Luiz Oliveira da Silva (RM 565836) · Giuliana Abe Takara (RM 562736) |
| **Cliente / contexto** | Grupo Águia Branca — conglomerado de transporte e logística |
| **Solução entregue** | **Inovagab** — aplicativo Android nativo para gestão integrada de inovação corporativa |
| **Sprint** | Sprint 1 — entrega 26/05/2026 |
| **Repositório** | `gtnix/aguia-branca-challenge` |

Este documento descreve a implementação técnica do aplicativo móvel desenvolvido, atendendo ao entregável oficial de **Documentação Técnica** (PDF ou PPT) com tecnologias utilizadas, arquitetura da solução e fluxo da aplicação.

---

## 2. Visão geral e problema de negócio

O Grupo Águia Branca (+20.000 colaboradores, 25+ empresas) busca ampliar a integração entre **estratégia, execução e mensuração** de inovação. O desafio não é apenas um app de ideias, mas um sistema que conecta direcionamento da liderança à execução operacional e demonstra impacto mensurável (ROI, redução de custos, produtividade).

### Os cinco pilares da solução Inovagab

| Pilar | Descrição | Módulo no app |
|-------|-----------|---------------|
| **Direcionamento** | Alinhar iniciativas aos objetivos estratégicos | Orientações Estratégicas |
| **Gestão de Ideias** | Capturar dores e sugestões de todos os níveis | Ideias / Problemas |
| **Inovação Aberta** | Conectar com ecossistema externo (startups) | Radar de Inovação |
| **Gestão de Projetos** | Estruturar ideias validadas em iniciativas reais | Projetos |
| **Mensuração** | Acompanhar indicadores e ROI | Dashboard Executivo |

### Três pilares funcionais (jornada)

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   CAPTURAR  │ ───► │  ESTRUTURAR │ ───► │ ACOMPANHAR  │
│ Dores e     │      │ Transformar │      │ Monitorar   │
│ oportunida- │      │ em inicia-  │      │ até resul-  │
│ des do dia  │      │ tivas ali-  │      │ tados men-  │
│ a dia       │      │ nhadas      │      │ suráveis    │
└─────────────┘      └─────────────┘      └─────────────┘
```

A implementação Sprint 1 entrega um app Android funcional com autenticação por perfil, persistência local (Room), integração REST simulada (Radar) e gamificação como diferencial de engajamento.

---

## 3. Tecnologias utilizadas

| Camada | Tecnologia | Versão / Propósito |
|--------|------------|-------------------|
| **Plataforma** | Android | minSdk 26, targetSdk 35, compileSdk 35 |
| **Linguagem** | Kotlin | 2.0.0 — JVM 17 |
| **UI** | Jetpack Compose + Material 3 | BOM 2024.12.01 — interface declarativa |
| **Arquitetura** | Clean Architecture + MVVM | Separação domain / data / presentation |
| **Injeção de dependência** | Hilt (Dagger) | 2.51.1 — módulos Singleton |
| **Persistência local** | Room | 2.6.1 — SQLite com DAOs e Flow |
| **Sessão** | DataStore Preferences | 1.1.1 — sessão do usuário logado |
| **Rede** | Retrofit + OkHttp + Gson | 2.11.0 / 4.12.0 — cliente REST |
| **Assincronismo** | Kotlin Coroutines + Flow | 1.8.1 — operações off-main-thread |
| **Navegação** | Navigation Compose | 2.7.7 — rotas type-safe |
| **Build** | Gradle KTS + Version Catalog | AGP 8.5.0 — `libs.versions.toml` |
| **Processamento de anotações** | KSP | 2.0.0-1.0.21 — Room, Hilt |
| **Testes** | JUnit 4, MockK, Turbine | UseCases e ViewModels |
| **IDE** | Android Studio Hedgehog+ | JDK 17 |

---

## 4. Arquitetura da solução

### 4.1 Padrão Clean Architecture + MVVM

O projeto organiza responsabilidades em três camadas, com fluxo unidirecional de dados (UDF): a UI emite eventos, o ViewModel processa via Use Cases, os repositórios abstraem fontes de dados e o estado reativo retorna à UI via `StateFlow`.

```
┌─────────────────────────────────────────────────────────────┐
│              PRESENTATION (Compose + ViewModel)             │
│   Screens · Navigation · Theme · StateFlow / UI State       │
├─────────────────────────────────────────────────────────────┤
│                    DOMAIN (núcleo de negócio)                │
│   Models · Repository interfaces · Use Cases · Session      │
├─────────────────────────────────────────────────────────────┤
│                         DATA                                │
│   Room DAOs · Retrofit API · Mappers · Repository Impl      │
└─────────────────────────────────────────────────────────────┘
         ▲                              │
         │         Hilt injeta          │
         └──────────────────────────────┘
```

**Princípios aplicados:** inversão de dependência (domain define ports), single source of truth (Room como cache local), MVVM (ViewModel expõe estado imutável).

### 4.2 Camadas detalhadas

| Camada | Pacote base | Responsabilidade |
|--------|-------------|------------------|
| **Presentation** | `presentation/` | Telas Compose, ViewModels, navegação, tema Material 3 |
| **Domain** | `domain/` | Entidades de negócio, contratos de repositório, casos de uso |
| **Data** | `data/` | Room, Retrofit, implementações de repositório, mappers Entity ↔ Domain |
| **DI** | `di/` | `DatabaseModule`, `NetworkModule`, `RepositoryModule` |

### 4.3 Persistência local (Room)

Banco SQLite versionado (schema export em `app/schemas/`). Entidades principais: `UsuarioEntity`, `OrientacaoEntity`, `IdeiaEntity`, `ProjetoEntity`. DAOs expõem queries reativas com `Flow`. `DatabaseSeeder` popula dados demo na primeira execução. `TypeConverters` serializam listas (ex.: membros do projeto) em JSON.

Relacionamentos simplificados:

```
 usuarios ──┬── ideias ──┬── projetos
             │            │
 orientacoes ┴────────────┘
```

### 4.4 Camada remota (Retrofit + mock Sprint 1)

| Componente | Função |
|------------|--------|
| `InovacaoApiService` | Interface Retrofit com `@GET("startups/recomendadas")` |
| `NetworkModule` | Configura OkHttp, Retrofit (base URL `https://api.inovagab.mock/`) |
| `MockApiInterceptor` | Intercepta chamadas e retorna JSON estático de startups (Sprint 1) |
| `InovacaoAbertaRepositoryImpl` | Orquestra chamada API e mapeia para `StartupPartner` |

Na Sprint 1 não há backend real: o interceptor simula resposta HTTP 200, cumprindo o requisito de **conectividade externa funcional** (API REST mockada). Na Sprint 2 a mesma interface apontará para backend Java/C#.

### 4.5 Injeção de dependência (Hilt)

- `@HiltAndroidApp` em `AguiaBrancaApp`
- `@AndroidEntryPoint` em `MainActivity`
- `@HiltViewModel` nos ViewModels
- Módulos `@InstallIn(SingletonComponent::class)` provêm database, APIs e repositórios

---

## 5. Fluxo da aplicação

### 5.1 Autenticação e três perfis

Login por e-mail/senha com hash SHA-256 (`PasswordHasher`). Sessão persistida via `SessionDataStore`. Após autenticação, navegação e ações são filtradas por `PerfilUsuario`.

| Perfil | Enum | Permissões principais | Telas / ações |
|--------|------|----------------------|---------------|
| **Operador** | `OPERADOR` | Cadastrar ideias, consultar orientações (leitura), acompanhar próprias ideias, ranking | Home, Ideias, Orientações, Perfil |
| **Gestor** | `GESTOR` | Avaliar/priorizar/aprovar ideias, CRUD projetos, atualizar progresso, Radar | + Projetos, Radar, avaliação em IdeiaDetalhe |
| **Líder** | `LIDER` | CRUD orientações, dashboard executivo, visão de todos os projetos, Radar | + Orientações (CRUD), LeaderDashboard |

Matriz resumida de permissões:

| Ação | Operador | Gestor | Líder |
|------|:--------:|:------:|:-----:|
| Submeter ideia | ✅ | ✅ | ✅ |
| Avaliar / aprovar ideia | ❌ | ✅ | ✅ |
| Criar / editar projeto | ❌ | ✅ | ✅ |
| CRUD orientações | ❌ | ❌ | ✅ |
| Dashboard executivo completo | ❌ | Parcial | ✅ |
| Radar de startups | ❌ | ✅ | ✅ |

### 5.2 Fluxo do Operador

1. Login → Home com atalhos e gamificação (pontos, nível).
2. Consulta **Orientações Estratégicas** (somente leitura).
3. Cadastra **Ideia** ou **Problema** (texto ou voz via SpeechRecognizer), vincula área e orientação.
4. Acompanha status: `PENDENTE` → `EM_ANALISE` → `APROVADA` / `REPROVADA` / `CONVERTIDA_PROJETO`.
5. Visualiza ranking e conquistas no perfil.

### 5.3 Fluxo do Gestor

1. Login → Home com contadores de ideias pendentes por área.
2. Lista todas as ideias; filtra por status; prioriza via impacto/esforço (score = impacto − esforço).
3. Em **IdeiaDetalhe**: aprova (com feedback), reprova ou inicia conversão.
4. Cria **Projeto** manualmente ou a partir de ideia aprovada.
5. Atualiza progresso (0–100%), investimento, retorno e resultados.
6. Consulta **Radar de Inovação** (startups com match score).

### 5.4 Fluxo do Líder

1. Login → Home com acesso ao **Dashboard Executivo**.
2. **CRUD completo** de Orientações Estratégicas (criar, editar, excluir, ativar/desativar).
3. Consulta andamento de **todos** os projetos (status, investimento, prazo, ROI).
4. Dashboard consolidado: ROI total, funil de inovação, métricas por projeto.
5. Radar de startups para decisões de inovação aberta.

### 5.5 Radar de Inovação (inovação aberta)

Tela `RadarScreen` consome `InovacaoApiService.getStartupsRecomendadas()`. O repositório retorna lista de `StartupPartner` (nome, setor, descrição, `matchScore` 0–100). Disponível para Gestor e Líder. Dados mockados alinhados ao setor de transporte/logística (LogTech, GreenRoute, FleetAI).

### 5.6 Funil ideia → projeto → resultado

```
  [Operador cadastra ideia]
            │
            ▼
       ┌─────────┐
       │PENDENTE │
       └────┬────┘
            │ Gestor analisa
            ▼
     ┌────────────┐     reprovar    ┌───────────┐
     │ EM_ANALISE ├────────────────►│ REPROVADA │
     └─────┬──────┘                 └───────────┘
           │ aprovar
           ▼
      ┌──────────┐    converter     ┌──────────────────┐
      │ APROVADA ├─────────────────►│CONVERTIDA_PROJETO│
      └──────────┘                   └────────┬─────────┘
                                              │
                                              ▼
                                    ┌─────────────────┐
                                    │ Projeto criado  │
                                    │ PLANEJADO →     │
                                    │ EM_ANDAMENTO →  │
                                    │ CONCLUIDO       │
                                    └────────┬────────┘
                                             │
                                             ▼
                                    ┌─────────────────┐
                                    │ Dashboard Líder │
                                    │ ROI · retorno   │
                                    └─────────────────┘
```

---

## 6. Integração externa (requisito obrigatório)

O Challenge exige **pelo menos uma** integração externa efetiva (API REST, BaaS ou banco em nuvem). A solução implementa **consumo de API REST** via Retrofit para o módulo Radar de Inovação.

| Aspecto | Detalhe |
|---------|---------|
| **Endpoint** | `GET /startups/recomendadas` |
| **Base URL (Sprint 1)** | `https://api.inovagab.mock/` |
| **Stack** | Retrofit 2.11 + OkHttp 4.12 + Gson |
| **Modo Sprint 1** | `MockApiInterceptor` retorna JSON local (sem servidor) |
| **Modo Sprint 2** | Mesma interface; interceptor removido; backend real |

Sequência de chamada (Sprint 1):

```
RadarScreen → RadarViewModel → GetStartupsUseCase
      → InovacaoAbertaRepositoryImpl → InovacaoApiService (Retrofit)
            → OkHttp → MockApiInterceptor → JSON mock → UI (lista + matchScore)
```

A integração é **funcional de ponta a ponta**: a tela exibe dados obtidos pela stack de rede, não hardcoded na UI. Logs HTTP habilitados em `DEBUG` via `HttpLoggingInterceptor`.

---

## 7. Modelo de dados (resumo)

| Entidade | Tabela Room | Campos principais |
|----------|-------------|-------------------|
| **Usuario** | `usuarios` | id, nome, email, senhaHash, perfil, area, divisao |
| **OrientacaoEstrategica** | `orientacoes` | titulo, descricao, categoria, prioridade, ativa |
| **Ideia** | `ideias` | titulo, tipo (IDEIA/PROBLEMA), status, autorId, orientacaoId, impacto/esforço |
| **Projeto** | `projetos` | nome, status, progresso, investimento, retorno, ideiaOrigemId, ROI calculado |
| **StartupPartner** | *(remoto)* | id, nome, setor, descricao, matchScore |

Enums de negócio: `PerfilUsuario`, `StatusIdeia`, `StatusProjeto`, `CategoriaOrientacao`, `AreaAtuacao`, `TipoIdeia`.

---

## 8. Como executar o projeto

### Pré-requisitos

- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 35

### Build

```bash
git clone https://github.com/gtnix/aguia-branca-challenge.git
cd aguia-branca-challenge
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Usuários de demonstração (DatabaseSeeder)

| E-mail | Senha | Perfil | Nome |
|--------|-------|--------|------|
| `pedro.santos@aguiabranca.com.br` | `123456` | Operador | Pedro Santos |
| `ana.oliveira@aguiabranca.com.br` | `123456` | Gestor | Ana Oliveira |
| `marcos.silva@aguiabranca.com.br` | `123456` | Líder | Marcos Silva |

> Os dados são inseridos automaticamente na primeira execução se o banco estiver vazio.

---

## 9. Entregáveis do Challenge (Sprint 1)

| # | Entregável | Formato | Status |
|---|------------|---------|--------|
| 1 | Aplicativo móvel | APK Android | Em finalização |
| 2 | Código-fonte | `.zip` do repositório | Repositório GitHub |
| 3 | **Documentação técnica** | **PDF** (este documento) | ✅ |
| 4 | Vídeo demonstrativo | Máx. 5 minutos | Pendente |

Critérios de avaliação FIAP: adequação (20%), implementação (30%), qualidade de código (25%), documentação/apresentação (15%), criatividade (10%).

---

## 10. Evolução prevista (Sprint 2)

| Item | Sprint 1 (atual) | Sprint 2 (2º semestre) |
|------|------------------|------------------------|
| Backend | Mock interceptor | Java ou C# com APIs REST completas |
| Autenticação | Local (Room + hash) | JWT / OAuth |
| Radar / Inovação Aberta | JSON mockado | API real de parceiros |
| Notificações | — | Push (Firebase / OneSignal) |
| Observabilidade | Logs debug | Auditoria, métricas, tracing |
| IA | Captura por voz | Clustering de ideias, insights automáticos |

Funcionalidades extras Sprint 1 já implementadas: gamificação (pontuação, níveis, conquistas, ranking), captura de ideias por voz, dashboard executivo com funil de inovação.

---

## Autores

| Nome | RM | Papel na entrega |
|------|-----|------------------|
| **André Luiz Oliveira da Silva** | 565836 | Documentação técnica, desenvolvimento |
| **Giuliana Abe Takara** | 562736 | Desenvolvimento, coordenação de entrega |

**Grupo 82** — Challenge FIAP 612289 — Grupo Águia Branca / Inovagab

*Documento gerado em Maio/2026 — Sprint 1*
