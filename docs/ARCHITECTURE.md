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
| Linguagem | Kotlin 2.x |
| UI Framework | Jetpack Compose |
| Arquitetura | Clean Architecture + MVVM |
| DI | Hilt (Dagger) |
| Networking | Retrofit 2 + OkHttp |
| Local Database | Room |
| Async | Kotlin Coroutines + Flow |
| Navigation | Compose Navigation |
| Serialization | Kotlinx Serialization |

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
│   │   │   │   │   ├── Usuario.kt
│   │   │   │   │   ├── Ideia.kt
│   │   │   │   │   ├── Projeto.kt
│   │   │   │   │   └── OrientacaoEstrategica.kt
│   │   │   │   ├── repository/            # Repository interfaces (ports)
│   │   │   │   │   ├── UsuarioRepository.kt
│   │   │   │   │   ├── IdeiaRepository.kt
│   │   │   │   │   └── ProjetoRepository.kt
│   │   │   │   └── usecase/               # Use Cases
│   │   │   │       ├── auth/
│   │   │   │       ├── ideias/
│   │   │   │       └── projetos/
│   │   │   │
│   │   │   ├── data/                      # Camada de Dados
│   │   │   │   ├── local/                 # Room Database
│   │   │   │   │   ├── AppDatabase.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   └── entity/
│   │   │   │   ├── remote/                # API (Sprint 2)
│   │   │   │   │   ├── api/
│   │   │   │   │   └── dto/
│   │   │   │   ├── repository/            # Repository implementations
│   │   │   │   └── mapper/                # Entity <-> DTO mappers
│   │   │   │
│   │   │   ├── presentation/              # Camada de Apresentação
│   │   │   │   ├── navigation/            # Compose Navigation
│   │   │   │   │   └── AppNavGraph.kt
│   │   │   │   ├── theme/                 # Material 3 Theme
│   │   │   │   ├── components/            # Componentes reutilizáveis
│   │   │   │   └── screens/               # Telas por feature
│   │   │   │       ├── auth/
│   │   │   │       │   ├── LoginScreen.kt
│   │   │   │       │   └── LoginViewModel.kt
│   │   │   │       ├── home/
│   │   │   │       ├── ideias/
│   │   │   │       ├── projetos/
│   │   │   │       └── dashboard/
│   │   │   │
│   │   │   ├── di/                        # Dependency Injection
│   │   │   │   ├── AppModule.kt
│   │   │   │   ├── DatabaseModule.kt
│   │   │   │   └── RepositoryModule.kt
│   │   │   │
│   │   │   └── AguiaBrancaApp.kt          # Application class
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
└── proguard-rules.pro
```

---

## 5. Módulos de Domínio

### 5.1 Autenticação

**Entities:**
- `Usuario` — Usuário do sistema com perfil (Operador, Gestor, Líder)

**Perfis:**
```kotlin
enum class PerfilUsuario {
    OPERADOR,   // Acesso básico: consulta estratégias, cadastra ideias
    GESTOR,     // Acesso intermediário: avalia ideias, gerencia projetos
    LIDER       // Acesso total: CRUD estratégias, dashboard completo
}
```

### 5.2 Orientações Estratégicas

**Entities:**
- `OrientacaoEstrategica` — Diretrizes da liderança para inovação

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | Read |
| Gestor | Read |
| Líder | CRUD |

### 5.3 Ideias/Problemas

**Entities:**
- `Ideia` — Sugestão ou problema identificado pelo operador

**Status:**
```kotlin
enum class StatusIdeia {
    PENDENTE,      // Aguardando avaliação
    EM_ANALISE,    // Sendo avaliada pelo gestor
    APROVADA,      // Aprovada para virar projeto
    REJEITADA,     // Não aprovada
    ARQUIVADA      // Arquivada para referência futura
}
```

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | Create, Read (próprias) |
| Gestor | Read (todas), Update status, Aprovar/Rejeitar |
| Líder | Read (todas) |

### 5.4 Projetos

**Entities:**
- `Projeto` — Iniciativa aprovada em execução

**Status:**
```kotlin
enum class StatusProjeto {
    PLANEJAMENTO,
    EM_EXECUCAO,
    PAUSADO,
    CONCLUIDO,
    CANCELADO
}
```

**Permissões:**
| Perfil | Permissão |
|--------|-----------|
| Operador | — |
| Gestor | CRUD, atualizar progresso |
| Líder | Read, visualizar métricas |

### 5.5 Dashboard

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
│ Estratégias │   │ Estratégias │   │ Estratégias*│
│ Minhas      │   │ Todas       │   │ Dashboard   │
│ Ideias      │   │ Ideias      │   │ Projetos    │
│             │   │ Projetos    │   │             │
└─────────────┘   └─────────────┘   └─────────────┘

* CRUD completo
```

---

## 7. Banco de Dados Local (Room)

### Tabelas

```kotlin
// usuarios
@Entity
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val email: String,
    val perfil: String,  // OPERADOR, GESTOR, LIDER
    val ativo: Boolean
)

// orientacoes_estrategicas
@Entity
data class OrientacaoEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descricao: String,
    val criadoPor: String,
    val criadoEm: Long,
    val atualizadoEm: Long
)

// ideias
@Entity
data class IdeiaEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descricao: String,
    val area: String,
    val status: String,
    val autorId: String,
    val criadoEm: Long,
    val atualizadoEm: Long
)

// projetos
@Entity
data class ProjetoEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val descricao: String,
    val status: String,
    val ideiaOrigemId: String?,
    val responsavelId: String,
    val investimento: Double,
    val retornoEsperado: Double,
    val retornoReal: Double?,
    val dataInicio: Long,
    val dataFimPrevista: Long,
    val dataFimReal: Long?,
    val criadoEm: Long,
    val atualizadoEm: Long
)
```

---

## 8. Telas Principais

| Tela | Descrição | Perfis |
|------|-----------|--------|
| `LoginScreen` | Autenticação com email/senha | Todos |
| `HomeScreen` | Dashboard inicial por perfil | Todos |
| `EstrategiasScreen` | Lista orientações estratégicas | Todos |
| `EstrategiaFormScreen` | Criar/Editar orientação | Líder |
| `IdeiasListScreen` | Lista de ideias | Todos |
| `IdeiaFormScreen` | Cadastrar ideia | Operador |
| `IdeiaDetailScreen` | Detalhe + ações | Gestor |
| `ProjetosListScreen` | Lista de projetos | Gestor, Líder |
| `ProjetoFormScreen` | Criar/Editar projeto | Gestor |
| `ProjetoDetailScreen` | Detalhe + progresso | Gestor, Líder |
| `DashboardScreen` | Métricas consolidadas | Líder |

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

### Semana 1 (até 26/05)

- [x] Setup projeto Android
- [x] Estrutura Clean Architecture
- [ ] Entidades de domínio
- [ ] Room Database + DAOs
- [ ] Repositórios (in-memory primeiro)
- [ ] Tela de Login
- [ ] Home por perfil
- [ ] CRUD Orientações
- [ ] Cadastro de Ideias
- [ ] Listagem e avaliação de Ideias
- [ ] Gestão de Projetos
- [ ] Dashboard básico
- [ ] APK funcional
- [ ] Vídeo demonstrativo

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

*Última atualização: Maio 2026 — Sprint 1*
