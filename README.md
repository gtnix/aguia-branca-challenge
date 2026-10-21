# Águia Branca Challenge

**Plataforma de Inovação Corporativa** - Challenge FIAP 2026

> Sistema mobile para gestão integrada de inovação corporativa conectando estratégia, pessoas, processos e tecnologia.

---

## Sobre o Projeto

Aplicativo móvel nativo Android desenvolvido para o Grupo Águia Branca como parte do Challenge FIAP 2026. A solução permite capturar ideias e problemas operacionais, estruturá-los em projetos alinhados à estratégia corporativa, e acompanhar resultados mensuráveis.

### Os Três Pilares

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   CAPTURAR  │ ───► │  ESTRUTURAR │ ───► │ ACOMPANHAR  │
│             │      │             │      │             │
│ Dores e     │      │ Transformar │      │ Monitorar   │
│ oportunida- │      │ em inicia-  │      │ até resul-  │
│ des do dia  │      │ tivas ali-  │      │ tados men-  │
│ a dia       │      │ nhadas      │      │ suráveis    │
└─────────────┘      └─────────────┘      └─────────────┘
```

### Perfis de Usuário

| Perfil | Funcionalidades |
|--------|-----------------|
| **Operador** | Consultar orientações estratégicas, cadastrar ideias/problemas por texto, acompanhar status das próprias ideias, ranking e conquistas |
| **Gestor** | Avaliar e priorizar ideias, aprovar/reprovar com feedback, criar e gerenciar projetos, ranking |
| **Líder** | CRUD de orientações estratégicas, visão de todos os projetos, dashboard executivo com métricas, ranking |

---

## Funcionalidades Implementadas

### Autenticação
- [x] Login com 3 perfis de usuário
- [x] Sessão de usuário persistente
- [x] Controle de acesso por perfil

### Ideias/Problemas
- [x] Cadastro de ideias com tipo (Ideia/Problema)
- [x] Associação com área de atuação
- [x] Vinculação a orientações estratégicas
- [x] Campos de priorização (impacto/esforço)
- [x] Listagem com filtros por status
- [x] Avaliação de ideias (aprovar/reprovar)
- [x] Conversão de ideia em projeto

### Projetos
- [x] Criação de projetos (manual ou via ideia)
- [x] Métricas financeiras (investimento/retorno)
- [x] Acompanhamento de progresso
- [x] Ciclo de vida (Planejado → Em Andamento → Concluído)
- [x] Cálculo de ROI

### Perfil
- [x] Visualização de dados do usuário
- [x] Estatísticas pessoais

### Orientações Estratégicas
- [x] CRUD completo de Orientações (Líder)
- [x] Listagem e detalhe
- [x] Editar e excluir orientações

### Dashboard Executivo
- [x] Dashboard com métricas consolidadas (Líder) via API
- [x] ROI, lucro, investimento, retorno, prazos e produtividade
- [x] Funil de inovação e desempenho por área (séries do servidor)

### Gamificação
- [x] Sistema de pontuação e níveis
- [x] Conquistas por perfil
- [x] Ranking de inovadores

---

## Stack Técnica

| Camada | Tecnologia |
|--------|------------|
| Plataforma | Android (minSdk 26, targetSdk 35) |
| Linguagem | Kotlin (JVM 17) |
| UI | Jetpack Compose + Material 3 |
| Arquitetura | Clean Architecture + MVVM |
| DI | Hilt (Dagger) |
| Network | Retrofit 2 + OkHttp + Gson |
| Local DB | Room (apoio à gamificação; API é a fonte do domínio) |
| Async | Kotlin Coroutines + Flow |
| Navigation | Compose Navigation (type-safe) |
| Build | Gradle KTS + Version Catalog |
| Annotation | KSP |

---

## Backend (Sprint 2)

API Java em [`backend/`](./backend/) — ver [`backend/README.md`](./backend/README.md).

## Estrutura do Projeto

```
app/src/main/kotlin/com/gtnix/aguiabranca/
├── domain/                    # Camada de Domínio
│   ├── model/                 # Entities (Usuario, Ideia, Projeto, etc.)
│   ├── repository/            # Interfaces de repositórios
│   ├── session/               # Sessão do usuário
│   └── usecase/               # Casos de uso
│
├── data/                      # Camada de Dados
│   ├── local/                 # Room (database, dao, entity)
│   ├── remote/                # API services
│   ├── repository/            # Implementações dos repositórios
│   └── mapper/                # Conversores Entity ↔ Domain
│
├── presentation/              # Camada de Apresentação
│   ├── navigation/            # Rotas type-safe + NavGraph
│   ├── theme/                 # Material 3 Theme
│   ├── util/                  # Utilitários de UI
│   └── screens/               # Telas organizadas por feature
│       ├── auth/              # Login
│       ├── home/              # Dashboard inicial
│       ├── ideias/            # Lista, Nova, Detalhe
│       ├── projetos/          # Lista, Novo, Detalhe
│       └── perfil/            # Perfil do usuário
│
└── di/                        # Módulos Hilt
    ├── DatabaseModule.kt
    ├── RepositoryModule.kt
    └── NetworkModule.kt
```

---

## Como Executar

### Backend (obrigatório para login na Sprint 2)

```bash
cd backend
docker compose up -d
export MONGODB_URI=mongodb://localhost:27017/inovagab
export JWT_SECRET=dev-only-secret-change-in-production-min-32-chars!!
mvn spring-boot:run
```

Health: `GET http://localhost:8080/api/v1/health`  
Swagger: `http://localhost:8080/api/v1/swagger-ui.html`

### App Android

- **Emulador:** o padrão usa `http://10.0.2.2:8080/api/v1/`
- **Dispositivo físico:** use `INOVAGAB_API_BASE_URL=http://IP-DA-MAQUINA:8080/api/v1/`
- **Ambiente exposto:** configure uma URL HTTPS

O JWT fica em EncryptedSharedPreferences. Login e restauração de sessão exigem rede.

### Pré-requisitos
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 35
- Docker (MongoDB) e Maven (API)

### Build

```bash
# Clone o repositório
git clone https://github.com/gtnix/aguia-branca-challenge.git

# Abra no Android Studio
# File → Open → selecionar pasta do projeto

# Ou via linha de comando
./gradlew testDebugUnitTest assembleDebug lintVitalRelease assembleRelease

# APKs gerados em:
# app/build/outputs/apk/debug/app-debug.apk
# app/build/outputs/apk/release/app-release-unsigned.apk
```

### Usuários de Teste

Senha de todos: `123456` (seed da API + botões demo do app).

| Email | Perfil | Nome |
|-------|--------|------|
| pedro.santos@aguiabranca.com.br | Operador | Pedro Santos |
| ana.oliveira@aguiabranca.com.br | Gestor | Ana Oliveira |
| marcos.silva@aguiabranca.com.br | Líder | Marcos Silva |

---

## Sprints

### Sprint 1 (Entrega: 26/05/2026)
- [x] Setup do projeto Android (Gradle KTS, Version Catalog)
- [x] Estrutura Clean Architecture
- [x] Room Database com dados iniciais
- [x] Autenticação com 3 perfis
- [x] Tela Home por perfil
- [x] Cadastro e listagem de Ideias
- [x] Avaliação de Ideias (Gestor)
- [x] Gestão de Projetos
- [x] Tela de Perfil
- [x] CRUD Orientações Estratégicas
- [x] Dashboard executivo (Líder)
- [x] Testes unitários (UseCases + ViewModels)
- [x] Gamificação (pontuação, conquistas, ranking)
- [x] APK release
- [ ] Vídeo demonstrativo

### Sprint 2 (Fase 6 — DevOps)
- [x] Backend **Java Spring Boot 3** + **MongoDB** (Spring Data MongoDB)
- [x] Autenticação JWT + RBAC no servidor + login Android remoto (EncryptedSharedPreferences)
- [x] Orientações estratégicas via API (CRUD do Líder, leitura Operador/Gestor, soft-delete)
- [x] Ideias do Operador via API (CRUD próprio, ownership, vínculo à orientação, mutações online). Demo: Pedro cria; Ana PUT → 403 e GET vê a ideia. Ver [`backend/README.md`](./backend/README.md).
- [x] Fila do Gestor, scoring IA-A (Gemini/fallback) e aprovação exclusivamente humana
- [x] Conversão idempotente ideia → projeto e CRUD de projetos via API (Gestor muta; Líder somente leitura)
- [x] Dashboard do Líder via API (KPIs, funil e desempenho por área calculados no servidor)
- [x] Integração completa do app com o backend (sem mocks de domínio) — hardening: validação, índices, 400/401/403/404, testes e OpenAPI
- [x] Diferenciais de IA via **Google Gemini**: **IA-A** scoring/priorização de ideias para o Gestor e **IA-C** relatório executivo do dashboard para o Líder, ambos com fallback
- [x] Entregáveis: zips, APK, apresentação unificada e especificação OpenAPI

### Qualidade verificada em 21/09/2026
- Backend: **241 testes**, 0 falhas
- Android: **268 testes**, 0 falhas
- `lintVitalRelease`, build debug/release e smoke dos três perfis: sucesso
- Fluxo ideia → IA/fallback → aprovação → projeto → dashboard: sucesso

📄 **Requisitos detalhados:** [`docs/REQUISITOS_SPRINT2.md`](./docs/REQUISITOS_SPRINT2.md)  
📄 **Requisitos Sprint 1 (baseline):** [`docs/REQUISITOS.md`](./docs/REQUISITOS.md)

---

## Equipe

| Nome | RM | Turma |
|------|-----|-------|
| Giuliana Abe Takara | 562736 | 2TDSA |
| André Luiz Oliveira da Silva | 565836 | 2TDSA |

---

## Licença

Projeto acadêmico - Challenge FIAP 2026

---

*Última atualização: 21 de Setembro de 2026*