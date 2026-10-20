# LEIA-ME — Entrega Final InovaGAB Sprint 2

## O que enviar

Envie o arquivo único:

`InovaGAB_Sprint2_Entrega_Final.zip`

O ZIP final está organizado da seguinte forma:

```text
InovaGAB_Sprint2_Entrega_Final/
├── 00_LEIA-ME/
│   ├── LEIA-ME_ENTREGA.md
│   └── MANIFESTO_ENTREGA.md
├── 01_BACKEND/
│   └── inovagab-backend-sprint2.zip
├── 02_APLICATIVO/
│   └── inovagab-mobile-sprint2.zip
├── 03_APK/
│   └── InovaGAB_Sprint2.apk
├── 04_APRESENTACAO/
│   └── InovaGAB_Documentacao_Tecnica_Sprint2.pdf
├── 05_API/
│   └── openapi-sprint2.json
└── 06_INTEGRIDADE/
    └── SHA256SUMS.txt
```

## Conferência dos requisitos obrigatórios

### Backend ZIP

- Código Java Spring Boot organizado em controller, service, repository, document, DTO,
  security, config e web.
- `README.md` com documentação completa.
- `read.me` com instruções diretas de execução, conforme redação literal do enunciado.
- Docker Compose, `.env.example` sanitizado, testes e script smoke.
- Não contém `.env`, `target/`, logs ou secrets.

### Aplicativo ZIP

- Projeto Android completo e compacto.
- Integração REST real por Retrofit.
- JWT criptografado e RBAC na UI.
- Contém uma cópia do APK final.
- Não contém `build/`, `.gradle/`, `local.properties` ou caches.

### APK

- APK Android release alinhado e assinado.
- Instalação validada em emulador.
- Para a demonstração local, a API deve estar disponível em
  `http://10.0.2.2:8080/api/v1/`.

### Apresentação PDF

- Nomes completos e RMs dos dois integrantes.
- Diagrama da arquitetura Android + backend.
- Arquitetura em camadas do backend.
- JWT, BCrypt, RBAC e MongoDB.
- Todos os endpoints com rota, método, payload/parâmetros e resposta.
- Exemplos JSON e contrato de erros.
- Qualidade, testes, execução e roteiro de demonstração.

### Duas implementações de IA

1. **IA-A — Avaliação e priorização de ideias**
   - Perfil: Gestor.
   - Endpoint: `POST /api/v1/ai/score-ideas`.
   - Resultado: score 0–100, justificativa e ranking.
   - A decisão de aprovação continua humana.

2. **IA-C — Relatório executivo do dashboard**
   - Perfil: Líder.
   - Endpoint: `GET /api/v1/ai/dashboard-narrative`.
   - Resultado: título, resumo, evidências numéricas e ação recomendada.
   - Pode ser filtrado por orientação estratégica.

As duas funcionalidades utilizam Google Gemini no backend e possuem fallback local com
o campo `fallback=true`.

## Usuários para demonstração

Senha comum: `123456`

- Operador: `pedro.santos@aguiabranca.com.br`
- Gestor: `ana.oliveira@aguiabranca.com.br`
- Líder: `marcos.silva@aguiabranca.com.br`

## Roteiro recomendado para a banca

1. Subir MongoDB e backend.
2. Entrar como Operador e cadastrar uma ideia vinculada à estratégia.
3. Entrar como Gestor, executar o scoring IA-A, definir prioridade e aprovar.
4. Converter a ideia aprovada em projeto e atualizar o progresso.
5. Entrar como Líder, abrir o dashboard e exibir o relatório executivo IA-C.
6. Mostrar Swagger/OpenAPI e a matriz de acesso no PDF.

## Observação sobre quota Gemini

A integração com o provedor foi validada. Caso a chave esteja sem quota, o sistema
mantém o fluxo por meio do fallback documentado. Para demonstrar respostas reais do
modelo, configure uma chave com quota disponível em `GEMINI_API_KEY`.
