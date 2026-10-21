# Plano — Marca Grupo Águia Branca no InovaGAB (site + app)

**Projeto:** InovaGAB · Challenge FIAP 2026  
**Objetivo:** site e aplicativo com a mesma régua de marca do Grupo Águia Branca atual — não “inspirados”, mas identificáveis como parte do grupo.  
**Contexto:** projeto acadêmico autorizado a usar a marca para fins educacionais. Não é peça oficial do Grupo; é uma proposta de produto incubado.

---

## 1. Fontes de verdade da marca

| Fonte | O que manda |
|-------|-------------|
| [gab.com.br](https://gab.com.br) | Site institucional atual: Poppins, foto real, branco, azul sólido, blocos roxo/lima, botões retangulares, cards de foto com overlay, wordmark gigante “Inovação” |
| [aguiabranca.com.br/marca](https://www.aguiabranca.com.br/marca) | Guia de marca 2023: asa como símbolo proprietário, três azuis, paleta brasilidade, iconografia linear, tipografia geométrica |
| `docs/MARCA_GAB.md` | Negócio, propósito, valores, divisões, programa InovaGAB, implicações de produto |
| `docs/IDENTIDADE_AGUIA_BRANCA.md` | Tokens amostrados, regra “corpo da holding + acentos brasilidade” |
| SVG oficial `logo_GA_Bvetorizado` | Lettering `#002361`, asa `#005FC5`; versão branca para fundo azul/foto |
| Pranchas do guia (`cores`, `FONTE`, `iconografia`, `asa`) | Fotografia da brasilidade, tipografia geométrica, ícones lineares azuis |

**Regra de ouro:** se uma decisão não existe no site do Grupo ou no guia, ela não entra. Nada de gradiente roxo de IA, glassmorphism, emoji, card flutuante genérico ou copy de landing de SaaS.

---

## 2. Sistema de marca extraído

### Cores

| Token | Hex | Uso |
|-------|-----|-----|
| Marinho institucional | `#002361` | lettering do logo, títulos, superfícies profundas |
| Azul da asa | `#005FC5` | símbolo, links, ícones, destaques |
| Azul de superfície | `#003071` / `#013171` | faixas institucionais, footer, inovação |
| Azul de ação | `#008FFF` | CTA pontual, dados, IA |
| Fundo claro | `#F1F4F7` | página |
| Container claro | `#E8F0FA` | cards, seções |
| Texto | `#101828` | corpo |
| Lima atual | `#F1FF89` | acento de marca no site atual; headings de footer, destaque |
| Amarelo | `#D7D624` | status, gamificação |
| Verde | `#00A544` | sucesso |
| Laranja | `#E87C1A` | aviso, Asas |
| Ciano | `#00B7D7` | dado / insight |
| Roxo | `#7A2E85` | categoria / inovação / bloco de destaque |

### Tipografia

- **Poppins** em todo o site (300–700). O site do Grupo não usa outra família.
- App: trocar Plus Jakarta Sans/Inter por **Poppins** (pesos 400/500/600/700) na fatia A1.
- Títulos grandes, pesados, alinhados à esquerda; corpo com line-height generoso.

### Forma e componentes

- Botões **retangulares arredondados** (12–16px), não pílula.
- Cards 16–24px de raio; foto com gradiente azul e texto branco; card claro `#E8F0FA` com título azul.
- Blocos de destaque em roxo e lima, como no site do Grupo.
- Ícones lineares 2px em `#005FC5`; nada de ícone preenchido colorido aleatório.
- Wordmark gigante de fundo (“Inovação”) em faixa azul sólida.
- Fotografia real de pessoas e operação, com overlay azul; nunca ilustração genérica.

### Tom de voz

- Propósito: **“Mover o mundo com excelência e respeito às pessoas.”**
- Inovação: **“Inovação que move o futuro.”**
- Valores: **Segurança, Relacionamento, Integridade & Confiança, Evolução.**
- Segurança é o primeiro valor; feedback é relacionamento; recompensa é concreta; resultado acima de volume.
- Nunca chamar de “revolucionário”, “disruptivo”, “poderoso”. Falar de operação, pessoas, pátio, oficina, resultado.

---

## 3. Mapa de fatias — SITE

| Fatia | Persona | Escopo | Pronto quando |
|-------|---------|--------|---------------|
| **S1 · Shell de marca** | Brand designer sênior | Nav flutuante branca com logo oficial do Grupo + “InovaGAB”; Poppins; botões retangulares; footer azul com headings lima | Nenhum resquício de Inter/pílula/gradiente de IA; logo SVG oficial aplicado com respiro |
| **S2 · Hero fotográfico** | Diretor de arte | Foto real de colaboradores do Grupo, overlay azul, título branco com acento lima, CTA branco, telefone do produto | Hero parece campanha institucional do Grupo, não landing de SaaS |
| **S3 · Desafio + pilares** | UX writer | Cards claros, valores do Grupo, história da “Oficina sem papel” | Copy 100% ancorada em `MARCA_GAB.md`; sem adjetivo genérico |
| **S4 · Demonstração** | Product designer | Faixa azul sólida `#003071`, wordmark gigante “Inovação”, três perfis, telefone; remover glow/dot grid | Demo legível e com a mesma linguagem da faixa de inovação do site do Grupo |
| **S5 · Prêmio InovaGAB** | Content strategist | Fotos reais (troféus, operação, equipe), categorias Imova-se, Demoday, Oscar, GAB Experience | O programa real aparece; fica claro que o app é a camada digital, não um programa paralelo |
| **S6 · Economia + campo** | Product designer | Extrato, Asas, loja, valores; acentos lima/roxo | Reconhecimento concreto e legível, com a paleta do Grupo |
| **S7 · CTA + footer** | Brand designer | Faixa azul, headings lima, QR do APK, logo oficial, nota de autorização acadêmica | Rodapé idêntico em linguagem ao do Grupo |
| **S8 · Auditoria** | QA | Desktop/mobile, contraste, console, Lighthouse, deploy | Zero erro de console, zero overflow, deploy no ar |

---

## 4. Mapa de fatias — APP

Baseado na auditoria de UX dos três perfis.

| Fatia | Persona | Escopo | Pronto quando |
|-------|---------|--------|---------------|
| **A1 · Tokens e tipografia** | Design system engineer | Poppins no app, lima como acento, raios, botões, status text-safe, ícones | App abre com Poppins; contraste AA nos textos de status; testes verdes |
| **A2 · Operador** | Product designer | Home “contribuição primeiro”; CTA “Compartilhar Ideia” visível; status textual no card; hero com o visual do demo do site (brilho azul, tipografia limpa) | Primeira dobra mostra minhas ideias + CTA; status igual em lista e detalhe; sem mudar view-model |
| **A3 · Gestor** | UX lead | Pipeline “Aprovadas” correto; empty states por filtro; “Em avaliação”; feedback obrigatório | Números do Gestor e do Líder contam a mesma história; filtro vazio não mente |
| **A4 · Líder** | A11y + data viz | Hero de orientação quando vazio; funil “Ideias → Aprovadas → Em projeto”; insight IA lido pelo TalkBack | TalkBack lê título/resumo/evidência/ação; labels iguais em home e dashboard |
| **A5 · Loja e estados** | Ops designer | Solicitado / Aprovado / Entregue / Negado; copy de “mecânicos” corrigida; strings duplicadas removidas | Status de resgate sem ambiguidade; sem promessa de “sofrimento” |
| **A6 · Acessibilidade compartilhada** | A11y specialist | Alvos de 48dp, chips com estado selecionado, posição no ranking anunciada | TalkBack e toque funcionam no campo, com luva, no sol |
| **A7 · Marca no app** | Brand designer | Logo do Grupo como endosso no login/sobre; InovaGAB como marca-produto; paleta atual | Login mostra “InovaGAB · Grupo Águia Branca”; nada de marca inventada |
| **A8 · Auditoria** | QA | `./gradlew testDebugUnitTest`, lint, build release, screenshots claro/escuro | Suíte verde; APK novo sem regressão |

---

## 5. Item específico do Operador no demo (“Um fluxo, três perspectivas”)

O telefone da demonstração no site é a **referência visual** do novo home do Operador:

- Hero azul com brilho suave (não glassmorphism), título “Você viu algo que pode melhorar?”, CTA branco “Compartilhar Ideia”.
- “Orientações estratégicas” com a orientação real **Eficiência operacional nas 690 bases**.
- “Minhas ideias” com **Oficina sem papel**, status **Aguardando avaliação**, 7 apoios.
- Tipografia Poppins, cards de 16px, ícones lineares, contraste alto.
- Wording do demo e do app idênticos: “O papel acaba no meio do turno. Sem a folha, a ordem de serviço não sai e o veículo fica parado.”

**Regra:** o app recebe o visual do demo, não o contrário. O demo não inventa um fluxo que o app não tem.

---

## 6. Fora de escopo / não mexer

- Não alterar a matemática do backend (`DashboardService`, funil, ROI).
- Não alterar `GetDashboardUseCase.ideiasAprovadas` sem atualizar testes.
- Não renomear “Imova-se” (nome real do programa).
- Não trocar a paleta base do logo (`#002361` / `#005FC5`).
- Não apagar telas, permissões ou rotas testadas.
- Não usar imagens do Grupo sem crédito; o uso é acadêmico e autorizado.

---

## 7. Ordem de execução

1. S1 → S2 → S3 → S4 (site visível)
2. S5 → S6 → S7 (conteúdo e fechamento)
3. S8 (deploy + auditoria)
4. A1 (tokens) → A2 (Operador) → A3 (Gestor) → A4 (Líder)
5. A5 → A6 → A7 → A8 (auditoria final)

Cada fatia fecha com **PRONTO**: evidência no chat, sem regressão, sem placeholder.

---

## 8. Status da execução (20/10/2026)

**Concluído**

- Fonte de marca: site atual do Grupo, guia 2023, SVG oficial do logo, prancha de cores/fonte/iconografia, fotos reais.
- Site S1–S7: Poppins, nav com logo oficial, hero fotográfico, cards roxo/lima, faixa de inovação, Prêmio InovaGAB com fotos, economia, valores, CTA/footer com nota de autorização.
- App A1, A2, A3 (contagem do pipeline), A4 (rótulo do funil) e A7 (endosso do Grupo no login).
- Demo novo: `docs/DEMO_CASES.md` com 10 casos ativos + 1 não priorizada, 4 projetos, funil 11 → 3 → 3, ROI 192,7% a.a.; seed, site e app alinhados.
- Qualidade: 241 testes backend + 268 testes Android, 0 falhas; site sem erro de console/overflow; APK novo com Poppins.

**Próximas fatias de polimento**

- A3: filtros da lista (`Aprovadas` x `Convertidas`) e empty states por filtro.
- A4: hero de orientação vazia no Líder e leitura completa do insight por TalkBack.
- A5: estados da Loja (solicitado/aprovado/entregue/negado) e revisão das strings duplicadas.
- A6: alvos de 48dp, estado selecionado dos chips e posição no ranking anunciada.
