# Águia Branca Challenge — Architecture Overview

> Documentação técnica para contexto de desenvolvimento e LLMs.

---

## 1. Proposta do Sistema

**Plataforma de Inovação Corporativa** é um aplicativo mobile Android nativo para o Grupo Águia Branca, desenvolvido como Challenge FIAP 2026.

### Contexto

- **Cliente**: Grupo Águia Branca — um dos maiores conglomerados de transporte e logística do Brasil
- **Objetivo**: Gestão integrada de inovação corporativa conectando estratégia, pessoas, processos e tecnologia
- **Fase atual**: Sprint 1 — App Mobile Nativo Android

---

## 2. Stack Técnica

| Camada | Tecnologia |
|--------|------------|
| Plataforma | Android (minSdk 26, targetSdk 35) |
| Linguagem | Kotlin (JVM 17) |
| UI Framework | Jetpack Compose + Material 3 |
| Arquitetura | Clean Architecture + MVVM |
| DI | Hilt (Dagger) |
| Networking | Retrofit 2 + OkHttp + Gson |
| Local Database | Room |
| Async | Kotlin Coroutines + Flow |
| Navigation | Compose Navigation (type-safe routes) |
| Serialization | Kotlinx Serialization + Gson |
| Build | Gradle KTS + Version Catalog |
| Annotation Processing | KSP |

---

## 3. Arquitetura

O projeto segue **Clean Architecture** com separação em camadas:

```
┌─────────────────────────────────────────────────────────┐
│                   Presentation Layer                     │
│           (Compose UI, ViewModels, Navigation)          │
├─────────────────────────────────────────────────────────┤
│                     Domain Layer                         │
│         (Entities, Use Cases, Repository Ports)         │
├─────────────────────────────────────────────────────────┤
│                      Data Layer                          │
│    (Repository Impl, Data Sources, Mappers, DTOs)       │
└─────────────────────────────────────────────────────────┘
```

### Princípios Aplicados

- **Dependency Inversion**: Domain define interfaces, Data implementa
- **Single Source of Truth**: Room como cache local
- **Unidirectional Data Flow**: State flows down, events flow up
- **MVVM**: ViewModel expõe StateFlow, Compose observa

---

## 4. Estrutura de Pastas

```
app/
├── src/
│   ├── main/
│   │   ├── kotlin/com/gtnix/aguiabranca/
│   │   │   │
│   │   │   ├── domain/                    # Camada de Domínio
│   │   │   │   ├── model/                 # Entities
│   │   │   │   │   ├── Usuario.kt         # + PerfilUsuario, AreaAtuacao enums
│   │   │   │   │   ├── Ideia.kt           # + TipoIdeia, StatusIdeia enums
│   │   │   │   │   ├── Projeto.kt         # + StatusProjeto enum
│   │   │   │   │   ├── OrientacaoEstrategica.kt  # + CategoriaOrientacao enum
│   │   │   │   │   └── StartupPartner.kt  # Para Radar de Inovação
│   │   │   │   ├── repository/            # Repository interfaces (ports)
│   │   │   │   │   ├── UsuarioRepository.kt
│   │   │   │   │   ├── IdeiaRepository.kt
│   │   │   │   │   ├── ProjetoRepository.kt
│   │   │   │   │   ├── OrientacaoRepository.kt
│   │   │   │   │   └── InovacaoAbertaRepository.kt
│   │   │   │   ├── session/               # Sessão do usuário
│   │   │   │   │   └── UserSession.kt
│   │   │   │   └── usecase/               # Use Cases
│   │   │   │       └── CalcularPontuacaoUseCase.kt
│   │   │   │
│   │   │   ├── data/                      # Camada de Dados
│   │   │   │   ├── local/                 # Room Database
│   │   │   │   │   ├── database/
│   │   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   │   ├── DatabaseSeeder.kt  # Dados iniciais
│   │   │   │   │   │   └── Converters.kt      # TypeConverters
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   ├── UsuarioDao.kt
│   │   │   │   │   │   ├── IdeiaDao.kt
│   │   │   │   │   │   ├── ProjetoDao.kt
│   │   │   │   │   │   └── OrientacaoDao.kt
│   │   │   │   │   └── entity/
│   │   │   │   │       ├── UsuarioEntity.kt
│   │   │   │   │       ├── IdeiaEntity.kt
│   │   │   │   │       ├── ProjetoEntity.kt
│   │   │   │   │       └── OrientacaoEntity.kt
│   │   │   │   ├── remote/                # API
│   │   │   │   │   └── api/
│   │   │   │   │       ├── InovacaoApiService.kt
│   │   │   │   │       └── MockInovacaoApi.kt
│   │   │   │   ├── repository/            # Repository implementations
│   │   │   │   │   ├── UsuarioRepositoryImpl.kt
│   │   │   │   │   ├── IdeiaRepositoryImpl.kt
│   │   │   │   │   ├── ProjetoRepositoryImpl.kt
│   │   │   │   │   ├── OrientacaoRepositoryImpl.kt
│   │   │   │   │   └── InovacaoAbertaRepositoryImpl.kt
│   │   │   │   └── mapper/                # Entity <-> Domain mappers
│   │   │   │       ├── UsuarioMapper.kt
│   │   │   │       ├── IdeiaMapper.kt
│   │   │   │       ├── ProjetoMapper.kt
│   │   │   │       └── OrientacaoMapper.kt
│   │   │   │
│   │   │   ├── presentation/              # Camada de Apresentação
│   │   │   │   ├── navigation/            # Compose Navigation
│   │   │   │   │   ├── Destination.kt     # Rotas type-safe
│   │   │   │   │   └── AppNavGraph.kt
│   │   │   │   ├── theme/                 # Material 3 Theme
│   │   │   │   │   ├── Theme.kt
│   │   │   │   │   ├── Color.kt
│   │   │   │   │   └── Type.kt
│   │   │   │   ├── util/                  # Utilitários de UI
│   │   │   │   │   └── CurrencyFormatter.kt
│   │   │   │   └── screens/               # Telas por feature
│   │   │   │       ├── auth/
│   │   │   │       │   ├── LoginScreen.kt
│   │   │   │       │   └── LoginViewModel.kt
│   │   │   │       ├── home/
│   │   │   │       │   ├── HomeScreen.kt
│   │   │   │       │   └── HomeViewModel.kt
│   │   │   │       ├── ideias/
│   │   │   │       │   ├── IdeiasScreen.kt
│   │   │   │       │   ├── IdeiasViewModel.kt
│   │   │   │       │   ├── NovaIdeiaScreen.kt
│   │   │   │       │   ├── NovaIdeiaViewModel.kt
│   │   │   │       │   ├── IdeiaDetalheScreen.kt
│   │   │   │       │   └── IdeiaDetalheViewModel.kt
│   │   │   │       ├── projetos/
│   │   │   │       │   ├── ProjetosScreen.kt
│   │   │   │       │   ├── NovoProjetoScreen.kt
│   │   │   │       │   └── ProjetoDetalheScreen.kt
│   │   │   │       ├── perfil/
│   │   │   │       │   ├── PerfilScreen.kt
│   │   │   │       │   └── PerfilViewModel.kt
│   │   │   │       └── inovacao/
│   │   │   │           ├── RadarScreen.kt
│   │   │   │           └── RadarViewModel.kt
│   │   │   │
│   │   │   ├── di/                        # Dependency Injection (Hilt)
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   ├── RepositoryModule.kt
│   │   │   │   └── NetworkModule.kt
│   │   │   │
│   │   │   └── AguiaBrancaApp.kt          # Application class (@HiltAndroidApp)
│   │   │
│   │   └── res/
│   │       ├── values/
│   │       │   ├── strings.xml
│   │       │   ├── colors.xml
│   │       │   └── themes.xml
│   │       └── drawable/
│   │
│   └── test/                              # Unit tests
│
├── build.gradle.kts                       # App-level build config
├── schemas/                               # Room schema exports
└── proguard-rules.pro
```

---

## 5. Módulos de Domínio

### 5.1 Autenticação e Usuários

**Entities:**
- `Usuario` — Usuário do sistema com perfil e área de atuação

**Perfis:**
```kotlin
enum class PerfilUsuario {
    OPERADOR,   // Acesso básico: consulta estratégias, cadastra ideias
    GESTOR,     // Acesso intermediário: avalia ideias, gerencia projetos
    LIDER       // Acesso total: CRUD estratégias, dashboard completo
}
```

**Áreas de Atuação:**
```kotlin
enum class AreaAtuacao {
    OPERACOES, LOGISTICA, COMERCIAL, FINANCEIRO,
    RH, TI, MARKETING, QUALIDADE
}
```

**Matriz de Permissões:**
| Perfil   | Submeter Ideia | Avaliar | Criar Projeto | Ver Dashboard |
|----------|----------------|---------|---------------|---------------|
| OPERADOR | ✅             | ❌      | ❌            | Próprio       |
| GESTOR   | ✅             | ✅      | ✅            | Área          |
| LIDER    | ✅             | ✅      | ✅            | Completo      |

### 5.2 Orientações Estratégicas

**Entities:**
- `OrientacaoEstrategica` — Diretrizes da liderança para inovação

**Categorias:**
```kotlin
enum class CategoriaOrientacao {
    REDUCAO_CUSTOS,        // Foco em redução de custos
    QUALIDADE_SERVICO,     // Melhoria de qualidade de serviço
    INOVACAO_TECNOLOGICA,  // Inovação tecnológica
    SUSTENTABILIDADE,      // Sustentabilidade e meio ambiente
    SEGURANCA,             // Segurança do trabalho
    EXPERIENCIA_CLIENTE,   // Experiência do cliente
    EFICIENCIA_OPERACIONAL // Eficiência operacional
}
```

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | Read |
| Gestor | Read |
| Líder | CRUD |

### 5.3 Ideias/Problemas

**Entities:**
- `Ideia` — Sugestão de melhoria ou problema identificado

**Tipos:**
```kotlin
enum class TipoIdeia {
    IDEIA,    // Sugestão de melhoria ou inovação
    PROBLEMA  // Problema ou gargalo identificado
}
```

**Status (State Machine):**
```kotlin
enum class StatusIdeia {
    PENDENTE,           // Aguardando avaliação de um gestor
    EM_ANALISE,         // Gestor está analisando
    APROVADA,           // Aprovada pelo gestor
    REPROVADA,          // Reprovada pelo gestor
    CONVERTIDA_PROJETO  // Convertida em projeto
}
```

**Ciclo de Vida:**
```
┌─────────┐   avaliar   ┌───────────┐   aprovar   ┌──────────┐
│ PENDENTE├────────────►│ EM_ANALISE├────────────►│ APROVADA │
└─────────┘             └─────┬─────┘             └────┬─────┘
                              │                        │
                          reprovar                converter
                              │                        │
                              ▼                        ▼
                        ┌───────────┐          ┌──────────────────┐
                        │ REPROVADA │          │CONVERTIDA_PROJETO│
                        └───────────┘          └──────────────────┘
```

**Campos de Priorização:**
- `impactoEstimado`: Impacto esperado (1-5)
- `esforcoEstimado`: Esforço necessário (1-5)
- `scorePriorizacao`: Calculado como (impacto - esforço)

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | Create, Read (próprias) |
| Gestor | Read (todas), Update status, Aprovar/Reprovar |
| Líder | Read (todas) |

### 5.4 Projetos

**Entities:**
- `Projeto` — Iniciativa aprovada em execução

**Status:**
```kotlin
enum class StatusProjeto {
    PLANEJADO,     // Em fase de planejamento
    EM_ANDAMENTO,  // Em execução ativa
    PAUSADO,       // Temporariamente pausado
    CONCLUIDO,     // Finalizado com sucesso
    CANCELADO      // Cancelado
}
```

**Ciclo de Vida:**
```
┌───────────┐    iniciar    ┌─────────────┐    concluir    ┌───────────┐
│ PLANEJADO ├──────────────►│ EM_ANDAMENTO├───────────────►│ CONCLUIDO │
└───────────┘               └──────┬──────┘               └───────────┘
                                   │
                                pausar
                                   │
                                   ▼
                             ┌──────────┐
                             │  PAUSADO │
                             └────┬─────┘
                                  │
                              retomar
                                  │
                                  ▼
                            EM_ANDAMENTO
```

**Métricas Financeiras:**
- `investimentoEstimado` / `investimentoRealizado`
- `retornoEstimadoMensal` / `retornoRealizadoMensal`
- `roi`: Calculado como `((retornoMensal * 12) - investimento) / investimento * 100`

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | — |
| Gestor | CRUD, atualizar progresso |
| Líder | Read, visualizar métricas |

### 5.5 Inovação Aberta (Radar)

**Entities:**
- `StartupPartner` — Parceiros/startups recomendadas

**Campos:**
```kotlin
data class StartupPartner(
    val id: String,
    val nome: String,
    val setor: String,
    val descricao: String,
    val matchScore: Int  // Score de compatibilidade (0-100)
)
```

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | — |
| Gestor | Read |
| Líder | Read |

### 5.6 Dashboard

**Métricas:**
- Total de ideias por status
- Total de projetos por status
- ROI consolidado
- Investimento total vs Retorno
- Produtividade (ideias → projetos)

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | — |
| Gestor | Visão parcial (seus projetos) |
| Líder | Visão completa |

---

## 6. Navegação

### Estrutura de Rotas (Type-Safe)

```kotlin
sealed class Destination(val route: String) {
    // Autenticação
    data object Login : Destination("login")
    
    // Principal
    data object Home : Destination("home/{perfil}")
    
    // Ideias
    data object Ideias : Destination("ideias")
    data object NovaIdeia : Destination("ideias/nova")
    data object IdeiaDetalhe : Destination("ideias/{ideiaId}")
    
    // Projetos
    data object Projetos : Destination("projetos")
    data object NovoProjeto : Destination("projetos/novo?ideiaId={ideiaId}")
    data object ProjetoDetalhe : Destination("projetos/{projetoId}")
    
    // Orientações
    data object Orientacoes : Destination("orientacoes")
    data object NovaOrientacao : Destination("orientacoes/nova")
    
    // Perfil e Extras
    data object Perfil : Destination("perfil")
    data object Radar : Destination("radar")  // Inovação Aberta
}
```

### Fluxo por Perfil

```
┌─────────────────────────────────────────────────────────┐
│                      LoginScreen                         │
│                   (Autenticação)                        │
└─────────────────────────┬───────────────────────────────┘
                          │
          ┌───────────────┼───────────────┐
          ▼               ▼               ▼
    [OPERADOR]       [GESTOR]         [LÍDER]
          │               │               │
          ▼               ▼               ▼
┌─────────────┐   ┌─────────────┐   ┌─────────────┐
│ HomeScreen  │   │ HomeScreen  │   │ HomeScreen  │
├─────────────┤   ├─────────────┤   ├─────────────┤
│ Orientações │   │ Orientações │   │ Orientações*│
│ Minhas      │   │ Todas       │   │ Dashboard   │
│ Ideias      │   │ Ideias      │   │ Projetos    │
│ Perfil      │   │ Projetos    │   │ Radar       │
│             │   │ Radar       │   │ Perfil      │
└─────────────┘   └─────────────┘   └─────────────┘

* CRUD completo
```

### Bottom Navigation

```kotlin
enum class BottomNavItem {
    HOME,     // Destination.Home - "home"
    IDEIAS,   // Destination.Ideias - "lightbulb"
    PROJETOS, // Destination.Projetos - "folder"
    PERFIL    // Destination.Perfil - "person"
}
```

---

## 7. Banco de Dados Local (Room)

### Tabelas e Relacionamentos

```
┌─────────────┐       ┌────────────┐       ┌─────────────┐
│  usuarios   ├───────┤   ideias   ├───────┤ orientacoes │
└──────┬──────┘       └──────┬─────┘       └──────┬──────┘
       │                     │                    │
       │                     │ se aprovada        │
       │                     ▼                    │
       │              ┌───────────┐               │
       └──────────────┤  projetos ├───────────────┘
                      └───────────┘
```

### Entities

```kotlin
// Tabela: usuarios
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val email: String,
    val senhaHash: String,  // Hash da senha (nunca texto puro!)
    val perfil: String,     // OPERADOR, GESTOR, LIDER
    val area: String,       // Área de atuação
    val fotoPerfil: String?,
    val dataCadastro: Long
)

// Tabela: orientacoes
@Entity(tableName = "orientacoes")
data class OrientacaoEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descricao: String,
    val categoria: String,  // CategoriaOrientacao
    val prioridade: Int,
    val ativa: Boolean,
    val criadoPor: String?,
    val dataCriacao: Long,
    val dataExpiracao: Long?
)

// Tabela: ideias
@Entity(tableName = "ideias")
data class IdeiaEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descricao: String,
    val tipo: String,           // IDEIA ou PROBLEMA
    val area: String,           // Área de atuação
    val status: String,         // StatusIdeia
    val autorId: String,        // FK → usuarios
    val autorNome: String,      // Denormalizado para performance
    val orientacaoId: String?,  // FK → orientacoes
    val feedback: String?,      // Feedback do gestor
    val projetoId: String?,     // FK → projetos (se convertida)
    val dataCriacao: Long,
    val dataAvaliacao: Long?,
    val impactoEstimado: Int,   // 1-5
    val esforcoEstimado: Int    // 1-5
)

// Tabela: projetos
@Entity(tableName = "projetos")
data class ProjetoEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val objetivo: String,
    val descricao: String,
    val area: String,               // Área de atuação
    val status: String,             // StatusProjeto
    val ideiaOrigemId: String?,     // FK → ideias
    val orientacaoId: String?,      // FK → orientacoes
    val responsavelId: String?,     // FK → usuarios
    val responsavelNome: String,    // Denormalizado
    val membrosIds: String,         // JSON Array de IDs
    val dataCriacao: Long,
    val dataInicio: Long?,
    val dataPrevistaConclusao: Long?,
    val dataConclusao: Long?,
    val progresso: Int,             // 0-100
    val resultados: String?,
    val investimentoEstimado: Double,
    val investimentoRealizado: Double,
    val retornoEstimadoMensal: Double,
    val retornoRealizadoMensal: Double
)
```

### Índices para Performance

```kotlin
// IdeiaEntity
@Index("autorId")      // Minhas ideias
@Index("area")         // Ideias por área
@Index("status")       // Ideias pendentes
@Index("orientacaoId") // Ideias por orientação

// ProjetoEntity
@Index("responsavelId") // Meus projetos
@Index("area")          // Projetos por área
@Index("status")        // Projetos por status
```

### TypeConverters

Lista de IDs (`membrosIds`) é convertida para JSON String via TypeConverter para simplificar o Sprint 1.

---

## 8. Telas Principais

| Tela | Descrição | Perfis |
|------|-----------|--------|
| `LoginScreen` | Autenticação com email/senha | Todos |
| `HomeScreen` | Dashboard inicial por perfil | Todos |
| `IdeiasScreen` | Lista de ideias (filtros por status) | Todos |
| `NovaIdeiaScreen` | Cadastrar nova ideia | Operador, Gestor |
| `IdeiaDetalheScreen` | Detalhe + avaliação | Todos (ações por perfil) |
| `ProjetosScreen` | Lista de projetos | Gestor, Líder |
| `NovoProjetoScreen` | Criar projeto (pode vir de ideia) | Gestor |
| `ProjetoDetalheScreen` | Detalhe + progresso | Gestor, Líder |
| `PerfilScreen` | Perfil do usuário logado | Todos |
| `RadarScreen` | Radar de Inovação (startups) | Gestor, Líder |

### Telas Planejadas (Sprint 2)

| Tela | Descrição | Perfis |
|------|-----------|--------|
| `OrientacoesScreen` | Lista orientações estratégicas | Todos |
| `NovaOrientacaoScreen` | Criar/Editar orientação | Líder |
| `DashboardScreen` | Métricas consolidadas completas | Líder |

---

## 9. Critérios de Avaliação FIAP

| Critério | Peso | Como atender |
|----------|------|--------------|
| Adequação ao problema | 20% | Todas funcionalidades do desafio |
| Implementação técnica | 30% | App funcional, Clean Architecture |
| Qualidade do código | 25% | Kotlin idiomático, SOLID, testes |
| Documentação | 15% | Este doc + README + vídeo |
| Criatividade | 10% | Gamificação, UX diferenciada |

---

## 10. Roadmap Sprint 1

### Concluído

- [x] Setup projeto Android (Gradle KTS, Version Catalog)
- [x] Estrutura Clean Architecture (domain/data/presentation)
- [x] Entidades de domínio (Usuario, Ideia, Projeto, OrientacaoEstrategica, StartupPartner)
- [x] Enums de negócio (PerfilUsuario, AreaAtuacao, StatusIdeia, StatusProjeto, etc.)
- [x] Room Database + Entities + DAOs
- [x] Mappers (Entity ↔ Domain)
- [x] Repositórios implementados
- [x] Injeção de dependência (Hilt modules)
- [x] Tela de Login com validação
- [x] Home por perfil
- [x] Listagem de Ideias com filtros
- [x] Cadastro de Ideias (NovaIdeiaScreen)
- [x] Detalhe e avaliação de Ideias
- [x] Listagem de Projetos
- [x] Criação de Projetos (conversão de ideia)
- [x] Detalhe de Projetos
- [x] Tela de Perfil
- [x] Radar de Inovação (startups parceiras)
- [x] Navegação type-safe com sealed class
- [x] DatabaseSeeder com dados iniciais

### Pendente

- [ ] CRUD completo de Orientações Estratégicas
- [ ] Dashboard com métricas consolidadas
- [ ] Testes unitários
- [ ] APK release
- [ ] Vídeo demonstrativo

---

## 11. Iniciativa de Redesign UI/UX Premium

### Objetivo

Modernizar a aparência visual do app para atingir padrões de qualidade comparáveis a apps como **Revolut**, **Apple Wallet** e **Linear** — saindo de uma estética corporativa genérica para um design premium e sofisticado.

### Documentação

Ver [`docs/PROMPT_MANUS_UX_RESEARCH.md`](./PROMPT_MANUS_UX_RESEARCH.md) para o prompt completo de pesquisa de tendências e proposta de redesign.

### Áreas de Modernização

| Área | Estado Atual | Objetivo |
|------|--------------|----------|
| Paleta de Cores | Azul/laranja corporativo tradicional | Paleta sofisticada com gradientes sutis e dark mode |
| Tipografia | Roboto padrão | Hierarquia clara com fonte moderna (Inter, SF Pro style) |
| Cards | Bordas conservadoras, espaçamento denso | Glassmorphism sutil, whitespace generoso |
| Microinterações | Mínimas | Transições fluidas, feedback visual elegante |
| Iconografia | Material Icons padrão | Ícones customizados ou biblioteca moderna |
| Layout | Enterprise software clássico | Bento grid, hierarchy-first design |

### Incorporação de IA (Sprint 2+)

| Feature | Perfil | Descrição |
|---------|--------|-----------|
| Voz-para-ideia | Operador | Captura de ideias por voz com transcrição automática |
| Clustering de ideias | Gestor | Agrupar ideias similares automaticamente |
| Insights automáticos | Líder | Alertas sobre tendências e anomalias |
| Busca semântica | Todos | Encontrar ideias/projetos por conceito |

### Priorização

1. **Quick Wins**: Ajustes de cores, tipografia, spacing
2. **Medium Effort**: Redesign de componentes, dark mode
3. **High Effort**: Features AI, motion design avançado

---

## 11. Como Executar

```bash
# Abrir no Android Studio
# File → Open → selecionar pasta do projeto

# Ou via linha de comando
./gradlew assembleDebug

# APK gerado em:
# app/build/outputs/apk/debug/app-debug.apk
```

---

*Última atualização: 25 de Maio de 2026 — Sprint 1*
