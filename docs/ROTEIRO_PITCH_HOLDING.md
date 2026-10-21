# Roteiro do pitch — InovaGAB para a holding Grupo Águia Branca

> **Nota:** roteiro histórico da apresentação de 20/10/2026. O dataset do demo foi refeito em [`DEMO_CASES.md`](./DEMO_CASES.md) com casos reais da operação (Docas inteligentes no CD Serra, preditiva de cubo e suspensão, embarque sem fila, roteirização verde). Onde este roteiro citar “Oficina sem papel”, vale o novo mapa.

**Live com a empresa parceira:** terça-feira, 20/10/2026, 19h30  
**Tempo:** 8 a 10 minutos (cronometrado pelo Scrum Guilherme)  
**Ordem pedida:** problemática → solução → demonstração do app → diferenciais  
**Equipe:** Giuliana Abe Takara (RM 562736) e André Luiz Oliveira da Silva (RM 565836), 2TDSA

> Público: a holding, não a banca técnica. Fale de operação, pessoas, estratégia e resultado.
> Stack (Spring, MongoDB, JWT, testes) só entra se alguém perguntar.

Fontes: [`REQUISITOS.md`](./REQUISITOS.md), [`REQUISITOS_SPRINT2.md`](./REQUISITOS_SPRINT2.md),
[`MARCA_GAB.md`](./MARCA_GAB.md), [`IDENTIDADE_AGUIA_BRANCA.md`](./IDENTIDADE_AGUIA_BRANCA.md),
[`GAMIFICACAO.md`](./GAMIFICACAO.md), [`ENTREGA_RECOMPENSAS.md`](./ENTREGA_RECOMPENSAS.md),
[`entregaveis/LEIA-ME_ENTREGA.md`](./entregaveis/LEIA-ME_ENTREGA.md).

---

## 1. Mapa do tempo

| Relógio | Bloco | Fala | Opera o app |
|---------|-------|------|-------------|
| 0:00–0:40 | Abertura | Giuliana | — |
| 0:40–2:00 | Problema | Giuliana | — |
| 2:00–3:20 | Solução | André | — |
| 3:20–7:20 | Demonstração | Giuliana narra | André |
| 7:20–8:50 | Diferenciais | Giuliana | — |
| 8:50–10:00 | Fecho e Next | André | — |

**Marcos de controle**

- Se a demonstração chegar a **6:50** sem ter aberto o dashboard do Líder: pule direto para o Líder.
- Se passar de **7:00** ao fim do Líder: corte o gancho da Loja e vá para os diferenciais.
- Se passar de **8:30** no início dos diferenciais: fale só os diferenciais 1 e 2.
- **Nunca corte** o fluxo ideia → avaliação do Gestor → dashboard do Líder.

---

## 2. Falas por bloco

As falas são guia, não texto para decorar palavra por palavra. Os trechos em **negrito** são as frases que precisam sair.

### 2.1 Abertura — 40s (Giuliana)

> Boa noite. Somos a Giuliana e o André, do grupo [confirmar número], e trouxemos o **InovaGAB**.
>
> O Grupo Águia Branca já tem um movimento de inovação forte: o Prêmio InovaGAB, o Imova-se, o Demoday, o Oscar da Inovação.
> O que a gente construiu é a **camada digital desse movimento**:
> **da ideia no pátio ao resultado no dashboard — e de volta para quem teve a ideia.**

### 2.2 Problema — 80s (Giuliana)

> O grupo tem mais de 20 mil colaboradores, mais de 25 empresas e mais de 690 bases.
> A maior parte dessas pessoas está no campo: motorista, pátio, oficina, guichê, concessionária. Em turno, longe do computador.
>
> E é justamente quem está no campo que conhece a dor real. Hoje vemos quatro problemas:
>
> 1. **Quem conhece a dor não tem canal.** A ideia morre no rádio ou no WhatsApp do encarregado.
> 2. **O funil é fragmentado.** O Imova-se captura, o Demoday premia o que foi implementado, o Oscar reconhece — mas não existe um sistema único que ligue a estratégia da liderança à execução e ao retorno financeiro.
> 3. **Volume não é valor.** Sem priorização e sem vínculo com a estratégia, o comitê recebe ruído.
> 4. **Engajamento sem contrapartida concreta.** Ponto virtual não mobiliza a operação.
>
> **O desafio não é ter mais um app de ideias. É fechar o ciclo da estratégia ao resultado, com reconhecimento para quem está na ponta.**

### 2.3 Solução — 80s (André)

> O InovaGAB conecta estratégia, pessoas, processos e tecnologia em três movimentos: **capturar, estruturar e acompanhar**. Cada um tem um dono.
>
> - **O Operador captura.** Registra uma dor ou oportunidade por texto ou por voz, sempre ligada a uma orientação estratégica vigente. É o Imova-se no bolso.
> - **O Gestor estrutura.** Recebe a fila de ideias, a inteligência artificial sugere uma priorização com justificativa, e **ele decide**. A ideia aprovada vira projeto com investimento, prazo e retorno.
> - **O Líder acompanha.** Define as orientações e campanhas, vê o portfólio em indicadores — ROI, lucro, prazos, funil por área — e recebe um relatório executivo gerado por IA.
>
> E tudo isso alimenta uma **Economia de Inovação**: quem gera resultado ganha reconhecimento e benefícios reais do próprio grupo, e os melhores de cada divisão são indicados ao Prêmio InovaGAB.
>
> É um único aplicativo, com uma base de dados única e permissões reais para cada papel. Vamos mostrar.

### 2.4 Demonstração — 4 min (André opera, Giuliana narra)

Usuários (senha `123456`, botões de login rápido na tela de entrada):

| Perfil | Pessoa | E-mail |
|--------|--------|--------|
| Operador | Pedro Santos | `pedro.santos@aguiabranca.com.br` |
| Gestor | Ana Oliveira | `ana.oliveira@aguiabranca.com.br` |
| Líder | Marcos Silva | `marcos.silva@aguiabranca.com.br` |

**Ideia da demonstração** (copiar para o bloco de notas antes da live):

- Título: `Checklist digital de pátio antes da saída do ônibus`
- Descrição: `Motorista confere pneus, freios e iluminação pelo celular antes de sair da garagem. Item crítico reprovado bloqueia a saída e avisa a manutenção.`
- Orientação: **Segurança operacional e jornada segura**

O banco **não** contém essa ideia. O Pedro já teve uma inspeção em papel reprovada com feedback da Ana — a versão digital é a resposta. Não use a palavra "checklist" em outra ideia do seed: a similaridade de título bloquearia a pontuação ao vivo.

O que já está na tela, para não parecer vazio:

- Pedro: 3 ideias próprias (1 convertida, 1 pendente, 1 reprovada), nível Em Ascensão, 2º na Logística, missão da semana aberta.
- Ana: 4 pendentes de operadores das três divisões, 2 aprovadas sem projeto, 3 projetos na carteira.
- Marcos: 4 orientações (campanha Jornada Segura 2026 ativa), dashboard com ROI e funil, 1 resgate da trilha para entregar.

**Passo 1 — Operador Pedro (50s)**

| Ação no app | Narração |
|-------------|----------|
| Login rápido como Pedro → Home | "Este é o Pedro, operador da Logística. Ele vê as orientações estratégicas do grupo." |
| Nova ideia → microfone (ou colar o texto) | "Ele está no pátio. Fala a ideia, o app transcreve em português." |
| Escolhe a orientação de Segurança → Enviar | "A ideia já nasce ligada à estratégia — aqui, segurança, o primeiro valor do grupo. **Quem está no campo registra em menos de um minuto.**" |

**Passo 2 — Gestora Ana (90s)**

| Ação no app | Narração |
|-------------|----------|
| Sair → login rápido como Ana → Ideias | "Agora a Ana, gestora. Ela vê a fila de todas as ideias, com filtros." |
| Rodar a priorização por IA | "A IA lê as ideias junto com a orientação estratégica e devolve uma nota de 0 a 100 com justificativa." |
| Abrir a ideia do Pedro → aprovar com feedback | "**A IA ranqueia; a Ana decide.** Nenhuma aprovação é automática. E o feedback vai de volta para o Pedro." |
| Converter em projeto → preencher investimento e retorno | "A ideia aprovada vira projeto, com investimento, prazo e retorno esperado. Esse é o caminho até o Demoday." |

**Passo 3 — Líder Marcos (70s)**

| Ação no app | Narração |
|-------------|----------|
| Sair → login rápido como Marcos → Dashboard | "O Marcos é da liderança. Ele vê o portfólio inteiro: ROI, lucro, investimento, projetos no prazo, funil de inovação e desempenho por área." |
| Abrir o relatório executivo por IA | "E recebe um relatório em linguagem de diretoria: o que está acontecendo, a evidência em números e a ação recomendada. **A diretoria vê resultado, não lista de tickets.**" |

**Passo 4 — Gancho de engajamento (30s, cortável)**

| Ação no app | Narração |
|-------------|----------|
| Voltar como Pedro → Ranking (ou Loja) | "Na Logística, Joana está em primeiro e o Pedro em segundo — os dois na zona de prêmio, indicados ao Imova-se. As Asas viram benefícios reais: passagem Águia Branca, trilha na Universidade Corporativa. O Pedro já resgatou a trilha; o Marcos confirma a entrega." |

**Se algo falhar ao vivo**

| Falha | O que fazer | Fala |
|-------|-------------|------|
| Microfone não transcreve | Colar o texto do bloco de notas | "Também dá para digitar." (seguir sem parar) |
| IA responde em modo de contingência (`fallback`) | Seguir normalmente | "Mesmo sem o modelo externo, a recomendação continua disponível por regra local, e a decisão continua humana." |
| Criação da ideia falha | Usar **"Sensor de ponto cego no pátio do CD Serra"** (Joana, pendente, Segurança) | "Vou usar uma ideia que já está na fila da Ana." |
| Backend ou rede cai | Reproduzir o vídeo de backup | "Para não tomar o tempo de vocês, vou mostrar a gravação do mesmo fluxo." |

### 2.5 Diferenciais — 90s (Giuliana)

> O que diferencia o InovaGAB são quatro escolhas:
>
> 1. **IA com governança humana.** São duas IAs: uma ajuda o gestor a priorizar, outra ajuda a liderança a agir. Nenhuma toma decisão sozinha, e se o serviço externo cair, o fluxo continua.
> 2. **Pontuamos resultado, não volume.** Mandar ideia vale pouco — 2 pontos. Ideia que vira projeto concluído vale 80 pontos e 100 Asas. Cada ponto aparece num extrato auditável, com regras públicas e proteção contra trapaça. É o valor **Integridade e Confiança** aplicado na prática.
> 3. **Ponte com o Prêmio InovaGAB, não programa paralelo.** Temporadas trimestrais, os três melhores por divisão indicados ao Imova-se, campanhas patrocinadas pela liderança e **peso extra para ideias de segurança**.
> 4. **Feito para o campo e para a cultura do grupo.** Voz em português, poucas telas, feedback obrigatório quando a ideia é reprovada — respeito às pessoas — e gincana entre equipes, como a AMIGAB já faz desde 1998.

### 2.6 Fecho e Next — 70s (André)

> Hoje o InovaGAB já funciona de ponta a ponta: aplicativo Android, servidor real, três perfis com permissões controladas, o funil completo da ideia ao dashboard, duas IAs e a economia de recompensas.
>
> Para o próximo passo, o que propomos é concreto: **um piloto em uma base de uma divisão**, com o catálogo de recompensas operado por RH, Divisão Passageiros e Universidade Corporativa. Depois, notificações e registro offline para quem está sem sinal, e a camada de inovação aberta com startups.
>
> **O InovaGAB não cria um programa paralelo. Ele digitaliza o Prêmio InovaGAB — da voz no pátio ao pódio do Oscar.**
>
> Obrigado.

Se houver avaliadores técnicos, uma frase extra antes do obrigado: "São 509 testes automatizados entre app e servidor, e toda a documentação técnica foi entregue."

---

## 3. Perguntas prováveis

| Pergunta | Resposta curta |
|----------|----------------|
| Quanto custa para rodar? | MVP acadêmico. O piloto cabe em uma base; a IA roda no servidor em plano de baixo custo; as recompensas usam ativos do próprio grupo, de custo marginal baixo. |
| E dados e LGPD? | Login com token, senha guardada com hash, permissões verificadas no servidor e nenhuma chave de IA no celular. |
| Funciona sem internet? | Nesta versão, registrar ideia exige rede. Registro offline com sincronização é o próximo passo para o campo. |
| Quem entrega a passagem ou o curso? | O app confirma o resgate e reserva o benefício. A área responsável (Passageiros, RH, oficina, Universidade Corporativa) entrega, e o Líder marca como entregue. |
| A IA pode aprovar sozinha? | Não. Ela recomenda com justificativa; aprovar e reprovar é sempre do gestor. |
| Por que não tem inovação aberta? | Priorizamos o funil interno com dados reais. Startups e universidades entram como próxima camada, ligadas à categoria Inovação Aberta do prêmio. |
| Dá para usar nas três divisões? | Sim. Orientações, ranking e indicações são separados por divisão (Passageiros, Logística, Comércio). |

---

## 4. Checklist pré-live

### Até 18h30 — ambiente

- [ ] MongoDB no ar: `cd codigo-fonte/backend && docker compose up -d`
- [ ] Backend no ar e respondendo em `GET http://localhost:8080/api/v1/health`
- [ ] `GEMINI_API_KEY` configurada; rodar uma priorização de teste e anotar se volta com `fallback=true`
- [ ] Emulador ou celular com o APK da Sprint 2 instalado (emulador usa `http://10.0.2.2:8080/api/v1/`)
- [ ] Notificações do computador e do celular silenciadas; bateria e carregador conectados
- [ ] Tela do emulador compartilhada e legível na chamada (zoom testado)

### Até 19h00 — dados da demonstração

- [ ] Login rápido funciona para Pedro, Ana e Marcos
- [ ] Orientação **"Segurança operacional e jornada segura"** aparece ativa para o Pedro
- [ ] Texto da ideia da demonstração copiado no bloco de notas
- [ ] Fila da Ana com pendentes de gente diferente: **"Sensor de ponto cego no pátio do CD Serra"** (Joana, reserva da IA), "Oficina sem papel" (Pedro), "Rastreamento de frota" (Rafael), "Peça certa na primeira visita" (Thiago)
- [ ] Dashboard do Marcos com funil 9 → 2 → 2 e três projetos: **Manutenção preditiva** (em andamento), **Check-in digital no embarque** (concluído, Demoday), **Roteirização da última milha** (planejado)
- [ ] Resgates do Marcos: **Trilha de capacitação** do Pedro em `APROVADO`, esperando marcar `ENTREGUE`
- [ ] Vídeo de backup gravado (45s a 1min: Pedro envia → Ana prioriza e aprova → Marcos abre dashboard e relatório)

### Ensaio — pelo menos uma vez inteiro

- [ ] Ensaio cronometrado terminando entre 9:00 e 9:30
- [ ] Troca Pedro → Ana → Marcos em menos de 15s cada (sair + login rápido)
- [ ] Combinar o sinal de corte entre Giuliana e André (ex.: "vamos direto ao dashboard")
- [ ] Ensaiar a fala de contingência da IA e a do vídeo de backup
- [ ] Confirmar o número do grupo da live (mensagem do Scrum cita **43**; documentos antigos citam **82**)

### Durante a live

- [ ] Confirmar presença com o Scrum antes da chamada dos grupos
- [ ] Câmera e microfone testados; uma pessoa só no aparelho
- [ ] Não abrir Swagger nem o PDF técnico — este pitch é para a holding
