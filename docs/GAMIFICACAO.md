# Gamificação InovaGAB — diagnóstico e Economia de Inovação GAB

**Challenge FIAP 2026 — Grupo Águia Branca**  
**Contexto de negócio:** [`MARCA_GAB.md`](./MARCA_GAB.md)  
**Requisitos:** [`REQUISITOS_SPRINT2.md`](./REQUISITOS_SPRINT2.md) §4.9 (`S2-GAM-*`)

Este documento registra o que estava errado na gamificação da Sprint 1, o modelo que a substitui e as regras exatas implementadas no backend. As regras moram **só no servidor** (`GamificacaoService`); o app apenas exibe o que a API devolve.

---

## 1. Princípios

1. **Resultado acima de volume.** Mandar ideia vale pouco. Ideia aprovada, convertida e concluída vale muito.
2. **O colaborador ganha algo concreto.** Pontos de reputação (XP) e uma moeda resgatável (Asas) trocada por recompensas reais do próprio grupo.
3. **Ciclo fechado.** Quem teve a ideia vê o que aconteceu com ela e quanto ela gerou.
4. **Justo e auditável.** Cada ponto é um evento no extrato, com motivo e data. Não há pontos calculados no celular.
5. **Temporadas.** O ranking zera a cada trimestre para que quem entrou agora tenha chance real.
6. **Ponte com o Prêmio InovaGAB.** Os 3 primeiros operadores de cada divisão ao fim da temporada são indicados à categoria Imova-se.

---

## 2. Diagnóstico da versão anterior (Sprint 1)

### 2.1 Quebras estruturais depois da Sprint 2

| Problema | Onde | Efeito |
|----------|------|--------|
| Ranking buscava participantes no Room | `GetRankingUseCase` → `UsuarioRepository.listarPorPerfil` | O Room só guarda o usuário logado; ranking com 0 ou 1 pessoa |
| Operador só recebe as próprias ideias da API | `IdeiaService.listar` | Pontuação dos colegas calculada no aparelho saía 0 |
| Nada persistido | `CalcularPontuacaoUseCase` recalculava a cada leitura | Sem histórico, sem extrato, datas de conquista falsas (`conquistaPlaceholderDate`) |
| Divisão padrão fixa | `HomeViewModel` chamava `executar(userId, perfil)` | Usuário de Passageiros ou Comércio via ranking de Logística |

### 2.2 Incentivos errados

| Perfil | Regra antiga | Problema |
|--------|--------------|----------|
| Operador | +10 por ideia, inclusive reprovada | Premia spam; 20 ideias ruins valiam mais que 1 ideia convertida |
| Operador | Conversão somava 10 + 5 + 20 | Contagem dupla do mesmo fato |
| Gestor | +5 por ideia avaliada da área | Sem `avaliadorUserId`: todos os gestores da área ganhavam pelo trabalho de um |
| Gestor | Conquistas por volume de aprovações | Aprovar tudo era a estratégia ótima |
| Líder | +15 por orientação criada, mesmo inativa | Subir de nível sem nenhum resultado |
| Líder | `LIDER_INOVACAO` contava todas as ideias do sistema | Conquista sem relação com o trabalho do líder |
| Todos | Upvote vazio (`incrementUpvote` sem efeito) | Botão que não fazia nada |
| Todos | Feedback obrigatório na reprovação valia 0 | O gesto mais importante do gestor não era reconhecido |

### 2.3 Elementos simulados

- Desafio semanal prometia "20 pts bônus" nunca creditados; progresso era `pontos % 3`.
- `streakDias`, `simularVariacaoPosicao`, `simularAcoesCount`, `DEMO_DELTA_SEMANA_POSICAO`: valores fictícios.
- Textos das conquistas (`strings.xml`), barra de progresso (`unlockProgress` + `simulatedUnlockProgress` com ruído aleatório) e regra real divergiam em pelo menos 8 conquistas.
- Tiers Bronze/Prata/Ouro/Platina eram decorativos.
- Gestor não via gamificação na Home.
- **Nenhuma recompensa tangível**, nenhuma mecânica coletiva, nenhuma ligação com o Prêmio InovaGAB.

---

## 3. Modelo: duas moedas

| Moeda | Para que serve | Pode diminuir? |
|-------|----------------|----------------|
| **XP** | Reputação. Define o nível (vitalício) e o ranking da temporada (trimestral) | Só por estorno de evento inválido |
| **Asas** | Moeda resgatável na Loja de Recompensas | Sim, ao resgatar |

### 3.1 Níveis (XP vitalício)

| Nível | XP |
|-------|----|
| Iniciante | 0–49 |
| Em Ascensão | 50–149 |
| Engajado | 150–399 |
| Visionário | 400–899 |
| Transformador | 900+ |

O nível libera itens da loja (`nivelMinimo`).

### 3.2 Temporada

Trimestre civil (`2026-T4` = out–dez). O ranking soma o XP dos eventos da temporada corrente.

---

## 4. Regras de pontuação

Todos os valores estão em `TipoEvento` (backend). Cada evento tem uma **chave única** `tipo:refId:userId`; repetir a ação não pontua duas vezes.

### 4.1 Operador

| Evento | XP | Asas | Quando |
|--------|---:|-----:|--------|
| `IDEIA_ENVIADA` | 2 | 0 | Ideia criada. Máximo de 3 ideias pontuadas por dia. Ideia muito parecida com outra já existente não pontua |
| `IDEIA_APROVADA` | 20 | 10 | Gestor aprova |
| `IDEIA_CONVERTIDA` | 40 | 30 | Ideia vira projeto |
| `PROJETO_CONCLUIDO_AUTOR` | 80 | 100 | Projeto nascido da ideia é concluído |
| `RETORNO_AUTOR` | 0 | 1 por R$ 1.000 de retorno mensal (teto 150) | Na conclusão, se houver retorno registrado |
| `FEEDBACK_RECEBIDO` | 3 | 0 | Ideia reprovada com feedback. Uma vez por temporada ("Aprendiz") |
| `APOIO_RECEBIDO` | 1 | 0 | Gestor ou líder apoia a ideia (1 apoio por pessoa; autor não apoia a própria) |

**Multiplicadores** (aplicados a `IDEIA_APROVADA`, `IDEIA_CONVERTIDA` e `PROJETO_CONCLUIDO_AUTOR`):

- **x1,5** se a ideia está ligada a uma **campanha patrocinada ativa** (orientação com prazo de campanha não vencido).
- **x1,5** se a orientação é da categoria **SEGURANCA** (primeiro valor do grupo).
- Os dois se acumulam (x2,25).

**Ideia reprovada vale 0** (além do "Aprendiz" uma vez por temporada). Não há penalidade: errar faz parte.

### 4.2 Gestor

| Evento | XP | Asas | Quando |
|--------|---:|-----:|--------|
| `AVALIACAO_NO_PRAZO` | 10 | 0 | Avaliou em até 7 dias da criação |
| `AVALIACAO_FORA_PRAZO` | 3 | 0 | Avaliou depois de 7 dias |
| `FEEDBACK_UTIL` | 5 | 5 | O autor marcou a avaliação como útil |
| `APROVACAO_CONVERTIDA` | 15 | 0 | Ideia que ele aprovou virou projeto |
| `PROJETO_NO_PRAZO` | 50 | 50 | Projeto sob sua responsabilidade concluído até o prazo |
| `ROI_POSITIVO` | 20 | ROI% ÷ 10, entre 5 e 50 | Projeto concluído com ROI positivo |

Aprovar sem converter não gera bônus. A avaliação é atribuída a quem avaliou (`avaliadorUserId`).

### 4.3 Líder

| Evento | XP | Asas | Quando |
|--------|---:|-----:|--------|
| (criar orientação) | 0 | 0 | Criar orientação não pontua |
| `IDEIA_ALINHADA` | 3 | 0 | Ideia pontuada criada numa orientação do líder |
| `PROJETO_CONCLUIDO_ESTRATEGIA` | 60 | 0 | Projeto de uma orientação do líder é concluído |

O líder ganha pelo engajamento e pelo resultado que a estratégia dele gera. Líder não acumula Asas: o papel dele na economia é **patrocinar campanhas** e **manter o catálogo**.

### 4.4 Campanhas patrocinadas

Na orientação, o líder pode definir:

- `campanhaFim` (data): enquanto não vencer, a campanha está ativa e aplica o multiplicador x1,5.
- `campanhaBonusAsas` e `campanhaTopN`: ao encerrar (`POST /orientacoes/{id}/campanha/encerrar`), os N autores com mais XP ganho em ideias da campanha recebem o bônus (`CAMPANHA_BONUS`).

### 4.5 Missões semanais (calculadas no servidor)

| Perfil | Missão | Recompensa |
|--------|--------|------------|
| Operador | Enviar 1 ideia na semana | 5 XP |
| Operador | Enviar 1 ideia de segurança na semana | 10 XP + 5 Asas |
| Gestor | Avaliar 3 ideias no prazo na semana | 10 XP + 5 Asas |
| Gestor | Fila da **sua área** sem ideias pendentes há mais de 7 dias | 10 XP |
| Líder | Receber 3 ideias alinhadas na semana | 10 XP |
| Líder | Catálogo com ao menos 1 prêmio ativo com estoque | 10 XP |

Ao concluir, o prêmio entra no extrato (`MISSAO_CONCLUIDA`, chave por missão e semana). Prêmio de missão não conta para outras missões.

### 4.6 Sequência semanal

Semanas seguidas (até a atual ou a anterior) com pelo menos um evento de valor. Semanal, e não diária, porque o público trabalha em turnos.

### 4.7 Conquistas com tiers reais

Cada conquista mede **um** número e tem quatro metas: **Bronze 1, Prata 3, Ouro 10, Platina 25**. Título, descrição, progresso, meta do próximo tier e data de desbloqueio vêm da API (fim da divergência entre texto e regra).

| Perfil | Conquista | Mede |
|--------|-----------|------|
| Todos | Bem-vindo | Primeiro acesso |
| Operador | Voz do campo (`PRIMEIRA_IDEIA`) | Ideias pontuadas |
| Operador | Ideia validada (`PRIMEIRA_APROVACAO`) | Ideias aprovadas |
| Operador | Do papel ao projeto (`EM_PROJETO`) | Ideias convertidas |
| Operador | Resultado entregue (`TRANSFORMADOR`) | Projetos concluídos de suas ideias |
| Operador | Alinhado à estratégia (`INOVADOR`) | Ideias aprovadas em campanha ativa |
| Operador | Guardião da segurança (`GUARDIAO_SEGURANCA`) | Ideias de segurança aprovadas |
| Operador | Apoio da equipe (`INFLUENCIADOR`) | Apoios recebidos |
| Gestor | Curador ágil (`CURADOR`) | Avaliações no prazo |
| Gestor | Mentor (`MENTOR`) | Feedbacks marcados úteis |
| Gestor | Ideias que viram projeto (`LIDER_RESULTADOS`) | Aprovações convertidas |
| Gestor | Entrega no prazo (`EXECUTOR`) | Projetos concluídos no prazo |
| Líder | Estratégia que engaja (`ESTRATEGISTA`) | Ideias alinhadas recebidas |
| Líder | Portfólio que entrega (`TRANSFORMADOR_ESTRATEGICO`) | Projetos concluídos nas suas orientações |
| Líder | Reconhecedor (`VISIONARIO_ESTRATEGICO`) | Prêmios ativos no catálogo |

---

## 5. Loja de Recompensas

Catálogo gerido pelo Líder. Itens ancorados nos negócios do grupo, com custo marginal baixo:

| Item (seed) | Categoria | Custo (Asas) | Nível mínimo | Parceiro |
|-------------|-----------|-------------:|--------------|----------|
| Passagem rodoviária Águia Branca | `VIAGEM` | 120 | Em Ascensão | Viação Águia Branca |
| 30% na revisão do seu carro | `VEICULO` | 80 | Iniciante | Divisão Comércio |
| Jogo de pneus Michelin com desconto | `VEICULO` | 150 | Engajado | Michelin / Divisão Comércio |
| Day-off de reconhecimento | `TEMPO` | 200 | Engajado | RH Águia Branca |
| Vale-compras R$ 100 | `VALE` | 60 | Iniciante | Parceiro de varejo |
| Trilha de capacitação | `CARREIRA` | 40 | Iniciante | Universidade Corporativa GAB |
| Almoço com a diretoria | `CARREIRA` | 250 | Visionário | Holding Águia Branca |
| Vaga no Demoday InovaGAB | `CARREIRA` | 400 | Visionário | Prêmio InovaGAB |

A categoria é texto livre (o Líder pode criar outras); o app mostra rótulos legíveis para as cinco do catálogo inicial.

**Fluxo do resgate:** pedido válido entra `APROVADO` → `ENTREGUE`. `NEGADO` não é mais transição.

- Ao solicitar: confere saldo, estoque e nível; se passar, debita as Asas (`RESGATE`), reserva 1 unidade e já fica `APROVADO`.
- Sem veto pessoa a pessoa. Quem não passa na vitrine recebe 409 com o mesmo texto de `motivoBloqueio`.
- Somente Operador e Gestor resgatam. Somente Líder mantém o catálogo e marca `ENTREGUE` (não o próprio pedido).
- Quem entrega o benefício na vida real (RH, garagem, concessionária) segue [`ENTREGA_RECOMPENSAS.md`](./ENTREGA_RECOMPENSAS.md).

---

## 6. Ranking

- `GET /gamificacao/ranking?perfil=&escopo=DIVISAO|GRUPO&temporada=&pagina=&tamanho=`: ordena **todos** os usuários ativos do perfil (inclusive com 0 XP) por XP da temporada. A resposta traz uma página (`pagina` começa em 0, `tamanho` padrão 50, máximo 200), `totalParticipantes` com a contagem inteira e `minhaEntrada` mesmo quando a pessoa está fora da página. Retorna só nome, divisão, equipe, XP e nível; nada de ideias.
- `zonaPremio = true` para os 3 primeiros operadores de cada divisão **com XP maior que zero**. Quem está com 0 XP aparece na lista, mas não ocupa a zona de prêmio.
- `GET /gamificacao/indicacoes-inovagab?temporada=` (somente Líder): lista esses operadores da zona de prêmio, por divisão. É o retrato que o comitê usa na categoria Imova-se. O app não inscreve ninguém sozinho e não envia e-mail.
- `GET /gamificacao/ranking/equipes`: gincana por equipe/base (soma do XP de temporada dos operadores da equipe e média por pessoa), no espírito da AMIGAB.
- Perfis que podem ver cada aba: Operador vê Operadores; Gestor vê Gestores e Operadores; Líder vê todos.

---

## 7. Antitrapaça

| Ataque | Defesa |
|--------|--------|
| Repetir a mesma ação | Chave única `tipo:refId:userId` com índice único no MongoDB |
| Criar e apagar ideias | Exclusão estorna todos os eventos da ideia (`ESTORNO` no extrato) |
| Spam de ideias | Limite de 3 ideias pontuadas por dia; ideia pouco pontua sozinha |
| Ideia copiada | Similaridade de título (Jaccard ≥ 0,7 sobre palavras normalizadas) marca `duplicadaDe` e não pontua |
| Gestor aprovando tudo | Aprovação em si vale 0; só ganha se a ideia virar projeto |
| Votar na própria ideia | Bloqueado; 1 apoio por pessoa |
| Resgatar sem saldo | Saldo, estoque e nível validados no servidor |
| Líder subindo de nível criando orientações | Criar orientação vale 0 |

---

## 8. Endpoints

| Método | Rota | Perfis |
|--------|------|--------|
| GET | `/api/v1/gamificacao/me` | Todos |
| GET | `/api/v1/gamificacao/extrato` | Todos |
| POST | `/api/v1/gamificacao/novidades/vistas` | Todos |
| GET | `/api/v1/gamificacao/ranking` | Todos (abas conforme §6) |
| GET | `/api/v1/gamificacao/ranking/equipes` | Todos |
| GET | `/api/v1/gamificacao/indicacoes-inovagab` | Líder |
| GET | `/api/v1/recompensas` | Todos |
| POST / PUT | `/api/v1/recompensas`, `/api/v1/recompensas/{id}` | Líder |
| POST | `/api/v1/recompensas/{id}/resgatar` | Operador, Gestor |
| GET | `/api/v1/resgates/meus` | Todos |
| GET | `/api/v1/resgates?status=` | Líder |
| PUT | `/api/v1/resgates/{id}/status` | Líder (só `ENTREGUE`; não o próprio) |
| POST | `/api/v1/ideias/{id}/apoio` | Gestor, Líder (não autor) |
| POST | `/api/v1/ideias/{id}/feedback-util` | Operador autor |
| POST | `/api/v1/orientacoes/{id}/campanha/encerrar` | Líder |

Exemplos de payload em [`REQUISITOS_SPRINT2.md`](./REQUISITOS_SPRINT2.md) §8.2.

---

## 9. O que o app mostra

| Perfil | Home | Ranking | Perfil |
|--------|------|---------|--------|
| Operador | Card de nível (XP e Asas) e card da Economia (temporada, novidade, Loja, Extrato) | Temporada, zona de prêmio, missões, gincana de equipes | Nível, Economia, conquistas reais |
| Gestor | Card da fila (pendentes e missões de SLA) e card da Economia | Gestores e Operadores | Idem, com impacto dos projetos |
| Líder | Card da Economia com XP e atalho do catálogo | Todos os perfis | Catálogo e confirmação de entrega |

Telas novas: **Loja** (saldo, itens disponíveis primeiro, motivo do bloqueio, confirmação, meus resgates), **Carteira** (XP, Asas, "Meu impacto" e extrato com estornos riscados) e **Gestão de recompensas** (Líder: abas de catálogo e entregas). O detalhe da ideia ganha "Este feedback foi útil?" para o autor e o botão de apoio para Gestor e Líder; o formulário de orientação ganha os campos de campanha patrocinada e o detalhe mostra o encerramento com os vencedores.

Avisos de pontos novos (`novidades`) disparam a animação de comemoração já existente.

---

## 10. Dados de demonstração

O `GamificacaoSeeder` (roda depois do `DataSeeder`) cria colegas em Passageiros, Logística e Comércio, com equipes (`Garagem Vitória`, `CD Serra`, `Concessionária Vila Velha`…), e um histórico de eventos na temporada para que o ranking da demo seja real. Também semeia o catálogo da loja e uma campanha patrocinada na orientação de Segurança. Os eventos semeados usam `refId` com prefixo `seed-`, exceto os das ideias de demonstração do Pedro, que usam a chave real para não pontuar duas vezes.
