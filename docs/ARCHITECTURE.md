# Arquitetura Técnica — InovaGAB

**Plataforma de Inovação Corporativa | Challenge FIAP 2026 — Grupo Águia Branca**

Pacote raiz: `com.gtnix.aguiabranca`

---

## 1. Visão Geral da Arquitetura

O aplicativo adota **Clean Architecture** com **MVVM** na camada de apresentação. A regra de dependência é unidirecional: camadas externas dependem de contratos definidos nas camadas internas, nunca o contrário.

```
┌─────────────────────────────────────────────────────────────────────┐
│                     PRESENTATION (UI)                               │
│  Compose Screens · ViewModels · Navigation · Theme · Components   │
└───────────────────────────────┬─────────────────────────────────────┘
                                │ observa StateFlow / Flow
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                       DOMAIN (Negócio)                              │
│  Models · Repository Interfaces · Use Cases · SessionManager        │
└───────────────────────────────┬─────────────────────────────────────┘
                                │ implementado por
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        DATA (Infra)                                 │
│  Room · DAOs · Entities · Mappers · Retrofit · DataStore            │
└─────────────────────────────────────────────────────────────────────┘
```

**Padrão MVVM:** cada tela possui um `ViewModel` (`@HiltViewModel`) que expõe `StateFlow` de UI state. A UI (Compose) coleta estados reativamente via `collectAsStateWithLifecycle()`. Regras de negócio ficam nos **Use Cases**; ViewModels orquestram chamadas e mapeiam resultados para o estado da tela.

**Inversão de dependência:** repositórios são definidos como interfaces em `domain/repository/` e implementados em `data/repository/`. O Hilt faz o binding em `RepositoryModule`.

---

## 2. Stack Tecnológica

| Tecnologia | Versão / Config | Papel no Projeto |
|------------|-----------------|------------------|
| Android SDK | minSdk 26, targetSdk 35, compileSdk 35 | Plataforma nativa |
| Kotlin | JVM 17 | Linguagem principal |
| Jetpack Compose + Material 3 | BOM 2024.12.01 | UI declarativa e design system |
| Clean Architecture + MVVM | — | Separação de responsabilidades |
| Hilt (Dagger) + KSP | 2.51.1 | Injeção de dependências |
| Room | 2.6.1, schema v6 | Persistência local SQLite |
| DataStore Preferences | 1.1.1 | Persistência de sessão (user ID) |
| Retrofit 2 + OkHttp + Gson | 2.11.0 / 4.12.0 | Cliente HTTP (Sprint 2) |
| Kotlin Coroutines + Flow | 1.8.1 | Programação assíncrona e reatividade |
| Navigation Compose | 2.7.7 | Navegação type-safe com rotas seladas |
| kotlinx.serialization | 1.6.3 | Serialização JSON (preparado) |
| Gradle KTS + Version Catalog | AGP 8.5.0 | Build e gestão de dependências |
| JUnit 4 + MockK + Turbine | — | Testes unitários (~133 `@Test` em 22 arquivos) |

---

## 3. Estrutura do Projeto

```
app/src/main/kotlin/com/gtnix/aguiabranca/
├── AguiaBrancaApp.kt                 # @HiltAndroidApp
│
├── domain/                           # Camada de Domínio
│   ├── model/                        # Entidades de negócio
│   ├── repository/                   # Contratos de repositório
│   ├── session/                      # SessionManager (interface)
│   ├── usecase/                      # Casos de uso
│   │   ├── ideia/                    # GetIdeias, AprovarIdeia, ContarIdeiasPendentes
│   │   └── dashboard/                # GetDashboardUseCase
│   └── util/                         # Result<T> sealed class
│
├── data/                             # Camada de Dados
│   ├── local/
│   │   ├── database/                 # AppDatabase, Converters, DatabaseSeeder
│   │   ├── dao/                      # UsuarioDao, IdeiaDao, ProjetoDao, OrientacaoDao
│   │   └── entity/                   # Room @Entity classes
│   ├── remote/
│   │   ├── api/                      # InovacaoApiService, MockInovacaoApi
│   │   └── interceptor/              # MockApiInterceptor
│   ├── repository/                   # Implementações (*Impl)
│   ├── mapper/                       # Entity ↔ Domain
│   ├── session/                      # SessionManagerImpl, SessionDataStore
│   └── util/                         # PasswordHasher, EnumExtensions
│
├── presentation/                     # Camada de Apresentação
│   ├── navigation/                   # Destination, AppNavGraph, MainNavPolicy, AppViewModel
│   ├── theme/                        # Color, Type, Theme, Spacing
│   ├── components/                   # Componentes reutilizáveis (cards, charts, FAB)
│   ├── util/                         # Formatters, SpeechRecognizerHelper, UiState
│   └── screens/                      # Telas por feature
│       ├── auth/                     # Login
│       ├── home/                     # Home (por perfil)
│       ├── ideias/                   # Lista, Nova, Detalhe
│       ├── projetos/                 # Lista, Novo, Detalhe
│       ├── orientacoes/              # Lista, Nova, Editar, Detalhe
│       ├── perfil/                   # Perfil do usuário
│       ├── ranking/                  # Ranking de inovadores
│       ├── leader/                   # Dashboard executivo (Líder)
│       ├── inovacao/                 # Radar de startups
│       └── notificacoes/             # Placeholder "Em breve"
│
└── di/                               # Módulos Hilt
    ├── DatabaseModule.kt
    ├── NetworkModule.kt
    └── RepositoryModule.kt
```

---

## 4. Camada de Domínio

### Models

| Classe / Enum | Descrição |
|---------------|-----------|
| `Usuario` | Dados do usuário com `PerfilUsuario`, `AreaAtuacao`, `DivisaoNegocio` |
| `PerfilUsuario` | `OPERADOR`, `GESTOR`, `LIDER` |
| `Ideia` | Ideia/problema com status, impacto/esforço, vinculação a orientação e projeto |
| `TipoIdeia` | `IDEIA`, `PROBLEMA` |
| `StatusIdeia` | `PENDENTE`, `EM_ANALISE`, `APROVADA`, `REPROVADA`, `CONVERTIDA_PROJETO` |
| `Projeto` | Projeto com ciclo de vida, métricas financeiras e cálculo de ROI |
| `StatusProjeto` | `PLANEJADO`, `EM_ANDAMENTO`, `PAUSADO`, `CONCLUIDO`, `CANCELADO` |
| `OrientacaoEstrategica` | Diretriz estratégica com categoria e prioridade |
| `CategoriaOrientacao` | 7 categorias (ex.: `SUSTENTABILIDADE`, `REDUCAO_CUSTOS`) |
| `Pontuacao`, `NivelUsuario`, `Conquista`, `TipoConquista` | Modelos de gamificação |
| `ResultadoRanking`, `RankingEntrada` | Ranking por perfil e divisão |
| `StartupPartner` | Startup parceira com `matchScore` |
| `Result<T>` | Sealed class: `Success`, `Error`, `Loading` |

### Repository Interfaces

| Interface | Responsabilidade |
|-----------|------------------|
| `UsuarioRepository` | Autenticação, CRUD, listagem por perfil/área |
| `IdeiaRepository` | CRUD de ideias, filtros, avaliação, upvote |
| `ProjetoRepository` | CRUD de projetos, listagem por usuário/área |
| `OrientacaoRepository` | CRUD de orientações estratégicas |
| `InovacaoAbertaRepository` | Busca de startups recomendadas (Radar) |

> **Nota:** não existe `RankingRepository`. O ranking é calculado por `GetRankingUseCase` usando `UsuarioRepository` + `CalcularPontuacaoUseCase`.

### Use Cases

| Use Case | Função |
|----------|--------|
| `GetIdeiasUseCase` | Lista ideias filtradas por perfil (operador: próprias; gestor: área; líder: todas) |
| `AprovarIdeiaUseCase` | Aprova/reprova ideia com validação de perfil e feedback obrigatório |
| `GetDashboardUseCase` | Consolida KPIs (ROI, funil, desempenho por área) com `Flow<Result<DashboardData>>` |
| `CalcularPontuacaoUseCase` | Calcula pontos, nível e conquistas por perfil |
| `GetRankingUseCase` | Monta ranking top-10 com filtro por perfil e divisão |
| `ContarIdeiasPendentesPorAreaUseCase` | Contagem auxiliar para dashboard do gestor |

### SessionManager

Interface em `domain/session/SessionManager.kt`:

- `currentUserFlow: Flow<Usuario?>` — sessão reativa
- `login(usuario)` / `logout()` — controle de sessão
- `restoreSession()` — restaura sessão ao iniciar o app
- `getCurrentUser()` — acesso síncrono ao usuário logado

Implementação: `SessionManagerImpl` (persiste `user_id` via `SessionDataStore` + DataStore Preferences).

---

## 5. Camada de Dados

### Room Database

- **Classe:** `AppDatabase` (version = **6**, `exportSchema = true`)
- **Arquivo:** `aguiabranca.db`
- **Migration:** `fallbackToDestructiveMigration()` (Sprint 1 — dados re-seedados)
- **TypeConverters:** `Converters` para enums e listas

| Entity | DAO | Descrição |
|--------|-----|-----------|
| `UsuarioEntity` | `UsuarioDao` | Usuários com senha hash SHA-256 |
| `IdeiaEntity` | `IdeiaDao` | Ideias com campos de priorização |
| `ProjetoEntity` | `ProjetoDao` | Projetos com métricas financeiras |
| `OrientacaoEntity` | `OrientacaoDao` | Orientações estratégicas |

**Seed:** `DatabaseSeeder` popula usuários demo, orientações, ideias e projetos na primeira execução (`seedDatabaseIfEmpty()`).

### Mappers

| Mapper | Conversão |
|--------|-----------|
| `UsuarioMapper` | `UsuarioEntity` ↔ `Usuario` |
| `IdeiaMapper` | `IdeiaEntity` ↔ `Ideia` |
| `ProjetoMapper` | `ProjetoEntity` ↔ `Projeto` |
| `OrientacaoMapper` | `OrientacaoEntity` ↔ `OrientacaoEstrategica` |

### Retrofit

- **Service:** `InovacaoApiService` — endpoint `GET startups/recomendadas`
- **Implementação Sprint 1:** resposta interceptada por `MockApiInterceptor` (JSON estático)
- **Repository:** `InovacaoAbertaRepositoryImpl` delega à API

### DataStore

- **Classe:** `SessionDataStore`
- **Chave:** `user_id` (string)
- **Arquivo:** `session_preferences`

---

## 6. Camada de Apresentação

### Telas e ViewModels

| Feature | Screen(s) | ViewModel |
|---------|-----------|-----------|
| Auth | `LoginScreen` | `LoginViewModel` |
| Home | `HomeContent` (em `HomeScreen.kt`) | `HomeViewModel` |
| Ideias | `IdeiasScreen`, `NovaIdeiaScreen`, `IdeiaDetalheScreen` | `IdeiasViewModel`, `NovaIdeiaViewModel`, `IdeiaDetalheViewModel` |
| Projetos | `ProjetosScreen`, `NovoProjetoScreen`, `ProjetoDetalheScreen` | `ProjetosViewModel`, `NovoProjetoViewModel`, `ProjetoDetalheViewModel` |
| Orientações | `OrientacoesScreen`, `NovaOrientacaoScreen`, `EditarOrientacaoScreen`, `OrientacaoDetalheScreen` | ViewModels correspondentes |
| Perfil | `PerfilScreen` | `PerfilViewModel` |
| Ranking | `RankingScreen` | `RankingViewModel` |
| Dashboard Líder | `LeaderDashboardScreen` | `LeaderDashboardViewModel` |
| Radar | `RadarScreen` | `RadarViewModel` |
| Notificações | `NotificacoesScreen` | — (placeholder estático) |

### Navegação

- **Rotas:** sealed class `Destination` com rotas type-safe e factory methods (`createRoute()`)
- **NavGraph raiz:** `AppNavGraph` — Login ↔ Home (com parâmetro de perfil)
- **NavGraph interno:** `MainScreen` — bottom nav + rotas secundárias
- **Política de FAB:** `MainNavPolicy.fabActionFor()` — ação contextual por perfil
- **Transições:** slide + fade (300 ms), edge-to-edge
- **Bottom bar:** `FloatingNavBar` com 3–4 tabs (Operador não vê tab Projetos)

### Theme

- **Material 3** customizado em `presentation/theme/`
- Paleta corporativa (`Color.kt`), tipografia Inter (`Type.kt`), espaçamentos (`Spacing.kt`)
- Suporte a dark/light via `InovagabTheme`
- Tokens especiais: `AISpark` para elementos de IA/sugestões

---

## 7. Injeção de Dependências

| Módulo | Tipo | Provê |
|--------|------|-------|
| `DatabaseModule` | `@Provides` | `AppDatabase`, DAOs |
| `NetworkModule` | `@Provides` | `OkHttpClient`, `Retrofit`, `InovacaoApiService` |
| `RepositoryModule` | `@Binds` | Bindings interface → implementação |

**Bindings em `RepositoryModule`:**

- `UsuarioRepository` → `UsuarioRepositoryImpl`
- `IdeiaRepository` → `IdeiaRepositoryImpl`
- `ProjetoRepository` → `ProjetoRepositoryImpl`
- `OrientacaoRepository` → `OrientacaoRepositoryImpl`
- `InovacaoAbertaRepository` → `InovacaoAbertaRepositoryImpl`
- `SessionManager` → `SessionManagerImpl`

**Escopo:** `@Singleton` para database, repositórios, session e network client.

**ViewModels:** injetados via `@HiltViewModel` + `hiltViewModel()` no Compose.

---

## 8. Fluxo de Dados

Exemplo: **listagem de ideias reativa**

```
Room (IdeiaDao.listarTodas)
    │ Flow<List<IdeiaEntity>>
    ▼
IdeiaRepositoryImpl
    │ mapper.toDomainList()
    │ Flow<List<Ideia>>
    ▼
GetIdeiasUseCase
    │ combine(ideias, sessionManager.currentUserFlow)
    │ filtra por perfil
    │ Flow<Result<List<Ideia>>>
    ▼
IdeiasViewModel
    │ collect → atualiza uiState (StateFlow)
    ▼
IdeiasScreen (Compose)
    │ collectAsStateWithLifecycle()
    ▼
UI renderiza lista com IdeiaCard
```

**Princípios:**

1. Room emite `Flow` — qualquer INSERT/UPDATE/DELETE atualiza a UI automaticamente
2. Use Cases aplicam regras de negócio e filtragem por perfil
3. ViewModels nunca acessam DAOs diretamente
4. Operações de escrita (`salvar`, `atualizarStatus`) são `suspend` e executadas em `viewModelScope`

---

## 9. Segurança

### Autenticação

1. Usuário informa email + senha em `LoginScreen`
2. `LoginViewModel` chama `UsuarioRepository.autenticar(email, senha)`
3. `PasswordHasher.hash()` aplica **SHA-256** à senha informada
4. `UsuarioDao.autenticar()` compara hash no banco local
5. Em sucesso, `SessionManager.login(usuario)` persiste `user_id` no DataStore

### Sessão

- `AppViewModel` chama `restoreSession()` no startup
- Se `user_id` válido → navega direto para Home
- Se inválido ou ausente → exibe Login
- Logout limpa DataStore e redireciona para Login

### Controle de Acesso

- Filtragem por perfil nos Use Cases (`GetIdeiasUseCase`, `AprovarIdeiaUseCase`)
- FAB e tabs condicionais via `MainNavPolicy` e `MainScreen`
- CRUD de orientações restrito ao perfil `LIDER` (validado em ViewModels)

### Limitações (Sprint 1)

- Autenticação 100% local (sem JWT/OAuth)
- SHA-256 sem salt (adequado para demo acadêmica; Sprint 2 deve usar bcrypt/Argon2 + backend)

---

## 10. Conectividade Externa

### Arquitetura preparada (Sprint 2)

```
Retrofit (InovacaoApiService)
    │
OkHttpClient
    ├── MockApiInterceptor    ← Sprint 1: intercepta e retorna JSON mock
    └── HttpLoggingInterceptor ← logs em DEBUG
```

### Sprint 1 — Mock

- `MockApiInterceptor` intercepta `GET /startups/recomendadas` e retorna 3 startups com `matchScore`
- `InovacaoAbertaRepositoryImpl.buscarStartupsRecomendadas()` consome via Retrofit normalmente
- A camada de domínio (`StartupPartner`) e apresentação (`RadarViewModel`, `RadarScreen`) não sabem que os dados são mockados

### Sprint 2 — Backend real

- Substituir `MockApiInterceptor` por interceptor de autenticação JWT
- Apontar `BASE_URL` para API Java/C# do backend
- Adicionar endpoints REST para ideias, projetos, orientações e sincronização

---

## 11. Diagrama de Navegação

### Fluxo raiz (AppNavGraph)

```
[App Start]
    │
    ├── sessão restaurada? ──SIM──► Home/{perfil}
    │
    └── NÃO ──► Login ──(sucesso)──► Home/{perfil}
```

### Fluxo interno (MainScreen) — por perfil

```
                    ┌──────────────────────────────────────┐
                    │           BOTTOM NAV                  │
                    │  Home · Ideias · [Projetos] · Perfil │
                    └──────────────────────────────────────┘

OPERADOR                          GESTOR                         LÍDER
────────                          ──────                         ─────
Home                              Home                           Home
 ├─ Orientações (lista/detalhe)    ├─ Ideias pendentes            ├─ Dashboard Executivo
 ├─ Radar Startups                 ├─ Projetos da área            ├─ Orientações (CRUD)
 ├─ Ranking                        ├─ Radar                       ├─ Radar
 └─ FAB: Nova Ideia                └─ FAB: Novo Projeto           └─ FAB: Nova Orientação

Ideias                            Ideias                         Ideias
 ├─ Nova Ideia (voz + IA)          ├─ Detalhe (aprovar/reprovar)  ├─ Detalhe (aprovar)
 └─ Detalhe (próprias)             └─ Converter → Projeto         └─ Todas as ideias

[sem tab Projetos]                Projetos                       Projetos
                                   ├─ Novo (manual/ideia)         ├─ Visão completa
                                   └─ Detalhe (ROI, progresso)    └─ Detalhe

Perfil                            Perfil                         Perfil
 ├─ Gamificação                    ├─ Gamificação                 ├─ Gamificação
 ├─ Ranking                        ├─ Ranking                     ├─ Ranking
 └─ Logout                         └─ Logout                      └─ Logout

Telas secundárias (todos): Ranking · Radar · Notificações (placeholder) · Leader Dashboard (Líder)
```

### Rotas seladas (`Destination`)

| Rota | Parâmetros |
|------|------------|
| `login` | — |
| `home/{perfil}` | perfil (OPERADOR/GESTOR/LIDER) |
| `ideias`, `ideias/nova`, `ideias/{ideiaId}` | ideiaId |
| `projetos`, `projetos/novo?ideiaId={id}`, `projetos/{projetoId}` | ideiaId, projetoId |
| `orientacoes`, `orientacoes/nova`, `orientacoes/{id}`, `orientacoes/editar/{id}` | orientacaoId |
| `perfil`, `ranking`, `radar`, `leader_dashboard`, `notificacoes` | — |

---

*Documento gerado para entrega Sprint 1 — Challenge FIAP 2026.*
