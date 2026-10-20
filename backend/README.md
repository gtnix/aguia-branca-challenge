# InovaGAB API — Backend Sprint 2

API REST em **Java 17 + Spring Boot 3** com **MongoDB**, **JWT**, **RBAC** e scoring de ideias (**IA-A / Google Gemini**).

Documentação de requisitos: [`../docs/REQUISITOS_SPRINT2.md`](../docs/REQUISITOS_SPRINT2.md)

## Pré-requisitos

- JDK 17+
- Maven 3.8+
- Docker (para MongoDB)

## Subir MongoDB

```bash
cd backend
docker compose up -d
```

## Configuração

Copie `.env.example` para `.env` (já gitignored) e preencha os secrets. A API carrega o `.env` na subida **sem sobrescrever** variáveis já exportadas no shell:

```bash
cp .env.example .env
# edite GEMINI_API_KEY e JWT_SECRET
```

```bash
export MONGODB_URI=mongodb://localhost:27017/inovagab
export JWT_SECRET=sua-chave-secreta-com-pelo-menos-32-caracteres
export GEMINI_API_KEY=   # opcional; sem chave usa fallback heurístico
export GEMINI_MODEL=gemini-3.6-flash
export CORS_ORIGINS=http://localhost:8080,http://127.0.0.1:8080
```

Em produção, use `SPRING_PROFILES_ACTIVE=prod`. Esse profile exige `MONGODB_URI`,
`JWT_SECRET` e `CORS_ORIGINS` sem valores default e desabilita Swagger/OpenAPI público.

## Executar a API

```bash
mvn spring-boot:run
```

- Base URL: `http://localhost:8080/api/v1`
- Health: `GET /api/v1/health`
- Swagger UI: `http://localhost:8080/api/v1/swagger-ui.html`

## Usuários demo (senha `123456`)

| Email | Perfil |
|-------|--------|
| marcos.silva@aguiabranca.com.br | Líder |
| ana.oliveira@aguiabranca.com.br | Gestor |
| pedro.santos@aguiabranca.com.br | Operador |

Para o ranking da gamificação, o `GamificacaoSeeder` também cria colegas com a mesma senha: Operadores `joana.ribeiro`, `rafael.costa`, `bruna.lima`, `carlos.mendes`, `lucia.ferraz`, `thiago.alves` e o Gestor `paulo.nunes` (todos `@aguiabranca.com.br`).

## Login (exemplo)

```bash
curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ana.oliveira@aguiabranca.com.br","senha":"123456"}'
```

Sessão autenticada:

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ana.oliveira@aguiabranca.com.br","senha":"123456"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

curl -s http://localhost:8080/api/v1/auth/me \
  -H "Authorization: Bearer $TOKEN"
```

## Orientações

Autenticado (qualquer perfil): `GET /api/v1/orientacoes?apenasAtivas=true` e `GET /api/v1/orientacoes/{id}` de orientação **ativa**.

Operador e Gestor: a listagem ignora `apenasAtivas=false` (sempre ativas). `GET /{id}` de orientação inativa retorna **404**.

Somente Líder: `POST /api/v1/orientacoes`, `PUT /api/v1/orientacoes/{id}`, `PUT /api/v1/orientacoes/{id}/ativo?ativo=true|false`, `DELETE /api/v1/orientacoes/{id}` (soft-delete, HTTP 204). `GET` com `apenasAtivas=false` inclui inativas e excluídas no histórico; o detalhe também permanece consultável pelo Líder. Operador/Gestor recebem 403 em escrita.

Seed de categoria: `EFICIENCIA_OPERACIONAL` (enum do app). Na subida, o seeder corrige documentos legados com `Operações`.

## Ideias

Autenticado: `GET /api/v1/ideias` (Operador = próprias; Gestor/Líder = todas) e `GET /api/v1/ideias/{id}`. Filtros opcionais: `status`, `area`, `orientacaoId`.

Somente Operador: `POST /api/v1/ideias`, `PUT /api/v1/ideias/{id}`, `DELETE /api/v1/ideias/{id}` (soft-delete, HTTP 204). `orientacaoId` obrigatório e deve ser uma orientação **ativa**. Operador não edita ideia de terceiro (403). Gestor/Líder recebem 403 em escrita.

Somente Gestor: `PATCH /api/v1/ideias/{id}/prioridade` (1–100) e `PATCH /api/v1/ideias/{id}/avaliacao` (`APROVADA` ou `REPROVADA`; feedback obrigatório ao reprovar). Operador e Líder recebem **403**. Ideia já avaliada não pode ser reavaliada (**400**). A IA **não aprova**.

Filtro `status` inválido (ex.: `FOO`, `EM_ANALISE`) responde **400**, não 401.

### Demo mínima (Operador → Gestor)

```bash
PEDRO=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"pedro.santos@aguiabranca.com.br","senha":"123456"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

ANA=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"ana.oliveira@aguiabranca.com.br","senha":"123456"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

ORI=$(curl -s http://localhost:8080/api/v1/orientacoes?apenasAtivas=true \
  -H "Authorization: Bearer $PEDRO" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)[0]['id'])")

IDEIA=$(curl -s -X POST http://localhost:8080/api/v1/ideias \
  -H "Authorization: Bearer $PEDRO" -H 'Content-Type: application/json' \
  -d "{\"titulo\":\"Demo banca\",\"descricao\":\"Vinculada à orientação vigente.\",\"tipo\":\"IDEIA\",\"area\":\"OPERACOES\",\"orientacaoId\":\"$ORI\"}" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

curl -s http://localhost:8080/api/v1/ideias -H "Authorization: Bearer $PEDRO"

curl -s -o /dev/null -w "%{http_code}\n" -X PUT "http://localhost:8080/api/v1/ideias/$IDEIA" \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d "{\"titulo\":\"hack\",\"descricao\":\"x\",\"tipo\":\"IDEIA\",\"area\":\"OPERACOES\",\"orientacaoId\":\"$ORI\"}"
# esperado: 403

curl -s -o /dev/null -w "%{http_code}\n" -X DELETE "http://localhost:8080/api/v1/ideias/$IDEIA" \
  -H "Authorization: Bearer $ANA"
# esperado: 403

curl -s http://localhost:8080/api/v1/ideias -H "Authorization: Bearer $ANA"
```

## Scoring IA-A (Gemini)

Somente Gestor: `POST /api/v1/ai/score-ideas`. Recebe `orientacaoId` + `ideiaIds` de ideias **pendentes** daquela orientação. IDs de outra orientação, já avaliados ou inexistentes são **ignorados**; se nenhum ID restante for pendente válido, a API responde **400**.

A chave Gemini vai no header `x-goog-api-key` (nunca na query string da URL). Sem `GEMINI_API_KEY` **ou** se a chamada Gemini falhar: `fallback=true`, `modelo=heuristic-local` e score `impacto*20 − esforço*5` (10–100). O score é persistido na ideia (`scoreIa`, `justificativaIa`) como **recomendação**.

```bash
curl -s -X POST http://localhost:8080/api/v1/ai/score-ideas \
  -H "Authorization: Bearer $ANA" \
  -H 'Content-Type: application/json' \
  -d "{\"orientacaoId\":\"$ORI\",\"ideiaIds\":[\"$IDEIA\"]}"
```

Resposta 200:

```json
{
  "ranking": [
    { "ideiaId": "...", "score": 87, "justificativa": "Alto alinhamento com a campanha..." }
  ],
  "modelo": "gemini-3.6-flash",
  "fallback": false
}
```

Prioridade humana (1–100), só Gestor:

```bash
curl -s -X PATCH "http://localhost:8080/api/v1/ideias/$IDEIA/prioridade" \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"prioridade":80}'
```

Aprovação humana:

```bash
curl -s -X PATCH "http://localhost:8080/api/v1/ideias/$IDEIA/avaliacao" \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"status":"APROVADA"}'
```

Operador e Líder em `POST /api/v1/ai/score-ideas` e nos PATCH de avaliação/prioridade recebem **403**.

## Relatório executivo IA-C (Gemini)

Somente Líder: `GET /api/v1/ai/dashboard-narrative`. O query param opcional
`orientacaoId` filtra os KPIs e a narrativa pela estratégia selecionada.

O backend envia ao Gemini métricas calculadas pelo `DashboardService`: ideias,
aprovações, conversões, projetos, prazos, investimento, retorno, lucro, ROI, progresso,
tempo de aprovação e participação por área. O prompt proíbe inventar números e exige:

- título executivo de até 10 palavras;
- resumo em até duas frases;
- até três evidências numéricas exatas;
- uma ação recomendada concreta.

```bash
curl -s "http://localhost:8080/api/v1/ai/dashboard-narrative" \
  -H "Authorization: Bearer $MARCOS"
```

```json
{
  "titulo": "Portfólio com retorno positivo e prazo em risco",
  "resumo": "Síntese executiva baseada nos KPIs.",
  "evidencias": ["ROI de 28%", "2 projetos atrasados"],
  "acaoRecomendada": "Revisar os projetos atrasados e definir recuperação.",
  "modelo": "gemini-3.6-flash",
  "fallback": false,
  "geradoEm": "2026-09-21T23:00:00Z"
}
```

Em ausência de chave, quota excedida ou erro do provedor, regras locais analisam ideias,
atrasos, ROI e conversão e devolvem o mesmo contrato com `modelo=heuristic-local` e
`fallback=true`. Operador e Gestor recebem **403**.

## Projetos

Gestor e Líder: `GET /api/v1/projetos` e `GET /api/v1/projetos/{id}` (somente ativos). Operador recebe **403**.

Somente Gestor: `POST /api/v1/projetos`, `PUT /api/v1/projetos/{id}`, `PATCH /api/v1/projetos/{id}/progresso`, `DELETE /api/v1/projetos/{id}` (soft-delete, HTTP 204). O soft-delete anula `ideiaId` e o índice único parcial cobre só documentos **ativos**, para a mesma ideia poder ser convertida de novo se voltar a `APROVADA`. Líder e Operador recebem **403** em mutação.

`orientacaoId` é obrigatório e deve ser uma orientação **ativa**. `ideiaId` é opcional:

- sem `ideiaId`: projeto manual
- com `ideiaId`: a ideia precisa estar **APROVADA** e na mesma orientação; o backend marca `CONVERTIDA_PROJETO`
- **idempotente**: um segundo `POST` com a mesma `ideiaId` devolve o projeto já existente (não duplica)

Campos de progresso: `etapa`, `status` (`PLANEJADO`, `EM_ANDAMENTO`, `PAUSADO`, `CONCLUIDO`, `CANCELADO`), `progressoPercentual` (0–100), `investimento`, `prazo`, `retorno` (mensal).

**ROI (%)** calculado no servidor, igual ao app:

`((retorno mensal × 12) − investimento) / investimento × 100`

```bash
# aprova e converte (repete o POST para ver o mesmo id)
curl -s -X PATCH "http://localhost:8080/api/v1/ideias/$IDEIA/avaliacao" \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"status":"APROVADA"}'

PROJ=$(curl -s -X POST http://localhost:8080/api/v1/projetos \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d "{\"titulo\":\"Demo banca\",\"objetivo\":\"Converter ideia aprovada\",\"descricao\":\"Iniciativa da demo.\",\"area\":\"OPERACOES\",\"orientacaoId\":\"$ORI\",\"ideiaId\":\"$IDEIA\",\"investimento\":100000,\"retorno\":10000,\"prazo\":\"2026-12-31\"}" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

curl -s -X POST http://localhost:8080/api/v1/projetos \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d "{\"titulo\":\"Demo banca\",\"objetivo\":\"Converter ideia aprovada\",\"descricao\":\"Iniciativa da demo.\",\"area\":\"OPERACOES\",\"orientacaoId\":\"$ORI\",\"ideiaId\":\"$IDEIA\",\"investimento\":100000,\"retorno\":10000,\"prazo\":\"2026-12-31\"}"
# esperado: mesmo id em $PROJ

curl -s -X PATCH "http://localhost:8080/api/v1/projetos/$PROJ/progresso" \
  -H "Authorization: Bearer $ANA" -H 'Content-Type: application/json' \
  -d '{"status":"EM_ANDAMENTO","progressoPercentual":40,"etapa":"EXECUCAO"}'

curl -s -o /dev/null -w "%{http_code}\\n" -X PATCH "http://localhost:8080/api/v1/projetos/$PROJ/progresso" \
  -H "Authorization: Bearer $PEDRO" -H 'Content-Type: application/json' \
  -d '{"progressoPercentual":50}'
# esperado: 403

curl -s http://localhost:8080/api/v1/projetos -H "Authorization: Bearer $ANA"
```

## Dashboard (Líder)

Somente Líder: `GET /api/v1/dashboard/summary`, `/funnel`, `/by-area`, `/by-strategy/{orientacaoId}`, `/by-project/{projetoId}`. Operador e Gestor recebem **403**. Sem token: **401**. Orientação ou projeto inexistente: **404**.

Os KPIs e as séries de gráfico são **calculados no servidor**. O app não agrega ROI/funil/área localmente no fluxo do Líder: a Home e o Resumo Executivo leem `GET /dashboard/summary`.

**ROI médio (%)** da carteira, igual aos projetos:

`((retorno mensal × 12) − investimento) / investimento × 100`

Projetos com status `CANCELADO` **não entram** em ROI, lucro, investimento, retorno, prazos nem em `projetosAtivos`. Soft-delete (`ativo=false`) também fica de fora.

**Lucro total** = retorno mensal × 12 − investimento.

**Ganho de produtividade** = média do `progressoPercentual` da mesma carteira (ativos e não cancelados).

**Tempo médio de aprovação** = média em dias entre `dataCriacao` e `dataAvaliacao` das ideias `APROVADA`, `REPROVADA` ou `CONVERTIDA_PROJETO`.

```bash
MARCOS=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"marcos.silva@aguiabranca.com.br","senha":"123456"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

curl -s http://localhost:8080/api/v1/dashboard/summary -H "Authorization: Bearer $MARCOS"

curl -s -o /dev/null -w "%{http_code}\\n" http://localhost:8080/api/v1/dashboard/summary \
  -H "Authorization: Bearer $ANA"
# esperado: 403
```

Resposta 200 de `/summary` (campos principais):

```json
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
    { "area": "OPERACOES", "label": "Operações", "quantidade": 4, "percentual": 80.0 },
    { "area": "LOGISTICA", "label": "Logística", "quantidade": 1, "percentual": 20.0 }
  ]
}
```

`ideiasAprovadas` contabiliza somente ideias no estado `APROVADA`; ideias já convertidas
são contabilizadas separadamente em `ideiasEmProjeto`. `projetosAtivos` representa a
carteira não cancelada e não excluída.

## Gamificação — Economia de Inovação GAB

Regras em [`docs/GAMIFICACAO.md`](../docs/GAMIFICACAO.md); contratos com exemplos em [`docs/REQUISITOS_SPRINT2.md`](../docs/REQUISITOS_SPRINT2.md) §8.2.

Os pontos são gravados no servidor (coleção `pontos_eventos`) pelos próprios fluxos de ideia, projeto e orientação; não há endpoint para "dar pontos". **XP** define nível e ranking da temporada; **Asas** são o saldo da loja.

| Rota | Perfis |
|------|--------|
| `GET /gamificacao/me`, `GET /gamificacao/extrato`, `POST /gamificacao/novidades/vistas` | Todos |
| `GET /gamificacao/ranking?perfil=&escopo=DIVISAO\|GRUPO&pagina=&tamanho=`, `GET /gamificacao/ranking/equipes` | Operador: aba Operadores; Gestor: Gestores e Operadores; Líder: todas (outras abas: **403**). Página padrão de 50; `totalParticipantes` e `minhaEntrada` cobrem a lista inteira |
| `GET /gamificacao/indicacoes-inovagab?temporada=` | Líder (**403** nos outros perfis). Retrato da zona de prêmio para o Imova-se |
| `GET /recompensas` | Todos (com `podeResgatar` e `motivoBloqueio`) |
| `POST /recompensas`, `PUT /recompensas/{id}` | Líder |
| `POST /recompensas/{id}/resgatar` | Operador, Gestor (**409** sem saldo, estoque ou nível) |
| `GET /resgates/meus` | Todos |
| `GET /resgates?status=`, `PUT /resgates/{id}/status` | Líder (só `ENTREGUE`; **403** o próprio, **409** transição inválida) |
| `POST /ideias/{id}/apoio` | Gestor, Líder (**400** própria ideia, **409** repetido) |
| `POST /ideias/{id}/feedback-util` | Operador autor |
| `POST /orientacoes/{id}/campanha/encerrar` | Líder (**400** sem campanha, **409** já encerrada) |

Demo do resgate (Pedro pede, já entra aprovado; Marcos marca entregue):

```bash
PEDRO=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"email":"pedro.santos@aguiabranca.com.br","senha":"123456"}' \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['accessToken'])")

curl -s http://localhost:8080/api/v1/gamificacao/me -H "Authorization: Bearer $PEDRO"
curl -s http://localhost:8080/api/v1/recompensas -H "Authorization: Bearer $PEDRO"

RESGATE=$(curl -s -X POST http://localhost:8080/api/v1/recompensas/rec-trilha/resgatar \
  -H "Authorization: Bearer $PEDRO" \
  | python3 -c "import sys,json; print(json.load(sys.stdin)['id'])")

curl -s -X PUT http://localhost:8080/api/v1/resgates/$RESGATE/status \
  -H "Authorization: Bearer $MARCOS" -H 'Content-Type: application/json' \
  -d '{"status":"ENTREGUE"}'

curl -s http://localhost:8080/api/v1/gamificacao/extrato -H "Authorization: Bearer $PEDRO"
```

Com o saldo semeado, Pedro tem Asas para a trilha (40). Se o saldo não bastar, a API responde **409** com o mesmo texto de `motivoBloqueio` (ex.: `"Faltam 40 Asas"`).

## App Android (emulador)

O app debug aponta para `http://10.0.2.2:8080/api/v1/` (localhost da máquina host). Suba o Mongo e a API antes de logar. Em dispositivo físico, altere `API_BASE_URL` em `app/build.gradle.kts` para o IP LAN da máquina.

## Estrutura (camadas)

```
src/main/java/com/gtnix/inovagab/
├── config/          # Security, seed, propriedades
├── controller/      # REST /api/v1/*
├── service/         # Regras de negócio
├── repository/      # Spring Data MongoDB
├── document/        # Entidades @Document
├── dto/             # Request/Response
└── security/        # JWT, UserDetails
```

## Build

```bash
mvn -q test package
```

JAR: `target/inovagab-api-0.1.0-SNAPSHOT.jar`

Validação final de 21/09/2026: **128 testes**, 0 falhas.

## Hardening (fatia 7)

- Validação Jakarta nos DTOs (tamanho, faixa de impacto/esforço 1–5, ROI/progresso).
- Máquina de estados: ideia só edita/exclui `PENDENTE`; projeto `CONCLUIDO`/`CANCELADO` não reabre.
- Índices MongoDB: `autorUserId`/`status`/`orientacaoId` (+ `ativo`) em ideias; `orientacaoId`/`status` em projetos; categoria em orientações.
- Erros JSON `{ "error", "status" }` em **400 / 401 / 403 / 404 / 500**. CORS restrito a `CORS_ORIGINS`.
- OpenAPI: `http://localhost:8080/api/v1/swagger-ui.html` e `GET /api/v1/openapi` (Bearer JWT).
- Logs de request: método, path, status e duração (sem body/token).

Smoke dos três perfis (API no ar):

```bash
chmod +x scripts/smoke.sh
./scripts/smoke.sh
```

## Logout / JWT

Sessão **stateless**: o cliente descarta o token. Expiração configurável em `JWT_EXPIRATION_SECONDS` (padrão 3600s).
