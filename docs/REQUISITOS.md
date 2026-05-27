# Levantamento de Requisitos — InovaGAB

**Plataforma de Inovação Corporativa | Challenge FIAP 2026 — Grupo Águia Branca**

---

## 1. Problema Proposto

O Grupo Águia Branca, conglomerado com atuação em transporte de passageiros, logística e comércio, precisa de uma **plataforma de inovação corporativa** que conecte estratégia, pessoas, processos e tecnologia.

O desafio proposto pela FIAP exige uma solução mobile que permita:

1. **Capturar** dores e oportunidades do dia a dia (operadores de campo)
2. **Estruturar** ideias em projetos alinhados à estratégia corporativa (gestores)
3. **Acompanhar** resultados mensuráveis e direcionar a inovação (líderes)

A solução entregue — app Android **InovaGAB** — implementa esses três pilares com três perfis de usuário, persistência offline-first e gamificação para engajamento.

---

## 2. Requisitos Funcionais

### 2.1 Autenticação (Auth)

| ID | Requisito | Status |
|----|-----------|--------|
| AUTH-01 | Login com email e senha | ✅ Implementado |
| AUTH-02 | Três perfis: Operador, Gestor, Líder | ✅ Implementado |
| AUTH-03 | Sessão persistente entre reinicializações | ✅ DataStore |
| AUTH-04 | Login demo rápido por perfil (botões na tela) | ✅ Implementado |
| AUTH-05 | Seed automático de usuários demo na 1ª execução | ✅ DatabaseSeeder |
| AUTH-06 | Logout com limpeza de sessão | ✅ Implementado |

### 2.2 Orientações Estratégicas

| ID | Requisito | Status |
|----|-----------|--------|
| ORI-01 | Listar orientações ativas | ✅ Todos os perfis |
| ORI-02 | Visualizar detalhe de orientação | ✅ Implementado |
| ORI-03 | Criar orientação (Líder) | ✅ NovaOrientacaoScreen |
| ORI-04 | Editar orientação (Líder) | ✅ EditarOrientacaoScreen |
| ORI-05 | Excluir orientação (Líder) | ✅ OrientacoesViewModel |
| ORI-06 | Categorias e prioridade | ✅ CategoriaOrientacao |

### 2.3 Ideias / Problemas

| ID | Requisito | Status |
|----|-----------|--------|
| IDE-01 | Cadastrar ideia ou problema | ✅ NovaIdeiaScreen |
| IDE-02 | Entrada por voz (SpeechRecognizer pt-BR) | ✅ VoiceRecordButton |
| IDE-03 | Sugestões de área/impacto por análise de texto | ✅ Regras locais (keywords) |
| IDE-04 | Associar área de atuação e orientação estratégica | ✅ Implementado |
| IDE-05 | Campos de impacto e esforço estimados | ✅ Implementado |
| IDE-06 | Listar ideias com filtro por status | ✅ IdeiasScreen |
| IDE-07 | Visualizar detalhe da ideia | ✅ IdeiaDetalheScreen |
| IDE-08 | Avaliar ideia: aprovar/reprovar (Gestor/Líder) | ✅ AprovarIdeiaUseCase |
| IDE-09 | Feedback obrigatório ao reprovar | ✅ Validado no Use Case |
| IDE-10 | Converter ideia aprovada em projeto | ✅ Navegação para NovoProjeto |
| IDE-11 | Indicador de similaridade (flag `temSimilaridade`) | ✅ Exibido em IdeiaCard (dados seed) |
| IDE-12 | Upvote em ideias | ✅ IdeiaRepository.incrementUpvote |

### 2.4 Projetos

| ID | Requisito | Status |
|----|-----------|--------|
| PRO-01 | Criar projeto manualmente | ✅ NovoProjetoScreen |
| PRO-02 | Criar projeto a partir de ideia aprovada | ✅ Com ideiaId na rota |
| PRO-03 | Listar projetos (Gestor/Líder) | ✅ ProjetosScreen |
| PRO-04 | Visualizar detalhe com progresso e ROI | ✅ ProjetoDetalheScreen |
| PRO-05 | Métricas financeiras (investimento/retorno) | ✅ Projeto.investimento/retorno |
| PRO-06 | Ciclo de vida (Planejado → Em Andamento → Concluído) | ✅ StatusProjeto |
| PRO-07 | Cálculo automático de ROI | ✅ Propriedade `Projeto.roi` |

### 2.5 Dashboard

| ID | Requisito | Status |
|----|-----------|--------|
| DASH-01 | Home personalizada por perfil | ✅ HomeViewModel + HomeContent |
| DASH-02 | KPIs consolidados (Líder) | ✅ LeaderDashboardScreen |
| DASH-03 | ROI, investimento e retorno total | ✅ GetDashboardUseCase |
| DASH-04 | Funil de inovação | ✅ FunnelChart |
| DASH-05 | Desempenho por área | ✅ HorizontalBarChart |
| DASH-06 | Recap semanal (novas ideias, avaliações) | ✅ WeeklyRecap |
| DASH-07 | Narrativa de insights (Líder) | ✅ generateAINarrative (baseada em KPIs) |

### 2.6 Gamificação

| ID | Requisito | Status |
|----|-----------|--------|
| GAM-01 | Sistema de pontuação por ações | ✅ CalcularPontuacaoUseCase |
| GAM-02 | Níveis de usuário (5 níveis) | ✅ NivelUsuario enum |
| GAM-03 | Conquistas desbloqueáveis por perfil | ✅ TipoConquista + tiers |
| GAM-04 | Ranking top-10 por perfil | ✅ RankingScreen |
| GAM-05 | Filtro por divisão (minha / grupo completo) | ✅ DivisaoFilter |
| GAM-06 | Posição do usuário logado no ranking | ✅ ResultadoRanking |

### 2.7 Inovação Aberta

| ID | Requisito | Status |
|----|-----------|--------|
| INO-01 | Radar de startups parceiras | ✅ RadarScreen |
| INO-02 | Score de compatibilidade (matchScore) | ✅ StartupPartner |
| INO-03 | Integração via Retrofit (mock Sprint 1) | ✅ MockApiInterceptor |

### 2.8 Outros

| ID | Requisito | Status |
|----|-----------|--------|
| OUT-01 | Perfil do usuário com estatísticas | ✅ PerfilScreen |
| OUT-02 | Notificações | ⏳ Placeholder ("Em breve") |
| OUT-03 | Pull-to-refresh na Home | ✅ PullToRefreshBox |

---

## 3. Requisitos Não-Funcionais

| ID | Categoria | Requisito | Implementação |
|----|-----------|-----------|---------------|
| RNF-01 | Performance | UI não bloqueia Main Thread | Coroutines + `Dispatchers.Default` em KPIs pesados |
| RNF-02 | Performance | Listas reativas sem reload manual | Room `Flow` + Compose recomposition |
| RNF-03 | Segurança | Senhas não armazenadas em texto plano | SHA-256 via `PasswordHasher` |
| RNF-04 | Segurança | Controle de acesso por perfil | Use Cases + ViewModels |
| RNF-05 | UX | Navegação intuitiva com bottom bar + FAB contextual | `MainNavPolicy` |
| RNF-06 | UX | Transições animadas entre telas | Slide/fade 300 ms |
| RNF-07 | UX | Suporte dark/light theme | Material 3 `InovagabTheme` |
| RNF-08 | UX | Edge-to-edge layout | Activity + Scaffold |
| RNF-09 | Offline-first | App funcional sem internet | Room como fonte primária |
| RNF-10 | Manutenibilidade | Arquitetura testável | Clean Architecture + DI |
| RNF-11 | Qualidade | Cobertura de testes unitários | ~133 testes (Use Cases + ViewModels) |
| RNF-12 | Compatibilidade | Android 8.0+ (API 26) | minSdk 26 |

---

## 4. Matriz de Funcionalidades por Perfil

| Funcionalidade | Operador | Gestor | Líder |
|----------------|:--------:|:------:|:-----:|
| Login / Logout | ✅ | ✅ | ✅ |
| Consultar orientações estratégicas | ✅ | ✅ | ✅ |
| CRUD orientações estratégicas | ❌ | ❌ | ✅ |
| Cadastrar ideias (texto/voz) | ✅ | ❌ | ❌ |
| Listar ideias (escopo filtrado) | Próprias | Da área | Todas |
| Avaliar ideias (aprovar/reprovar) | ❌ | ✅ | ✅ |
| Converter ideia em projeto | ❌ | ✅ | ✅ |
| Criar projetos | ❌ | ✅ | ✅ |
| Listar/gerenciar projetos | ❌ | Da área | Todos |
| Dashboard executivo (KPIs) | ❌ | Parcial (Home) | ✅ |
| Radar de startups | ✅ | ✅ | ✅ |
| Gamificação (pontos/nível/conquistas) | ✅ | ✅ | ✅ |
| Ranking | ✅ | ✅ | ✅ |
| Perfil e estatísticas | ✅ | ✅ | ✅ |
| Notificações | ⏳ | ⏳ | ⏳ |
| Tab Projetos (bottom nav) | ❌ | ✅ | ✅ |
| FAB contextual | Nova Ideia | Novo Projeto | Nova Orientação |

---

## 5. Fluxos Principais

### 5.1 Login e Restauração de Sessão

```
1. App inicia → AppViewModel.restoreSession()
2. SessionDataStore retorna user_id (se existir)
3. UsuarioRepository.buscarPorId() valida usuário
4. Se válido → Home/{perfil}; senão → Login
5. Login: email + senha → hash SHA-256 → autenticar no Room
6. Sucesso → SessionManager.login() → navega para Home
```

**Usuários demo (senha: `123456`):**

| Email | Perfil | Nome |
|-------|--------|------|
| pedro.santos@aguiabranca.com.br | Operador | Pedro Santos |
| ana.oliveira@aguiabranca.com.br | Gestor | Ana Oliveira |
| marcos.silva@aguiabranca.com.br | Líder | Marcos Silva |

### 5.2 Cadastro de Ideia (Operador)

```
1. Operador acessa Nova Ideia (FAB ou Home)
2. Opcional: grava voz via SpeechRecognizerHelper (pt-BR)
   └─ Fallback: simulação typewriter se reconhecimento indisponível
3. Texto transcrito → analyzeTextForAISuggestions()
   └─ Sugere área (Logística, Comercial, etc.) e impacto por keywords
4. Operador preenche/confirma campos (tipo, área, orientação)
5. NovaIdeiaViewModel.salvar() → IdeiaRepository.salvar()
6. Room persiste → Flow atualiza listas automaticamente
```

### 5.3 Avaliação de Ideia (Gestor)

```
1. Gestor acessa Ideias → filtra pendentes da sua área
2. Abre detalhe → informa impacto/esforço
3. Aprova ou reprova (feedback obrigatório se reprovar)
4. AprovarIdeiaUseCase valida perfil e atualiza status
5. Ideia aprovada pode ser convertida em projeto
```

### 5.4 Conversão Ideia → Projeto (Gestor)

```
1. Gestor abre ideia aprovada → "Converter em Projeto"
2. Navega para NovoProjetoScreen com ideiaId
3. Preenche dados do projeto (objetivo, métricas, responsável)
4. ProjetoRepository.salvar() + IdeiaRepository.vincularProjeto()
5. Status da ideia → CONVERTIDA_PROJETO
```

### 5.5 Gestão de Orientações (Líder)

```
1. Líder acessa Orientações (Home ou FAB)
2. Cria/edita/exclui orientações estratégicas
3. OrientacaoRepository persiste no Room
4. Operadores vinculam ideias às orientações ativas
5. Líder acompanha alinhamento no Dashboard Executivo
```

### 5.6 Ranking e Gamificação

```
1. Usuário acessa Ranking (Home ou Perfil)
2. GetRankingUseCase lista usuários do perfil selecionado
3. CalcularPontuacaoUseCase computa pontos por ações do perfil
4. Ranking exibe top-10 + posição do usuário logado
5. Conquistas e nível visíveis no Perfil e Home
```

### 5.7 Radar de Startups

```
1. Usuário acessa Radar (Home)
2. RadarViewModel chama InovacaoAbertaRepository
3. Retrofit → MockApiInterceptor retorna JSON mock
4. RadarScreen exibe startups com matchScore
```

---

## 6. Conectividade com Serviços Externos

### Implementado (Sprint 1)

| Serviço | Integração | Dados |
|---------|------------|-------|
| Radar de Startups | Retrofit + `MockApiInterceptor` | 3 startups mockadas com matchScore |
| Reconhecimento de voz | Android `SpeechRecognizer` API | Transcrição pt-BR on-device |

### Estratégia de Integração

```
┌─────────────┐     Sprint 1      ┌──────────────────┐
│  ViewModel  │ ────────────────► │ Repository (domain)│
└─────────────┘                   └────────┬─────────┘
                                             │
                              ┌──────────────┴──────────────┐
                              │                             │
                     Room (offline-first)          Retrofit (remoto)
                     fonte primária               mock → real (Sprint 2)
```

- **Offline-first:** Room é a fonte de verdade para ideias, projetos, orientações e usuários
- **Retrofit preparado:** stack completa (OkHttp, Gson, logging) com interceptor substituível
- **Sincronização futura:** Sprint 2 adicionará sync bidirecional com backend REST

---

## 7. Inovações Implementadas

| Inovação | Descrição | Implementação |
|----------|-----------|---------------|
| **Gamificação corporativa** | Pontos, 5 níveis, 16 tipos de conquistas com tiers (Bronze→Platina), ranking por perfil | `CalcularPontuacaoUseCase`, `GetRankingUseCase`, `GamificationSnippetCard` |
| **Entrada por voz** | Captura de ideias via microfone em português | `SpeechRecognizerHelper`, `VoiceRecordButton`, fallback typewriter |
| **Sugestões inteligentes** | Análise de texto para sugerir área e impacto | `NovaIdeiaViewModel.analyzeTextForAISuggestions()` — regras por keywords (não é ML externo) |
| **Radar de inovação aberta** | Descoberta de startups parceiras com score de compatibilidade | `RadarScreen`, `MockApiInterceptor`, `StartupPartner.matchScore` |
| **Dashboard executivo com IA** | Narrativa automática baseada em KPIs | `LeaderDashboardViewModel.generateAINarrative()` |
| **Indicador de similaridade** | Badge "similar" em ideias com flag `temSimilaridade` | `AIChip` em `IdeiaCard` |
| **UI premium** | Design system customizado, glass cards, animações, pull-to-refresh | Theme InovaGAB, componentes em `presentation/components/` |

> **Transparência:** as funcionalidades marcadas como "IA" utilizam lógica local baseada em regras e templates, não modelos de linguagem externos. A infraestrutura Retrofit está preparada para integração com serviços de IA no Sprint 2.

---

## 8. Roadmap Sprint 2

| Item | Descrição | Prioridade |
|------|-----------|------------|
| **Backend REST** | API Java/C# com endpoints CRUD completos | Alta |
| **Autenticação JWT** | Substituir auth local por tokens com refresh | Alta |
| **Sincronização** | Sync Room ↔ Backend (ideias, projetos, orientações) | Alta |
| **Remover mock** | Substituir `MockApiInterceptor` por API real de startups | Média |
| **Notificações push** | Implementar `NotificacoesScreen` com FCM | Média |
| **IA real** | Integração com LLM para sugestões e detecção de similaridade | Média |
| **Observabilidade** | Logs estruturados, métricas e crash reporting | Média |
| **Segurança avançada** | bcrypt/Argon2, certificate pinning, ProGuard | Alta |
| **APK release** | Build assinado para distribuição | Alta (Sprint 1 pendente) |
| **Testes instrumentados** | Espresso/Compose UI tests | Baixa |

### Entregáveis Sprint 1 pendentes

- [ ] APK release assinado
- [ ] Vídeo demonstrativo
- [ ] Documentação técnica (.PDF ou .PPT) — *este documento atende parcialmente; exportar para PDF para entrega formal*

---

*Documento gerado para entrega Sprint 1 — Challenge FIAP 2026.*
