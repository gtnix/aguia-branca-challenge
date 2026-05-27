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
| **Operador** | Consultar orientações estratégicas, cadastrar ideias/problemas (texto ou voz), acompanhar status das próprias ideias, ranking e conquistas |
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

### Inovação Aberta
- [x] Radar de startups parceiras
- [x] Score de compatibilidade

### Perfil
- [x] Visualização de dados do usuário
- [x] Estatísticas pessoais

### Orientações Estratégicas
- [x] CRUD completo de Orientações (Líder)
- [x] Listagem e detalhe
- [x] Editar e excluir orientações

### Dashboard Executivo
- [x] Dashboard com métricas consolidadas (Líder)
- [x] ROI, investimento, retorno
- [x] Funil de inovação

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
| Local DB | Room |
| Async | Kotlin Coroutines + Flow |
| Navigation | Compose Navigation (type-safe) |
| Build | Gradle KTS + Version Catalog |
| Annotation | KSP |

---

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
│       ├── perfil/            # Perfil do usuário
│       └── inovacao/          # Radar de startups
│
└── di/                        # Módulos Hilt
    ├── DatabaseModule.kt
    ├── RepositoryModule.kt
    └── NetworkModule.kt
```

---

## Como Executar

### Pré-requisitos
- Android Studio Hedgehog (2023.1.1) ou superior
- JDK 17
- Android SDK 35

### Build

```bash
# Clone o repositório
git clone https://github.com/gtnix/aguia-branca-challenge.git

# Abra no Android Studio
# File → Open → selecionar pasta do projeto

# Ou via linha de comando
./gradlew assembleDebug

# APK gerado em:
# app/build/outputs/apk/debug/app-debug.apk
```

### Usuários de Teste

| Email | Senha | Perfil | Nome |
|-------|-------|--------|------|
| motorista@aguiabranca.com.br | 123456 | Operador | Pedro Santos |
| gestor@aguiabranca.com.br | 123456 | Gestor | Ana Oliveira |
| ceo@aguiabranca.com.br | 123456 | Líder | Carlos Silva |

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
- [x] Radar de Inovação
- [x] Tela de Perfil
- [x] CRUD Orientações Estratégicas
- [x] Dashboard executivo (Líder)
- [x] Testes unitários (UseCases + ViewModels)
- [x] Gamificação (pontuação, conquistas, ranking)
- [ ] APK release
- [x] Vídeo demonstrativo — https://youtu.be/9FJmqSRNSkQ

### Sprint 2 (2º Semestre)
- Backend Java/C#
- APIs REST completas
- Integração com serviços externos
- Segurança e autenticação JWT
- Observabilidade (logs, métricas)

---

## Equipe

| Nome | RM | Turma |
|------|-----|-------|
| Giuliana Takara | - | - |

---

## Entregas

- Vídeo demonstrativo: https://youtu.be/9FJmqSRNSkQ

## Documentação

| Documento | Descrição |
|-----------|-----------|
| [ARCHITECTURE.md](docs/ARCHITECTURE.md) | Arquitetura técnica detalhada |
| [REQUISITOS.md](docs/REQUISITOS.md) | Levantamento completo de requisitos |
| [PROMPT_MANUS_UX_RESEARCH.md](docs/PROMPT_MANUS_UX_RESEARCH.md) | Prompt para pesquisa de UI/UX e redesign premium |
| [material-aula/](docs/material-aula/) | Material de referência FIAP |

---

## Licença

Projeto acadêmico - Challenge FIAP 2026

---

*Última atualização: 26 de Maio de 2026*
