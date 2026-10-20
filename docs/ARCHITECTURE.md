# Arquitetura Técnica — InovaGAB Sprint 2

**Plataforma de Inovação Corporativa | Challenge FIAP 2026 — Grupo Águia Branca**

## 1. Visão integrada

```text
Android nativo
Compose + MVVM + Hilt
        |
        | HTTPS/HTTP local + JSON + Bearer JWT
        v
Spring Boot 3 /api/v1
Controller -> Service -> Repository
        |                    |
        v                    v
MongoDB                  Google Gemini
```

O app Android preserva Clean Architecture e usa a API como fonte oficial para autenticação,
orientações, ideias, projetos, dashboard e IA. Room permanece apenas como infraestrutura
local de apoio à gamificação; não autentica nem substitui os fluxos de domínio da Sprint 2.

## 2. Android

Pacote raiz: `com.gtnix.aguiabranca`.

```text
presentation/
  screens, components, navigation, ViewModels, StateFlow
        |
        v
domain/
  models, repository interfaces, use cases, SessionManager
        |
        v
data/
  remote/api, DTOs, interceptors, repository implementations
  local/Room (apoio local), session/EncryptedTokenStore
```

### Stack

- Kotlin/JVM 17, minSdk 26, target/compile SDK 35
- Jetpack Compose + Material 3
- Hilt + KSP
- Retrofit 2, OkHttp e Gson
- Coroutines + Flow
- Room para apoio local
- `EncryptedSharedPreferences` para o JWT
- JUnit 4, MockK, Turbine e MockWebServer

### Fluxo de sessão

1. `LoginViewModel` exige conectividade.
2. `UsuarioRepositoryImpl` chama `POST /api/v1/auth/login`.
3. O token e sua expiração são persistidos pelo `EncryptedTokenStore`.
4. `AuthInterceptor` adiciona `Authorization: Bearer`.
5. `SessionManagerImpl.restoreSession()` consulta `GET /api/v1/auth/me`.
6. Token expirado ou resposta 401 limpa a sessão; `UnauthorizedInterceptor` retorna ao login.

O usuário remoto autenticado é armazenado localmente apenas para compatibilidade com
gamificação. Não há `DatabaseSeeder` nem autenticação local no fluxo oficial.

### Repositórios oficiais

- `UsuarioRepositoryImpl`: login remoto e cache do usuário autenticado
- `OrientacaoRepositoryImpl`: REST, leitura para todos e mutação do Líder
- `IdeiaRepositoryImpl`: REST, CRUD próprio do Operador e IA-A do Gestor
- `ProjetoRepositoryImpl`: REST, mutação do Gestor e leitura do Líder
- `DashboardRepositoryImpl`: KPIs e séries calculados no servidor

### RBAC também na navegação

- Operador: cria e mantém apenas as próprias ideias
- Gestor: consulta todas as ideias, pontua com IA, aprova e gerencia projetos
- Líder: gerencia orientações e consulta projetos/dashboard
- Rotas de criação/edição são bloqueadas no `MainScreen` quando o perfil não é permitido

## 3. Backend

Pacote raiz: `com.gtnix.inovagab`.

```text
controller/   endpoints REST e @PreAuthorize
service/      regras de negócio, ownership, estados e agregações
repository/   Spring Data MongoDB
document/     documentos e índices
dto/          requests/responses validados
security/     JWT, UserDetails e filtro Bearer
config/       seed, CORS, OpenAPI, Gemini e ambiente
web/          erros JSON e logs de request
```

### Stack

- Java 17, Spring Boot 3.3.5
- Spring Security + JWT HMAC
- BCrypt para senhas
- Spring Data MongoDB
- Jakarta Bean Validation
- Springdoc OpenAPI/Swagger
- Cliente HTTP server-side para Google Gemini

### Domínio e persistência

- `usuarios`: email único, perfil e senha BCrypt
- `orientacoes`: histórico, categoria, campanha, ativo e soft-delete
- `ideias`: autor, orientação, status, prioridade e recomendação de IA
- `projetos`: orientação/ideia, progresso, investimento, retorno, prazo e ROI

Índices cobrem email, autor, status, área, orientação e documentos ativos. Exclusões de
orientações, ideias e projetos preservam rastreabilidade por soft-delete.

## 4. Segurança e acesso

O servidor é a autoridade final:

| Recurso | Operador | Gestor | Líder |
|---|---|---|---|
| Orientações | leitura | leitura | CRUD + histórico |
| Ideias | CRUD próprias | leitura, prioridade, IA e avaliação | leitura |
| Projetos | sem acesso | CRUD e progresso | leitura |
| Dashboard | sem acesso | sem acesso | leitura |

- 401: token ausente, inválido ou expirado
- 403: perfil sem permissão
- 400: payload ou transição inválida
- 404: recurso inexistente/inacessível
- Secrets são lidos de variáveis de ambiente; o profile `prod` não possui defaults
- Swagger é desabilitado no profile `prod`
- CORS é configurável por `CORS_ORIGINS`

## 5. IA e serviço externo

### 5.1 IA 1 — avaliação e priorização de ideias

`POST /api/v1/ai/score-ideas` é exclusivo do Gestor. O backend monta contexto com a
orientação e as ideias pendentes, chama o Google Gemini e devolve score 0–100,
justificativa e ranking. A recomendação é persistida, mas a aprovação continua humana.

Em ausência de chave, quota excedida ou falha externa, a API responde 200 com
`fallback=true`, `modelo=heuristic-local` e aplica a regra documentada:

```text
score = clamp(impacto * 20 - esforço * 5, 10, 100)
```

### 5.2 IA 2 — relatório executivo do dashboard

`GET /api/v1/ai/dashboard-narrative` é exclusivo do Líder e aceita `orientacaoId`
opcional. O backend envia ao Gemini somente métricas calculadas pelo `DashboardService`:
ideias, conversão, projetos, prazos, investimento, retorno, lucro, ROI, progresso,
tempo de aprovação e participação por área.

O prompt proíbe inventar números e exige JSON com:

- `titulo`: síntese executiva de até 10 palavras
- `resumo`: até duas frases sobre a saúde do portfólio
- `evidencias`: até três dados numéricos exatos
- `acaoRecomendada`: ação concreta para a liderança

O `LeaderDashboardViewModel` consome o relatório ao abrir/atualizar o dashboard ou mudar
o filtro de orientação. Se o Gemini falhar, regras locais analisam ausência de ideias,
projetos atrasados, ROI e conversão, preservando o mesmo contrato com `fallback=true`.

## 6. Dashboard

O Líder consome `GET /api/v1/dashboard/summary`. ROI, lucro, investimento, retorno,
prazos, produtividade, funil e desempenho por área são calculados no servidor.

- `ideiasAprovadas`: ideias atualmente em `APROVADA`
- `ideiasEmProjeto`: ideias em `CONVERTIDA_PROJETO`
- `projetosAtivos`: carteira não cancelada e não excluída
- Projetos cancelados não entram nos indicadores financeiros

## 7. Ambientes e build

- Debug e release aceitam `INOVAGAB_API_BASE_URL` como propriedade Gradle ou variável
  de ambiente.
- O padrão `10.0.2.2` atende a demonstração local no emulador.
- Ambientes expostos devem usar endpoint HTTPS.
- O profile Spring `prod` exige `MONGODB_URI`, `JWT_SECRET` e `CORS_ORIGINS`.

## 8. Qualidade verificada

Validação de 21/09/2026:

- Backend: **128 testes**, 0 falhas
- Android: **206 testes**, 0 falhas
- `lintVitalRelease`, `assembleDebug` e `assembleRelease`: sucesso
- Smoke 401/403/404 e três perfis: sucesso
- Fluxo API ideia → IA/fallback → aprovação → projeto → dashboard: sucesso
- Login no emulador como Operador, Gestor e Líder: sucesso

---

*Documento atualizado para a entrega da Sprint 2 — 21/09/2026.*
