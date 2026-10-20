# Levantamento de Requisitos — Sprint 2 | InovaGAB

**Plataforma de Inovação Corporativa | Challenge FIAP 2026 — Grupo Águia Branca**  
**Fase 6 — DevOps > Challenge — Sprint 2**

> Documento complementar a [`REQUISITOS.md`](./REQUISITOS.md) (Sprint 1).  
> Objetivo: organizar o contexto de produto e implementação para a transição **offline/mock → backend real + integração mobile + IA**.

---

## 1. Contexto e Objetivo da Sprint

### 1.1 O que a Sprint 1 entregou

O app Android **InovaGAB** já implementa os três pilares (Capturar → Estruturar → Acompanhar) com:

- Três perfis (Operador, Gestor, Líder) e RBAC local
- CRUD de orientações, ideias e projetos
- Dashboard executivo, gamificação e (Sprint 1) radar de startups — o Radar foi removido na Sprint 2
- Persistência **offline-first** (Room) e cliente HTTP preparado (Retrofit + mock)

### 1.2 Objetivo da Sprint 2

Trazer o protótipo da Sprint 1 para uma **plataforma funcional com backend real**, sem mocks de domínio:

1. Desenvolver APIs REST completas
2. Integrar o app nativo ao backend
3. Implementar restrições de nível de acesso no servidor (JWT + RBAC)
4. Consumir serviços externos
5. Integrar IA (diferencial / plus)

**Propósito de negócio:** plataforma robusta para otimizar **gestão do conhecimento** e **gestão de projetos de inovação** no Grupo Águia Branca.

### 1.3 Hipótese de valor (produto)

| Persona | Dor | Valor esperado na Sprint 2 |
|---------|-----|----------------------------|
| Operador | Ideias ficam só no dispositivo | Ideias sincronizadas, rastreáveis e alinhadas à estratégia vigente |
| Gestor | Priorização manual e local | Fila centralizada, **scoring IA-A (Gemini)** e projetos com dados reais |
| Líder | Visão fragmentada | Dashboard com métricas consolidadas via API + histórico de estratégia |

---

## 1.4 Matriz de auditoria (enunciado × documento)

Última revisão: alinhamento com as imagens do enunciado Sprint 2 e decisões de produto §5.1.

| Bloco do enunciado | IDs / seções | Aderência | Observação |
|--------------------|--------------|:---------:|------------|
| APIs completas + backend real | S2-01, §4, §8 | ✅ | Stack fixada em **Java Spring Boot** (§3) |
| MongoDB / NoSQL | S2-02, S2-RNF-05 | ✅ | Spring Data MongoDB (não JPA/Hibernate relacional) |
| Integração app Sprint 1 sem mocks | S2-09, §4.7 | ✅ | Radar de startups removido (era o único mock) |
| JWT, criptografia, segurança | S2-AUTH-*, S2-RNF-* | ✅ | — |
| RBAC por perfil | S2-04, §5 | ✅ | D1: aprovação só Gestor |
| Consumo de serviços externos | S2-EXT-* | ✅ | Gemini no backend (IA + serviço externo) |
| Orientações: CRUD Líder, leitura demais; histórico id/data/categoria/campanha | S2-ORI-* | ✅ | D3: soft-delete |
| Ideias: CRUD Operador; Gestor consulta/prioriza/aprova; vínculo estratégia | S2-IDE-* | ✅ | D2: Gestor vê todas com filtros |
| Projetos: CRUD Gestor; Líder consulta etapa/status/investimento/prazo/retorno | S2-PRO-* | ✅ | — |
| Dashboard Líder: ROI, lucro, prazos, investimento, produtividade; gráficos via API | S2-DASH-* | ✅ | Funil/by-area = melhoria opcional |
| IA (plus): ≥1 opção | S2-IA-01..08 (IA-A + IA-C) | ✅ | Scoring de ideias e relatório executivo com Gemini |
| Entregáveis E1–E6 + critérios de avaliação | §10 | ✅ | E5 exige payloads/responses de exemplo |
| Gamificação no servidor (Economia de Inovação GAB) | S2-GAM-*, §4.9, §8.2 | ✅ | Melhoria opcional entregue: XP, Asas, loja e resgates reais |
| FCM / sync offline completo | §2.4 | ⚪ | Fora do enunciado mínimo (não bloqueia nota) |

Legenda: ✅ atende | ⚪ escopo explícito reduzido | ❌ gap (nenhum pendente após esta revisão).

---

## 2. Escopo e Fora de Escopo

### 2.1 Em escopo (obrigatório)

| # | Item | Descrição |
|---|------|-----------|
| S2-01 | Backend REST | **Java 17+**, Spring Boot 3, Spring Security, Spring Data MongoDB |
| S2-02 | Banco NoSQL | **MongoDB** |
| S2-EXT | Serviços externos | Backend consome **Google Gemini** para IA-A e IA-C; credenciais via env |
| S2-03 | Auth + JWT | Login, tokens, criptografia de senha, sessões seguras |
| S2-04 | RBAC | Toda operação respeita o perfil (Operador / Gestor / Líder) |
| S2-05 | Orientações estratégicas | CRUD Líder; leitura demais perfis; histórico (id, data, categoria, campanha) |
| S2-06 | Ideias / problemas | CRUD Operador (próprias); Gestor consulta, prioriza e aprova; vínculo com estratégia |
| S2-07 | Projetos / iniciativas | CRUD Gestor; Líder consulta progresso (etapa, status, investimento, prazo, retorno) |
| S2-08 | Dashboard | Endpoints de relatório/resumo; app consome e exibe gráficos |
| S2-09 | Integração mobile | App Sprint 1 consome as APIs; **sem mocks** para fluxos de domínio |
| S2-10 | Documentação | Código, README, especificação de endpoints, apresentação |

### 2.2 Diferencial (plus — pelo menos 1)

| Opção | Nome | Descrição |
|-------|------|-----------|
| IA-A | Scoring & Priorização | IA pontua/prioriza ideias para apoiar seleção de projetos |
| IA-B | Assistente generativo | Chat integrado para gestores escolherem ideias e registrarem projetos |
| IA-C | Geração de insights | Análise e sugestões para liderança a partir dos dados do dashboard |

APIs gratuitas sugeridas pelo enunciado: **Google Gemini**, **OpenRouter**, **GitHub Models**.

### 2.3 Melhorias opcionais (não exigidas pelo enunciado)

- Endpoints extras de dashboard (`/funnel`, `/by-area`) se o app já os consumir com dados locais
- Persistir gamificação no backend — **entregue** como Economia de Inovação GAB (§4.9)
- Radar de startups via API própria (S2-MOB-06)
- Refresh token / blacklist de JWT (S2-AUTH-07)
- Observabilidade além de logs básicos (S2-RNF-08)

### 2.4 Fora de escopo (explícito para não inflar a entrega)

- App iOS nativo (APK Android atende; `.ipa` só se o time optar)
- Opção de IA **IA-B** (chat/assistente) — IA-A e IA-C foram entregues
- Multi-tenant / múltiplas empresas
- Notificações push (FCM)
- Sincronização bidirecional Room ↔ API com resolução de conflitos
- Observabilidade enterprise (APM completo)

---

## 3. Stack e decisões de engenharia (Backend)

**Decisão do time:** backend em **Java**, conforme enunciado (Spring Boot, Spring Security).  
**Persistência:** MongoDB via **Spring Data MongoDB** — o enunciado menciona JPA/Hibernate no contexto Java/SQL; com NoSQL o mapeamento oficial é documentos `@Document`, repositórios `MongoRepository`, sem Hibernate ORM relacional.

| Camada | Tecnologia |
|--------|------------|
| Runtime | Java 17+ |
| Framework | Spring Boot 3 |
| Segurança | Spring Security + JWT (Bearer) |
| Persistência | Spring Data MongoDB |
| Validação | Jakarta Bean Validation |
| Contrato API | OpenAPI 3 / Springdoc (Swagger UI) |
| IA (plus) | Cliente HTTP para **Google Gemini API** (serviço externo) |

### 3.1 Decisão do time (registrada)

| Campo | Valor |
|-------|--------|
| Stack escolhida | **Java Spring Boot 3** |
| Banco | **MongoDB** |
| Provedor de IA (plus) | **Google Gemini** |
| Opções de IA | **IA-A + IA-C** — Priorização de ideias e relatório executivo |
| Base URL (dev) | `http://localhost:8080/api/v1` |
| Exclusão lógica (D3) | Campo `ativo` / `deletedAt` em entidades de domínio |

---

## 4. Requisitos Funcionais — Backend

Status: implementação concluída e validada no módulo [`backend/`](../backend/).  
IDs prefixados com `S2-` para não colidir com a Sprint 1.

### 4.1 Autenticação e autorização

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-AUTH-01 | Login com email e senha retorna JWT de acesso | Token válido; resposta inclui perfil e dados básicos do usuário | ✅ |
| S2-AUTH-02 | Senhas armazenadas com hash seguro (bcrypt/Argon2 ou equivalente) | Não existe senha em texto plano no banco | ✅ |
| S2-AUTH-03 | Três perfis: Operador, Gestor, Líder | Enum/role no token e no documento do usuário | ✅ |
| S2-AUTH-04 | Endpoints protegidos exigem Bearer JWT | 401 sem token; 403 se perfil insuficiente | ✅ |
| S2-AUTH-05 | RBAC em todas as operações de domínio | Matriz §5 respeitada no servidor (não só no app) | ✅ |
| S2-AUTH-06 | Seed de usuários demo para avaliação | Mesmos (ou mapeados) usuários da Sprint 1 documentados no README | ✅ |
| S2-AUTH-07 | Logout / invalidação (opcional: blacklist ou refresh curto) | Documentar estratégia adotada | ✅ |

### 4.2 Orientações sobre a estratégia da empresa

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-ORI-01 | Líder: CRUD completo de orientações | Criar, listar, detalhar, atualizar; exclusão = **soft-delete** (D3) | ✅ |
| S2-ORI-02 | Operador e Gestor: somente leitura | Tentativa de escrita → 403 | ✅ |
| S2-ORI-03 | Histórico com ID, data, categoria e campanha | Campos persistidos e retornados na API | ✅ |
| S2-ORI-04 | Listagem de orientações ativas/vigentes | Filtro por status/ativo disponível | ✅ |
| S2-ORI-05 | Ideias e projetos referenciam orientação vigente | FK/referência NoSQL validada na criação | ✅ |

**Modelo mínimo sugerido**

```
Orientacao {
  id, titulo, descricao, categoria, campanha,
  dataCriacao, dataAtualizacao, ativo, criadoPorUserId
}
```

### 4.3 Ideias / problemas enfrentados pelos operadores

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-IDE-01 | Operador: CRUD das **próprias** ideias | Create/Read/Update/Delete apenas do autor | ✅ |
| S2-IDE-02 | Operador não altera ideias de terceiros | 403 ou 404 conforme política | ✅ |
| S2-IDE-03 | Gestor: consultar **todas** as ideias (filtros opcionais) | Query params: `status`, `area`, `orientacaoId`; paginação recomendada | ✅ |
| S2-IDE-04 | Gestor: priorizar ideias | Campo prioridade/score atualizável pelo gestor | ✅ |
| S2-IDE-05 | **Somente Gestor:** aprovar / reprovar ideias | Líder recebe 403; feedback obrigatório na reprovação | ✅ |
| S2-IDE-06 | Ideia vinculada à estratégia atual | `orientacaoId` obrigatório (ou validado contra ativa) | ✅ |
| S2-IDE-07 | Fluxo ideia aprovada → projeto | Endpoint ou regra que permita conversão (mantém rastreio `ideiaId`) | ✅ |

**Alinhamento com Sprint 1 (app):** manter status compatíveis (`PENDENTE`, `APROVADA`, `REPROVADA`, `CONVERTIDA_PROJETO`, etc.) para minimizar refactor de UI.

### 4.4 Projetos e iniciativas

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-PRO-01 | Gestor: CRUD de projetos/iniciativas | Cadastro, edição, exclusão (ou arquivamento) | ✅ |
| S2-PRO-02 | Gestor: atualizar progresso e resultados | Campos de etapa, % progresso, resultados obtidos | ✅ |
| S2-PRO-03 | Projeto vinculado à estratégia | `orientacaoId` (e opcionalmente `ideiaId`) | ✅ |
| S2-PRO-04 | Campos: etapa, status, investimento, prazo, retorno financeiro | Presentes no payload e persistidos | ✅ |
| S2-PRO-05 | Líder: consulta de progresso (somente leitura) | GET list/detail; sem mutação | ✅ |
| S2-PRO-06 | ROI calculado (servidor ou contrato documentado) | Consistente com o app (`roi = f(investimento, retorno)`) | ✅ |

### 4.5 Dashboard e relatórios

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-DASH-01 | Líder consulta resumo estruturado de resultados | Endpoint(s) de summary agregados | ✅ |
| S2-DASH-02 | Retornos por estratégia e/ou por projeto | Filtros `orientacaoId` / `projetoId` | ✅ |
| S2-DASH-03 | Resumo geral: ROI, lucro, prazos, investimento, ganhos de produtividade | Campos numéricos documentados | ✅ |
| S2-DASH-04 | App exibe gráficos a partir dos dados da API | Sem hardcode de KPIs no cliente para o fluxo líder (Home e Resumo Executivo usam `GET /summary`) | ✅ |

**Sugestão de endpoints de relatório**

```
GET /api/v1/dashboard/summary
GET /api/v1/dashboard/by-strategy/{orientacaoId}
GET /api/v1/dashboard/by-project/{projetoId}
GET /api/v1/dashboard/funnel          # opcional — funil de inovação
GET /api/v1/dashboard/by-area         # opcional — desempenho por área
```

### 4.6 Diferencial — Inteligência Artificial (IA-A + IA-C — decisão D5)

**Escopo entregue:** **IA-A** (scoring/priorização) e **IA-C** (relatório executivo do dashboard). Apenas IA-B permanece fora do escopo (§2.4).

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-IA-01 | `POST /api/v1/ai/score-ideas` (Gestor) | Recebe lista de ideias (ou ids) + contexto da orientação estratégica; retorna score 0–100, justificativa curta e ranking sugerido | ✅ |
| S2-IA-02 | Integração **Google Gemini** no backend | Chamada server-side; `GEMINI_API_KEY` em variável de ambiente | ✅ |
| S2-IA-03 | Decisão humana preservada | Score da IA é **recomendação**; aprovação/reprovação continua em `PATCH .../avaliacao` pelo Gestor | ✅ |
| S2-IA-04 | Falha degradada | Se Gemini indisponível: 503 ou resposta com flag `fallback=true` e regra local documentada | ✅ |
| S2-IA-05 | Documentação de IA (E6) | Modelo, prompt, limites, custo/zero-cost tier, exemplo request/response | ✅ |
| S2-IA-06 | `GET /api/v1/ai/dashboard-narrative` (Líder) | Gera título, resumo, evidências numéricas e ação recomendada a partir dos KPIs | ✅ |
| S2-IA-07 | Relatório filtrável por estratégia | `orientacaoId` opcional aplica o mesmo filtro ao summary e à narrativa | ✅ |
| S2-IA-08 | Narrativa degradada | Regras locais analisam ideias, atrasos, ROI e conversão com `fallback=true` | ✅ |

**Racional de produto:** IA-A apoia a seleção de projetos pelo Gestor; IA-C transforma os resultados do portfólio em insight acionável para o Líder. Ambas preservam rastreabilidade, utilizam dados reais e mantêm fallback funcional.

### 4.7 Integração com o app mobile (Sprint 1 → Sprint 2)

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-MOB-01 | Remover mocks de domínio nos fluxos oficiais | Auth, Orientações, Ideias, Projetos, Dashboard via REST; API = fonte da verdade (D4) | ✅ |
| S2-MOB-02 | JWT persistido com **EncryptedSharedPreferences** ou Keystore | DataStore sozinho não criptografa o token; sessão sobrevive a restart; logout limpa credenciais | ✅ |
| S2-MOB-03 | Repositórios passam a usar Retrofit real | `NetworkModule` aponta para base URL configurável | ✅ |
| S2-MOB-04 | Tratamento de erros HTTP (401 → login; 403 → mensagem) | UX clara, sem crash | ✅ |
| S2-MOB-05 | Features Sprint 1 mantidas (gamificação, voz, etc.) | Gamificação passou a vir do servidor (S2-GAM-*), sem cálculo nem dado simulado no app; Room como **cache de leitura** opcional (D4) | ✅ |
| S2-MOB-07 | Login e mutações exigem rede | Sem create/update/delete offline na S2; mensagem clara se offline | ✅ |
| S2-MOB-06 | Radar startups: mock → API real **ou** endpoint próprio no backend | Feature **removida** na Sprint 2 (mock expurgado; fora do enunciado mínimo) | ⚪ |

### 4.8 Serviços externos (enunciado)

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-EXT-01 | Backend consome pelo menos um serviço externo | Gemini para S2-IA-*; não expor API key no app mobile | ✅ |
| S2-EXT-02 | Radar / startups (opcional) | Fora do escopo: Radar removido do app | ⚪ |

### 4.9 Gamificação — Economia de Inovação GAB (melhoria opcional entregue)

Regras completas, valores e racional em [`GAMIFICACAO.md`](./GAMIFICACAO.md). Contexto da marca em [`MARCA_GAB.md`](./MARCA_GAB.md).

**Objetivo de produto:** a pontuação recompensa o que gera valor para o grupo (ideia aprovada, ideia que vira projeto, projeto entregue no prazo e com retorno), e o colaborador troca o que ganhou por benefícios reais dos negócios do grupo.

| ID | Requisito | Critério de aceite | Status |
|----|-----------|--------------------|--------|
| S2-GAM-01 | Ledger de pontos no servidor | Coleção `pontos_eventos`; chave única `tipo:refId:userId` impede pontuar duas vezes a mesma ação | ✅ |
| S2-GAM-02 | Duas moedas | **XP** (reputação: nível vitalício e ranking da temporada) e **Asas** (saldo resgatável); saldo = soma do ledger | ✅ |
| S2-GAM-03 | Pontuação por resultado | Ideia enviada vale pouco (2 XP, até 3/dia); aprovação, conversão e conclusão valem mais; ideia reprovada vale 0 | ✅ |
| S2-GAM-04 | Pontos para os três perfis | Operador ganha pelas ideias; Gestor pela avaliação no prazo, feedback útil e projetos entregues; Líder pelo engajamento e resultado das suas orientações | ✅ |
| S2-GAM-05 | Multiplicadores estratégicos | x1,5 em campanha patrocinada ativa e x1,5 em orientação de Segurança (acumulam) | ✅ |
| S2-GAM-06 | Estorno | Excluir ideia ou excluir projeto gera `ESTORNO` no extrato; o evento original fica marcado `estornado` | ✅ |
| S2-GAM-07 | Antitrapaça | Limite diário, ideia duplicada (similaridade de título) não pontua, autor não apoia a própria ideia, 1 apoio por pessoa | ✅ |
| S2-GAM-08 | Temporada trimestral | Ranking por XP da temporada (`2026-T4`); dias restantes no painel; zona de prêmio = top 3 por divisão com XP &gt; 0; Líder exporta a lista em `GET /gamificacao/indicacoes-inovagab` | ✅ |
| S2-GAM-09 | Ranking sem dados simulados | Ordena todos os usuários do perfil, inclusive com 0 XP; a API pagina (`pagina`, `tamanho` padrão 50, máximo 200) e devolve `totalParticipantes` e `minhaEntrada`; Operador vê Operadores, Gestor vê Gestores e Operadores, Líder vê todos | ✅ |
| S2-GAM-10 | Gincana de equipes | `GET /gamificacao/ranking/equipes` ordena equipes por XP médio por pessoa | ✅ |
| S2-GAM-11 | Missões semanais e sequência | Missões calculadas no servidor por perfil; prêmio creditado uma vez por semana; sequência em semanas (público de turnos) | ✅ |
| S2-GAM-12 | Conquistas com tiers reais | Bronze 1, Prata 3, Ouro 10, Platina 25; título, progresso, próxima meta e data vêm da API | ✅ |
| S2-GAM-13 | Loja de recompensas | Catálogo mantido pelo Líder (custo, estoque, nível mínimo, ativo); a API informa `podeResgatar` e `motivoBloqueio` | ✅ |
| S2-GAM-14 | Fluxo de resgate | Pedido válido (saldo, estoque, nível) entra `APROVADO`; `APROVADO → ENTREGUE` (outras transições: 409); debita Asas e reserva estoque ao solicitar; sem negativa discricionária | ✅ |
| S2-GAM-15 | Campanha patrocinada | Líder define prazo, bônus de Asas e top N na orientação; ao encerrar, os N autores com mais XP na campanha recebem o bônus (uma vez) | ✅ |
| S2-GAM-16 | Feedback útil e apoio | Operador autor marca a avaliação como útil (pontua o Gestor avaliador); Gestor e Líder apoiam ideias de outros | ✅ |
| S2-GAM-17 | Meu impacto | Painel e carteira mostram ideias, aprovações, projetos e retorno mensal gerado, para a avaliação de desempenho | ✅ |
| S2-GAM-18 | App consome a API | Telas Loja, Carteira (extrato), Gestão de recompensas (Líder), card do Gestor, missões, temporada e gincana no Ranking; nenhum número simulado | ✅ |

---

## 5. Matriz RBAC (servidor)

| Recurso / Ação | Operador | Gestor | Líder |
|----------------|:--------:|:------:|:-----:|
| Login / obter JWT | ✅ | ✅ | ✅ |
| Consultar orientações | ✅ | ✅ | ✅ |
| CRUD orientações | ❌ | ❌ | ✅ |
| CRUD próprias ideias | ✅ | ❌ | ❌ |
| Listar ideias | Próprias | Todas (filtros) | Todas (leitura) |
| Priorizar / aprovar ideias | ❌ | ✅ | ❌ |
| Chamar scoring IA (IA-A) | ❌ | ✅ | ❌ |
| Gerar relatório executivo IA (IA-C) | ❌ | ❌ | ✅ |
| CRUD projetos | ❌ | ✅ | ❌ (somente leitura) |
| Consultar projetos / progresso | ❌ | ✅ | ✅ |
| Dashboard executivo / reports | ❌ | ❌ | ✅ |
| Painel, extrato e meus resgates (gamificação) | ✅ | ✅ | ✅ |
| Ranking da temporada | Aba Operadores | Abas Gestores e Operadores | Todas as abas |
| Resgatar recompensa | ✅ | ✅ | ❌ |
| Manter catálogo e marcar entregas | ❌ | ❌ | ✅ (não o próprio) |
| Apoiar ideia | ❌ | ✅ (de outros) | ✅ (de outros) |
| Marcar feedback como útil | ✅ (autor) | ❌ | ❌ |
| Encerrar campanha patrocinada | ❌ | ❌ | ✅ |

**Ajuste no app (Sprint 1 → S2):** remover ou desabilitar avaliação de ideias pelo perfil Líder na UI, alinhando à decisão D1 e ao enunciado (“gestores … aprovam”).

### 5.1 Decisões de produto (registradas)

| # | Pergunta | Decisão | Racional |
|---|----------|---------|----------|
| D1 | Líder pode aprovar ideias? | **Não — somente Gestor** | Enunciado atribui aprovação ao gestor; Líder foca estratégia e resultados |
| D2 | Escopo do Gestor nas ideias | **Todas, com filtros** | Priorização transversal; MVP simples (sem regra extra por área no servidor) |
| D3 | Soft-delete vs hard-delete | **Soft-delete** | Histórico, auditoria e vínculos ideia→projeto; listagens padrão excluem inativos |
| D4 | Offline | **API fonte oficial; Room cache leitura; mutações online** | Atende integração real sem sync bidirecional nesta sprint |
| D5 | Qual opção de IA | **IA-A + IA-C com Google Gemini** | Scoring para o Gestor e relatório executivo para o Líder; ambos com fallback |

---

## 6. Requisitos Não-Funcionais — Sprint 2

| ID | Categoria | Requisito |
|----|-----------|-----------|
| S2-RNF-01 | Segurança | HTTPS em ambientes expostos; CORS restrito ao necessário |
| S2-RNF-02 | Segurança | Validação de input (Jakarta Bean Validation) |
| S2-RNF-03 | Arquitetura | Backend organizado por camadas/módulos (Controller → Service → Repository) |
| S2-RNF-04 | API | Versionamento (`/api/v1`); contratos JSON estáveis documentados |
| S2-RNF-05 | Dados | MongoDB com índices em campos de filtro (userId, status, orientacaoId) |
| S2-RNF-06 | Operação | README com como subir API + DB (Docker Compose recomendado) |
| S2-RNF-07 | Qualidade | Testes automatizados mínimos nos serviços críticos (auth, RBAC, CRUD) |
| S2-RNF-08 | Observabilidade | Logs estruturados de request/erro (nível básico) |
| S2-RNF-09 | Integração | App e API com contratos alinhados (DTOs ↔ models Kotlin) |
| S2-RNF-10 | Config | Secrets via variáveis de ambiente (JWT secret, Mongo URI, API key IA) |

---

## 7. Arquitetura-alvo (visão)

```
┌──────────────────────┐         HTTPS / JWT          ┌────────────────────────────┐
│  InovaGAB (Android)  │ ───────────────────────────► │  Backend REST (Java)        │
│  Compose + MVVM      │                              │  Controller → Service →     │
│  Retrofit (real)     │ ◄─────────────────────────── │  Repository (Spring Boot 3) │
│  Room (cache leitura)│         JSON responses       │  Spring Security + JWT      │
└──────────────────────┘                              └─────────────┬──────────────┘
                                                                    │
                                                      ┌─────────────▼──────────────┐
                                                      │  MongoDB (NoSQL)           │
                                                      │  users, orientacoes,       │
                                                      │  ideias, projetos, ...     │
                                                      └────────────────────────────┘
                                                                    │
                                                      ┌─────────────▼──────────────┐
                                                      │  Serviço externo de IA     │
                                                      │  Gemini / OpenRouter / etc │
                                                      └────────────────────────────┘
```

### 7.1 Evolução da estratégia de dados (produto + engenharia)

| Fase | Estratégia | Quando |
|------|------------|--------|
| Sprint 1 | Room = fonte da verdade; Retrofit mock | Entregue |
| Sprint 2 (adotado — D4) | API = fonte da verdade; login e mutações exigem rede; Room cache de leitura opcional | Obrigatório |
| Sprint 2 (opcional) | Pré-carregar listas no Room após fetch para UX offline de consulta | Se houver tempo |
| Pós-sprint | Sync bidirecional completo + conflitos | Fora do escopo mínimo |

---

## 8. Mapa de endpoints (rascunho para especificação)

> Entregável formal: documento de rotas, métodos, payloads e responses.  
> Este mapa é o backlog inicial de contrato.

### Auth
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| POST | `/api/v1/auth/login` | público | Login → JWT |
| GET | `/api/v1/auth/me` | autenticado | Usuário atual |

### Orientações
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| GET | `/api/v1/orientacoes` | todos | Listar |
| GET | `/api/v1/orientacoes/{id}` | todos | Detalhe |
| POST | `/api/v1/orientacoes` | Líder | Criar |
| PUT | `/api/v1/orientacoes/{id}` | Líder | Atualizar |
| PUT | `/api/v1/orientacoes/{id}/ativo` | Líder | Ativar/desativar |
| DELETE | `/api/v1/orientacoes/{id}` | Líder | Remover |
| POST | `/api/v1/orientacoes/{id}/campanha/encerrar` | Líder | Encerra a campanha e paga o bônus aos top N (S2-GAM-15) |

### Ideias
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| GET | `/api/v1/ideias` | conforme RBAC | Listar (filtros) |
| GET | `/api/v1/ideias/{id}` | conforme RBAC | Detalhe |
| POST | `/api/v1/ideias` | Operador | Criar |
| PUT | `/api/v1/ideias/{id}` | Operador (própria) | Atualizar |
| DELETE | `/api/v1/ideias/{id}` | Operador (própria) | Remover |
| PATCH | `/api/v1/ideias/{id}/prioridade` | Gestor | Priorizar |
| PATCH | `/api/v1/ideias/{id}/avaliacao` | Gestor | Aprovar/reprovar |
| POST | `/api/v1/ideias/{id}/apoio` | Gestor, Líder | Apoiar ideia de outra pessoa (1 por usuário) |
| POST | `/api/v1/ideias/{id}/feedback-util` | Operador (autor) | Avaliar se o feedback do Gestor foi útil |
| POST | `/api/v1/ai/score-ideas` | Gestor | Scoring IA-A (Gemini) |
| GET | `/api/v1/ai/dashboard-narrative` | Líder | Narrativa executiva (IA extra) |

### Projetos
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| GET | `/api/v1/projetos` | Gestor, Líder | Listar |
| GET | `/api/v1/projetos/{id}` | Gestor, Líder | Detalhe |
| POST | `/api/v1/projetos` | Gestor | Criar |
| PUT | `/api/v1/projetos/{id}` | Gestor | Atualizar |
| DELETE | `/api/v1/projetos/{id}` | Gestor | Remover |
| PATCH | `/api/v1/projetos/{id}/progresso` | Gestor | Progresso/resultados |

### Dashboard
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| GET | `/api/v1/dashboard/summary` | Líder | Resumo geral + séries (funil e área) |
| GET | `/api/v1/dashboard/funnel` | Líder | Funil de inovação |
| GET | `/api/v1/dashboard/by-area` | Líder | Desempenho por área |
| GET | `/api/v1/dashboard/by-strategy/{id}` | Líder | Retornos por estratégia |
| GET | `/api/v1/dashboard/by-project/{id}` | Líder | Retornos por projeto |

### Gamificação
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| GET | `/api/v1/gamificacao/me` | todos | Painel: XP, Asas, nível, temporada, posição, conquistas, missões, impacto, novidades |
| GET | `/api/v1/gamificacao/extrato` | todos | Extrato completo do ledger (inclui estornos) |
| POST | `/api/v1/gamificacao/novidades/vistas` | todos | Marca as novidades como vistas (204) |
| GET | `/api/v1/gamificacao/ranking?perfil=&escopo=DIVISAO\|GRUPO&temporada=&pagina=&tamanho=` | conforme §5 | Ranking da temporada (página; total em `totalParticipantes`) |
| GET | `/api/v1/gamificacao/ranking/equipes?escopo=DIVISAO\|GRUPO` | todos | Gincana de equipes por XP médio |
| GET | `/api/v1/gamificacao/indicacoes-inovagab?temporada=` | Líder | Top 3 operadores por divisão (XP &gt; 0) para o Imova-se |

### Recompensas e resgates
| Método | Rota | Perfis | Descrição |
|--------|------|--------|-----------|
| GET | `/api/v1/recompensas?incluirInativas=` | todos | Catálogo com `podeResgatar` e `motivoBloqueio` para quem consulta; inativas só para o Líder |
| POST | `/api/v1/recompensas` | Líder | Criar item (201) |
| PUT | `/api/v1/recompensas/{id}` | Líder | Atualizar item (custo, estoque, nível mínimo, ativo) |
| POST | `/api/v1/recompensas/{id}/resgatar` | Operador, Gestor | Solicitar resgate (201) |
| GET | `/api/v1/resgates/meus` | todos | Resgates do usuário |
| GET | `/api/v1/resgates?status=` | Líder | Lista de resgates (filtro opcional) |
| PUT | `/api/v1/resgates/{id}/status` | Líder | Marcar `ENTREGUE` (`NEGADO` = 409) |

### 8.1 Exemplos de contrato (entregável E5 — amostra)

**Login** — `POST /api/v1/auth/login`

```json
// Request
{ "email": "ana.oliveira@aguiabranca.com.br", "senha": "123456" }

// Response 200
{
  "accessToken": "eyJhbGciOiJIUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "usuario": { "id": "...", "nome": "Ana Oliveira", "perfil": "GESTOR", "area": "Logística" }
}
```

**Scoring IA-A** — `POST /api/v1/ai/score-ideas` (Gestor)

```json
// Request
{ "orientacaoId": "64f...", "ideiaIds": ["64a...", "64b..."] }

// Response 200
{
  "ranking": [
    { "ideiaId": "64a...", "score": 87, "justificativa": "Alto alinhamento com campanha X..." },
    { "ideiaId": "64b...", "score": 62, "justificativa": "Impacto moderado..." }
  ],
  "modelo": "gemini-3.6-flash",
  "fallback": false
}
```

**Dashboard summary** — `GET /api/v1/dashboard/summary` (Líder)

```json
// Response 200
{
  "roiMedio": 35.00,
  "lucroTotal": 28000.00,
  "investimentoTotal": 80000.00,
  "retornoTotal": 9000.00,
  "projetosNoPrazo": 1,
  "projetosAtrasados": 0,
  "ganhoProdutividadePercentual": 0.0,
  "totalIdeias": 5,
  "ideiasAprovadas": 1,
  "ideiasEmProjeto": 1,
  "projetosAtivos": 1,
  "tempoMedioAprovacaoDias": 7,
  "funil": [
    { "etapa": "IDEIAS", "label": "Ideias", "quantidade": 5 },
    { "etapa": "APROVADAS", "label": "Aprovadas", "quantidade": 1 },
    { "etapa": "EM_PROJETO", "label": "Em Projeto", "quantidade": 1 }
  ],
  "desempenhoPorArea": [
    { "area": "OPERACOES", "label": "Operações", "quantidade": 4, "percentual": 80.0 }
  ]
}
```

Documento formal E5 deve listar **todos** os endpoints com códigos de erro (400, 401, 403, 404) e schemas OpenAPI gerados pelo backend.

### 8.2 Exemplos de contrato — Gamificação (S2-GAM-*)

Respostas capturadas da API local com os dados de demonstração (Pedro Santos, Operador, Logística). Listas encurtadas com `…`. Erros seguem o formato padrão `{ "error": "mensagem", "status": 409 }`.

**Painel** — `GET /api/v1/gamificacao/me`

```json
// Response 200
{
  "userId": "user-operador",
  "nome": "Pedro Santos",
  "perfil": "OPERADOR",
  "divisao": "LOGISTICA",
  "equipe": "Transportadora Cariacica",
  "xpTotal": 70,
  "xpTemporada": 70,
  "asas": 0,
  "nivel": {
    "codigo": "EM_ASCENSAO", "label": "Em Ascensão", "xpMinimo": 50,
    "proximoCodigo": "ENGAJADO", "proximoLabel": "Engajado", "xpProximo": 150,
    "xpParaProximo": 80, "progresso": 0.2
  },
  "temporada": { "id": "2026-T4", "label": "4º trimestre de 2026", "inicio": "2026-10-01", "fim": "2026-12-31", "diasRestantes": 73 },
  "posicaoRanking": 2,
  "totalRanking": 4,
  "sequenciaSemanas": 3,
  "conquistas": [
    {
      "tipo": "PRIMEIRA_IDEIA", "titulo": "Voz do campo", "descricao": "Ideias enviadas que pontuaram",
      "valor": 5, "tier": "PRATA", "desbloqueada": true, "proximaMeta": 10, "progresso": 0.5,
      "desbloqueadaEm": "2026-10-04T18:18:39.398Z"
    },
    {
      "tipo": "GUARDIAO_SEGURANCA", "titulo": "Guardião da segurança", "descricao": "Ideias de segurança aprovadas",
      "valor": 0, "tier": null, "desbloqueada": false, "proximaMeta": 1, "progresso": 0.0, "desbloqueadaEm": null
    }
  ],
  "missoes": [
    {
      "id": "seguranca-semana", "titulo": "Olhar de segurança",
      "descricao": "Envie 1 ideia para uma orientação de Segurança",
      "progresso": 0, "meta": 1, "concluida": false, "recompensaXp": 10, "recompensaAsas": 5
    }
  ],
  "impacto": {
    "ideiasEnviadas": 5, "ideiasAprovadas": 1, "ideiasAvaliadas": 0, "ideiasAlinhadas": 0,
    "projetos": 1, "projetosConcluidos": 0, "retornoMensal": 9000
  },
  "novidades": [
    { "id": "6ad665b7…", "tipo": "RESGATE", "descricao": "Resgate: Trilha de capacitação", "xp": 0, "asas": -40, "criadoEm": "2026-10-19T18:47:19.518Z", "estornado": false }
  ]
}
```

`novidades` traz os eventos ainda não vistos. O app mostra o primeiro na Home e chama `POST /gamificacao/novidades/vistas` (204) ao abrir o extrato.

**Extrato** — `GET /api/v1/gamificacao/extrato`

```json
// Response 200
{
  "xpTotal": 70,
  "asas": 0,
  "eventos": [
    { "id": "6ad65f1e…cff", "tipo": "ESTORNO", "descricao": "Estorno: Resgate: Trilha de capacitação (negado pelo líder)", "xp": 0, "asas": 40, "criadoEm": "2026-10-19T18:19:10.664Z", "estornado": false },
    { "id": "6ad65f1e…cfe", "tipo": "RESGATE", "descricao": "Resgate: Trilha de capacitação", "xp": 0, "asas": -40, "criadoEm": "2026-10-19T18:19:10.603Z", "estornado": true },
    { "id": "…", "tipo": "IDEIA_APROVADA", "descricao": "Ideia aprovada: …", "xp": 20, "asas": 10, "criadoEm": "…", "estornado": false }
  ]
}
```

**Ranking da temporada** — `GET /api/v1/gamificacao/ranking?perfil=OPERADOR&escopo=DIVISAO`

```json
// Response 200
{
  "temporada": { "id": "2026-T4", "label": "4º trimestre de 2026", "inicio": "2026-10-01", "fim": "2026-12-31", "diasRestantes": 73 },
  "perfil": "OPERADOR",
  "escopo": "DIVISAO",
  "entradas": [
    { "posicao": 1, "usuarioId": "user-op-joana", "nome": "Joana Ribeiro", "perfil": "OPERADOR", "divisao": "LOGISTICA", "equipe": "CD Serra", "xpTemporada": 71, "nivel": "EM_ASCENSAO", "nivelLabel": "Em Ascensão", "zonaPremio": true, "voce": false },
    { "posicao": 2, "usuarioId": "user-operador", "nome": "Pedro Santos", "perfil": "OPERADOR", "divisao": "LOGISTICA", "equipe": "Transportadora Cariacica", "xpTemporada": 70, "nivel": "EM_ASCENSAO", "nivelLabel": "Em Ascensão", "zonaPremio": true, "voce": true }
  ],
  "minhaPosicao": 2,
  "minhaEntrada": { "posicao": 2, "usuarioId": "user-operador", "…": "…" },
  "totalParticipantes": 4,
  "regraPremio": "Os 3 primeiros de cada divisão ao fim da temporada são indicados ao Prêmio InovaGAB (categoria Imova-se)."
}

// Response 403 (Operador pedindo a aba de Gestores)
{ "error": "Ranking não disponível para o seu perfil", "status": 403 }
```

**Gincana de equipes** — `GET /api/v1/gamificacao/ranking/equipes`

```json
// Response 200
[
  { "posicao": 1, "equipe": "CD Serra", "divisao": "LOGISTICA", "membros": 2, "xpTotal": 96, "xpMedio": 48, "minhaEquipe": false },
  { "posicao": 2, "equipe": "Transportadora Cariacica", "divisao": "LOGISTICA", "membros": 2, "xpTotal": 77, "xpMedio": 38, "minhaEquipe": true }
]
```

**Catálogo** — `GET /api/v1/recompensas`

```json
// Response 200 (visão do Operador com 0 Asas)
[
  {
    "id": "rec-trilha", "titulo": "Trilha de capacitação",
    "descricao": "Curso de liderança, segurança ou tecnologia na Universidade Corporativa.",
    "categoria": "CARREIRA", "parceiro": "Universidade Corporativa GAB",
    "custoAsas": 40, "estoque": 199, "nivelMinimo": "INICIANTE", "nivelMinimoLabel": "Iniciante",
    "ativo": true, "podeResgatar": false, "motivoBloqueio": "Faltam 40 Asas"
  }
]
```

`motivoBloqueio` pode ser `"Esgotado"`, `"Disponível a partir do nível …"` ou `"Faltam N Asas"`.

**Criar / atualizar item** — `POST /api/v1/recompensas` (201) e `PUT /api/v1/recompensas/{id}` (Líder)

```json
// Request
{
  "titulo": "Passagem rodoviária Águia Branca",
  "descricao": "Ida e volta para você e um acompanhante.",
  "categoria": "VIAGEM",
  "parceiro": "Viação Águia Branca",
  "custoAsas": 120,
  "estoque": 30,
  "nivelMinimo": "EM_ASCENSAO",
  "ativo": true
}
// Validação: custoAsas 1..10000, estoque 0..100000, titulo e categoria obrigatórios → 400
```

**Resgatar** — `POST /api/v1/recompensas/{id}/resgatar` (Operador, Gestor)

```json
// Response 201
{
  "id": "6ad665b77b3c8e68cf800f97", "recompensaId": "rec-trilha", "recompensaTitulo": "Trilha de capacitação",
  "userId": "user-operador", "userNome": "Pedro Santos", "custoAsas": 40, "status": "APROVADO",
  "observacao": null, "aprovadorUserId": null,
  "solicitadoEm": "2026-10-19T18:47:19.468Z", "decididoEm": "2026-10-19T18:47:19.468Z", "entregueEm": null
}

// Response 409 (saldo, estoque ou nível insuficiente; mesmo texto de motivoBloqueio)
{ "error": "Disponível a partir do nível Engajado", "status": 409 }
```

**Confirmar entrega** — `PUT /api/v1/resgates/{id}/status` (Líder)

```json
// Request
{ "status": "ENTREGUE" }

// Response 200
{ "id": "6ad665b7…", "status": "ENTREGUE", "aprovadorUserId": "user-lider", "decididoEm": "2026-10-19T18:47:19.468Z", "entregueEm": "2026-10-19T18:49:58.006Z", "…": "…" }

// Erros
// 403 { "error": "Não é possível decidir o próprio resgate", "status": 403 }
// 409 { "error": "Transição inválida: APROVADO → NEGADO", "status": 409 }
```

**Apoiar ideia** — `POST /api/v1/ideias/{id}/apoio` (Gestor, Líder)

```json
// Response 200: IdeiaResponse com apoios atualizado
// 400 { "error": "Não é possível apoiar a própria ideia", "status": 400 }
// 409 { "error": "Você já apoiou esta ideia", "status": 409 }
```

**Feedback útil** — `POST /api/v1/ideias/{id}/feedback-util` (Operador autor)

```json
// Request
{ "util": true }

// Response 200: IdeiaResponse com feedbackUtil = true (gera FEEDBACK_UTIL para o Gestor avaliador)
// 400 { "error": "Ideia ainda não foi avaliada", "status": 400 }
// 409 { "error": "Avaliação já respondida", "status": 409 }
```

**Campanha patrocinada** — campos opcionais em `POST/PUT /api/v1/orientacoes`

```json
// Request
{
  "titulo": "Zero acidentes no pátio",
  "descricao": "Ideias para reduzir incidentes em manobras e carga/descarga.",
  "categoria": "SEGURANCA",
  "campanha": "Pátio Seguro 2026",
  "campanhaFim": "2026-11-30",
  "campanhaBonusAsas": 50,
  "campanhaTopN": 3
}
// OrientacaoResponse devolve também campanhaEncerradaEm e campanhaAtiva.
// PUT sem os campos campanhaFim/campanhaBonusAsas/campanhaTopN remove a campanha.
```

**Encerrar campanha** — `POST /api/v1/orientacoes/{id}/campanha/encerrar` (Líder)

```json
// Response 200
{
  "orientacaoId": "ori-seguranca",
  "campanha": "Pátio Seguro 2026",
  "encerradaEm": "2026-11-30T21:00:00Z",
  "vencedores": [
    { "userId": "user-op-joana", "nome": "Joana Ribeiro", "xpCampanha": 45, "bonusAsas": 50 },
    { "userId": "user-operador", "nome": "Pedro Santos", "xpCampanha": 30, "bonusAsas": 50 }
  ]
}
// 400 { "error": "Orientação não tem campanha patrocinada", "status": 400 }
// 409 { "error": "Campanha já encerrada", "status": 409 }
```

---

## 9. Rastreabilidade Sprint 1 → Sprint 2

| Capacidade Sprint 1 (app) | IDs S1 | Evolução Sprint 2 |
|---------------------------|--------|-------------------|
| Auth local (Room + SHA-256) | AUTH-01..06 | S2-AUTH-* (JWT no servidor) |
| Orientações | ORI-01..06 | S2-ORI-* via API |
| Ideias | IDE-01..12 | S2-IDE-* via API; voz pode permanecer on-device |
| Projetos | PRO-01..07 | S2-PRO-* via API |
| Dashboard | DASH-01..07 | S2-DASH-* via API; narrativa local pode permanecer ou evoluir pós-S2 |
| Avaliação ideias (Gestor/Líder no app S1) | IDE-08 | Backend: só Gestor (D1); ajustar UI do Líder |
| Scoring / similaridade local | IDE-03, IDE-11 | S2-IA-01 (Gemini) no fluxo do Gestor |
| Radar mock | INO-01..03 | Removido na Sprint 2 (S2-MOB-06 fora do escopo) |
| Gamificação (cálculo local, ranking e desafios simulados) | GAM-01..06 | S2-GAM-01..18: ledger no servidor, loja de recompensas e resgates; regras locais e dados simulados removidos do app |
| Offline-first Room | RNF-09 | D4: API fonte oficial; Room opcional como cache de leitura |

---

## 10. Entregáveis (checklist do enunciado)

| # | Entregável | Formato | Status |
|---|------------|---------|--------|
| E1 | Código-fonte do backend | `.zip`, camadas/módulos claros + `README.md` de execução | ✅ |
| E2 | Projeto do app mobile | `.zip` com artefatos; integrado via REST | ✅ |
| E3 | Executável mobile | `.apk` (Android) ou `.ipa` (iOS) | ✅ |
| E4 | Apresentação | `.PDF` ou `.PPT` com nomes, RMs e **diagrama de arquitetura do backend** | ✅ |
| E5 | Especificação de endpoints | Rotas, métodos, payloads, responses e erros; OpenAPI/Swagger + amostras §8.1 e §8.2 | ✅ |
| E6 | Documentação de IA (plus) | Modelo usado + funcionalidade implementada | ✅ |

### Critérios de avaliação (referência)

| Critério | Peso |
|----------|------:|
| Implementação técnica funcional | 50% |
| Qualidade de código e boas práticas | 10% |
| Integração app ↔ backend | 15% |
| Apresentação e documentação | 15% |
| Criatividade e inovação | 10% |

**Implicação de produto:** priorizar **API + RBAC + integração real + 1 diferencial de IA** antes de polish secundário.

---

## 11. Backlog sugerido (ordem de implementação)

### Fase A — Fundação (bloqueante)
1. Projeto Spring Boot 3 + MongoDB (Docker Compose)
2. Modelo de usuário + login JWT + roles (Operador/Gestor/Líder)
3. CRUD Orientações com RBAC + soft-delete
4. CRUD Ideias + prioridade/avaliação (somente Gestor aprova)
5. CRUD Projetos + vínculo estratégia/ideia
6. Endpoints de Dashboard summary
7. Cliente Gemini + `POST /api/v1/ai/score-ideas`

### Fase B — Integração mobile
7. Configurar base URL / ambientes no app
8. Substituir repositórios locais pelos remotos nos fluxos oficiais
9. Persistir JWT; tratar 401/403
10. Remover mocks do caminho de demo
11. Gerar novo APK

### Fase C — Diferencial e entrega
12. Documentação IA (E6) + testes do scoring
13. Especificação formal de endpoints (OpenAPI)
14. README backend + diagrama na apresentação
15. Empacotar zips e revisar checklist §10

### Estimativa relativa de valor vs esforço (produto)

```
Alto valor / menor risco:   Auth JWT + CRUD + Dashboard + integração
Alto valor / médio risco:   IA-A + IA-C (Gemini) no backend
Fora do escopo S2:          IA-B (chat), sync offline completo
```

---

## 12. Critérios de pronto (Definition of Done — Sprint 2)

Uma feature está **pronta** quando:

1. Endpoint implementado com validação e RBAC no **servidor**
2. Cobertura mínima de teste no caminho feliz + 401/403 onde aplicável
3. App consome o endpoint (sem mock) no fluxo correspondente
4. Contrato documentado (request/response de exemplo)
5. README permite a outro aluno subir e testar com os usuários seed

A Sprint 2 está **pronta para entrega** quando o checklist §10 (E1–E6) estiver completo e a demo percorrer: **login → ideia → scoring IA (Gestor) → aprovação → projeto → dashboard (Líder)** com dados vindos da API.

---

## 13. Riscos e mitigações

| Risco | Impacto | Mitigação |
|-------|---------|-----------|
| UI Sprint 1: Líder ainda aprova ideias | Médio | Ajustar telas conforme D1; backend já retorna 403 |
| App acoplado ao Room | Médio | Repositórios remotos + cache opcional (D4) |
| Escopo de IA estourar | Médio | Manter **IA-A + IA-C** implementadas; IA-B/chat permanece fora |
| MongoDB sem índices | Médio | Índices no seed/migration inicial |
| Segredo JWT/API key commitado | Alto | `.env` + `.gitignore`; documentar variáveis no README |
| Divergência de contrato app/API | Alto | OpenAPI/Swagger + exemplos no doc de endpoints |

---

## 14. Referências internas

| Documento | Uso |
|-----------|-----|
| [`REQUISITOS.md`](./REQUISITOS.md) | Baseline funcional Sprint 1 |
| [`ARCHITECTURE.md`](./ARCHITECTURE.md) | Arquitetura atual do app |
| [`GAMIFICACAO.md`](./GAMIFICACAO.md) | Regras da Economia de Inovação GAB (S2-GAM-*) |
| [`ENTREGA_RECOMPENSAS.md`](./ENTREGA_RECOMPENSAS.md) | Quem entrega cada recompensa depois do resgate confirmado |
| [`MARCA_GAB.md`](./MARCA_GAB.md) | Marca, negócios e valores do Grupo Águia Branca |
| `DOCUMENTACAO_TECNICA.pdf` (raiz do challenge) | Material técnico já gerado |
| `InovaGAB.apk` | Binário Sprint 1 (baseline) |

---

*Documento de requisitos Sprint 2 — Challenge FIAP 2026 | Grupo Águia Branca.*  
*Decisões D1–D5 e stack Java registradas em 21/09/2026. Atualizar status dos IDs `S2-*` durante a implementação.*
