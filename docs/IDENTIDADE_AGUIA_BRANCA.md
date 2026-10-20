# Identidade visual — InovaGAB no território do Grupo Águia Branca

**Challenge FIAP 2026 — Grupo Águia Branca**  
**Escopo:** material de marca para o app de inovação colaborativa InovaGAB.  
**Data da pesquisa:** 19/10/2026.

Este dossiê substitui a paleta inventada no código (“Premium Blue” + “Electric Mint”). O app passa a se identificar com a holding e com o programa de inovação do grupo, não com uma paleta de produto genérica e não com a campanha de venda de passagem.

---

## 1. Fontes consultadas

| Fonte | O que prova |
|-------|-------------|
| [gab.com.br](https://gab.com.br/) | Propósito, divisões, discurso institucional, logotipo vetorial publicado, CSS do site |
| [gab.com.br/inovacao](https://gab.com.br/inovacao) | Prêmio InovaGAB, Imova-se, Demoday, inovação aberta, pilares de inovação |
| [aguiabranca.com.br/marca](https://www.aguiabranca.com.br/marca) | Guia de marca da Viação (2023): pilares, arquétipo, asa, brasilidade |
| Caleb Design + Hagens (cobertura 2023) | Reposicionamento da Viação: três azuis, asa como elemento gráfico, paleta de apoio |
| Manual antigo (SlideShare, 2016) | Pantone 285 C / 288 C e Helvetica Neue Condensed — histórico, não é a referência viva |
| Código do app (`Color.kt`, `ic_inovagab_logo.xml`) | Paleta atual: `#0A2540`, `#FF5C00`, `#00D4B2` — inventada, a retirar |

O guia público da Viação descreve a paleta (“céu, mar, flora, cultura”) e não publica hex. Os tokens abaixo foram **amostrados** do SVG oficial da holding e do CSS de [gab.com.br](https://gab.com.br/). Não são um manual de marca recebido do grupo.

---

## 2. Duas marcas, um app

O Grupo Águia Branca e a Viação Águia Branca compartilham a asa e o azul, mas ocupam territórios diferentes.

**Holding (gab.com.br)** — institucional, mobilidade e logística desde 1946. Três divisões: Passageiros, Logística e Comércio. Tom de confiança, excelência e propósito. O InovaGAB nasce aqui: é a plataforma digital do movimento de inovação da holding.

**Viação (aguiabranca.com.br)** — marca de encontro e reencontro, campanha “Vai com tudo. Vai de Águia Branca.” Paleta mais colorida (verde, amarelo, laranja, fúcsia) para peças de comunicação com o passageiro.

**Regra do app:** corpo institucional da holding (dois azuis, fundo claro, asa). A paleta brasilidade da Viação entra só em gráfico, status e gamificação. O InovaGAB não pode parecer app de venda de passagem.

---

## 3. Proposta de valor que o app carrega

**Propósito do grupo:** “Mover o mundo com excelência e respeito às pessoas.”

**Inovação, nas palavras deles:** “Inovação que move o futuro.” Em todas as divisões, a inovação é guiada pela estratégia e orientada à geração de valor — experiência do cliente, performance e negócio sustentável. Da ideia ao resultado.

**Prêmio InovaGAB (programa real que o app espelha):**

| Categoria | O que é | Como o app traduz |
|-----------|---------|-------------------|
| Imova-se | Projetos internos, intraempreendedorismo, dor → ideia | Operador captura dores e oportunidades |
| Demoday | Solução já implementada, cultura de experimentação | Gestor estrutura a ideia em projeto |
| Inovação aberta | Startups, universidades, desafios estratégicos | Radar / parcerias (Sprint 1; fora do mínimo da Sprint 2) |
| Oscar da Inovação + GAB Experience | Reconhecimento e imersão | Gamificação, ranking, protagonismo |

**Pilares publicados em gab.com.br/inovacao:** Governança (comitês, portfólio, métricas), Pessoas e Cultura (capacitação, engajamento), Ecossistema (parcerias, inovação aberta) e a base digital (gestão da inovação, plataforma data driven, laboratórios).

O fluxo do app — capturar, estruturar, acompanhar — é o mesmo arco “da ideia ao resultado”. Três perfis: Operador, Gestor, Líder.

**Pilares da Viação que também cabem no produto interno:** segura, confiável, engajada, brasileira, inovadora, inspiradora. Arquétipo herói. Símbolo proprietário: a asa.

---

## 4. Tokens recomendados (amostrados)

### Núcleo de marca — logo da holding

Amostrados do SVG `logo_GA_Bvetorizado` publicado em gab.com.br:

| Token | Hex | Origem |
|-------|-----|--------|
| Marinho institucional | `#002361` | lettering do logo |
| Azul da asa | `#005FC5` | símbolo (asa/águia) do logo |

### Interface — CSS de gab.com.br

| Token | Hex | Uso no app |
|-------|-----|------------|
| Marinho de superfície | `#003071` | containers escuros, splash noturno |
| Marinho intermediário | `#013171` | hover / variante de superfície |
| Azul de ação | `#008FFF` | destaque, IA, botão de ênfase |
| Fundo de página | `#F1F4F7` | background claro |
| Fundo de container | `#E8F0FA` | cards, primaryContainer |
| Texto | `#101828` | onSurface claro |

### Acentos brasilidade — só gráfico, status e gamificação

Presentes no CSS do site da holding, alinhados ao discurso da Viação:

| Token | Hex | Papel |
|-------|-----|--------|
| Amarelo | `#D7D624` | nível / destaque positivo |
| Verde | `#00A544` | sucesso, conclusão |
| Laranja | `#E87C1A` | aviso, esforço |
| Ciano | `#00B7D7` | dado, insight |
| Roxo | `#7A2E85` | categoria, inovação |

### O que sai

| Hex | Por que sai |
|-----|-------------|
| `#0A2540` | Marinho inventado; o logo real é `#002361` |
| `#FF5C00` | Laranja de produto; o laranja do grupo é `#E87C1A` e não é cor de marca |
| `#00D4B2` | Menta “tech” sem lastro na identidade do grupo |

### Tipografia

- Site da holding: **Poppins**.
- App nesta leva: **Plus Jakarta Sans** (títulos) + **Inter** (corpo). Ambas são geométricas, de linhas limpas — o mesmo critério do guia da Viação.
- Troca para Poppins fica para uma leva seguinte (exige novos arquivos em `res/font/`).
- Manual de 2016 citava Helvetica Neue Condensed. É histórico.

---

## 5. Logo Asa-rota (aprovado)

**Conceito:** estrada que vira asa + ponto de ideia no topo. Wordmark **InovaGAB** (“Inova” em marinho, “GAB” em azul da asa).

**Arquivos-mestre (Google Flow):**

| Arquivo | Uso |
|---------|-----|
| `docs/brand/asa-rota-simbolo.jpg` | Símbolo isolado |
| `docs/brand/asa-rota-lockup.jpg` | Símbolo + nome |

**Derivados para o app** (trim, fundo transparente, PNG):

| Processado | Android (`res/drawable-nodpi/`) | Onde aparece |
|------------|----------------------------------|--------------|
| `processed/ic_inovagab_asa_rota_simbolo.png` | `ic_inovagab_logo.png` | Splash, referência do símbolo |
| `processed/ic_inovagab_asa_rota_lockup.png` | `ic_inovagab_lockup.png` | Login, header da home |
| `processed/ic_launcher_foreground.png` | `ic_launcher_foreground.png` | Foreground do launcher (432×432) |

Para regenerar a partir dos JPG (ImageMagick):

```bash
cd docs/brand
mkdir -p processed
for f in simbolo lockup; do
  convert "asa-rota-${f}.jpg" -fuzz 4% -trim +repage PNG32:"processed/tmp.png"
  convert "processed/tmp.png" -fuzz 4% -transparent white "processed/ic_inovagab_asa_rota_${f}.png"
done
```

O símbolo do app **não copia** o arquivo oficial da águia da holding; é marca do produto InovaGAB.

---

## 6. O que o app usa hoje

- Tokens em `Color.kt` (holding `#002361` / `#005FC5` / `#008FFF`)
- Gradiente do login e splash
- Bitmap Asa-rota (PNG com alpha), não JPG com fundo branco
- Launcher: fundo `#002361` + foreground Asa-rota

## Tema claro/escuro

A preferência fica no aparelho (DataStore `inovagab_tema`): Claro, Escuro ou Sistema. O padrão é Sistema. A Activity resolve o boolean e o passa a `InovagabTheme`; telas leem `LocalDarkTheme` e `MaterialTheme.colorScheme`, não o night do Android.

Claro: página `#F1F4F7`, container `#E8F0FA`, texto `#101828`, primary `#002361`, secondary `#005FC5`, ação `#008FFF`.

Escuro fechado (matiz marinho, profundidade tipo Telegram): fundo `#000814`, card `#0A1628`, card alto `#102033`, elevado/nav `#15283F`. Primary do chrome e botão preenchido: asa `#005FC5` com texto branco. `#008FFF` permanece só como token de ação pontual (IA), não como `colorScheme.primary`. Tertiary `#E87C1A` só em aviso e gamificação. Corpo `#F1F4F7`; secundário `#8B9CB5`; borda de card branco ~10% (`#FFFFFF1A`).

Acentos `#D7D624`, `#00A544`, `#E87C1A`, `#00B7D7` e `#7A2E85` continuam em medalha, status e gráfico. O splash noturno do sistema permanece `#002361`.

**Depois**

- Troca de família tipográfica para Poppins
- Vetor manual opcional (substituir PNG se quiser escala infinita sem raster)

---

## 7. Prompt completo para o Google Flow

Cole o bloco abaixo. As cores são as amostradas da holding. O laranja e a menta do prompt anterior estão fora.

```text
Crie uma prancha de logotipos para o app "InovaGAB", plataforma interna de inovação colaborativa do Grupo Águia Branca, holding brasileira de mobilidade e logística desde 1946, com divisões de Passageiros, Logística e Comércio.

O produto digitaliza o Prêmio InovaGAB: operadores de campo capturam dores do dia a dia, gestores estruturam ideias em projetos, líderes acompanham resultado. O arco da marca é "da ideia ao resultado". O propósito do grupo é "Mover o mundo com excelência e respeito às pessoas." O slogan da inovação é "Inovação que move o futuro."

O app precisa ser reconhecido imediatamente como Grupo Águia Branca. A identidade institucional da holding usa dois azuis amostrados do logotipo oficial publicado em gab.com.br: marinho #002361 no lettering e azul #005FC5 na asa. A interface do site usa ainda o azul de ação #008FFF, fundos #F1F4F7 e #E8F0FA. A Viação Águia Branca, marca irmã, trata a ASA como símbolo proprietário: fundo, moldura e elemento gráfico. Arquétipo herói. Pilares: segura, confiável, engajada, brasileira, inovadora, inspiradora.

Gere, numa única imagem em fundo #F1F4F7, uma grade com 4 conceitos, cada um em duas versões lado a lado: (A) só o símbolo, centralizado, e (B) o símbolo à esquerda do wordmark "InovaGAB". Rótulo cinza sob cada conceito: "1 Asa institucional", "2 Três voos", "3 G com asa", "4 Wordmark".

Conceito 1 — Asa institucional. Uma asa geométrica, abstrata, em voo para a direita e para cima, feita de no máximo três planos. Fundo circular ou rounded square em #002361. Asa em #005FC5. Um corte interno branco para leitura a 48 pixels. Sem penas realistas, sem águia completa.

Conceito 2 — Três voos, um destino. Três traços grossos (capturar, estruturar, acompanhar) nascem separados e se fundem numa única asa. Cores só #002361, #005FC5 e branco. O encontro dos três pode receber um ponto #008FFF, mínimo.

Conceito 3 — G com asa. A letra G estilizada é o símbolo. O corte interno do G vira uma asa. G em #002361, asa em #005FC5. Geometria bold, legível a 48 pixels.

Conceito 4 — Wordmark com personalidade. O nome é o herói. "Inova" em #002361 e "GAB" em #005FC5, fonte geométrica sem serifa, peso semibold (próxima de Poppins ou Plus Jakarta Sans). O "A" de GAB tem um corte que sugere asa. Símbolo pequeno à esquerda, derivado da mesma asa.

Estilo de todos: vetor flat, geométrico, traços grossos, poucos elementos, institucional, contemporâneo, brasileiro. Tem que funcionar como ícone de app Android em 48 pixels e no header. Alto contraste.

Cores permitidas no símbolo e no wordmark, e só estas: #002361, #005FC5, #FFFFFF. Acento opcional mínimo de #008FFF só no ponto da ideia. Fundo da prancha #F1F4F7.

Proibido: círculo com seta para cima, lâmpada, foguete, águia clipart, águia realista, penas detalhadas, linhas finas, detalhes miúdos, 3D, fotorrealismo, sombra projetada, mockup de celular, slogan, texto extra, marca d'água, laranja #FF5C00, menta #00D4B2, qualquer verde, amarelo, fúcsia ou roxo no símbolo (esses tons existem na paleta brasilidade do grupo e ficam só para gráficos do app, não para o logo).
```
