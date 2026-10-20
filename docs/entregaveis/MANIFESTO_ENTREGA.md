# Manifesto de Entrega — InovaGAB Sprint 2

**Challenge FIAP 2026 — Grupo Águia Branca**  
**Data de validação:** 21/09/2026

## Equipe

- Giuliana Abe Takara — RM 562736 — 2TDSA
- André Luiz Oliveira da Silva — RM 565836 — 2TDSA

## Entregáveis

- **Pacote único para upload:** `InovaGAB_Sprint2_Entrega_Final.zip`
- **E1 — Backend:** `inovagab-backend-sprint2.zip`
- **E2 — Projeto Android:** `inovagab-mobile-sprint2.zip`
- **E3 — Executável Android:** `InovaGAB_Sprint2.apk`
- **E4–E6 — Apresentação, endpoints e IA:** `InovaGAB_Documentacao_Tecnica_Sprint2.pdf`
- **E5 — Contrato de máquina:** `openapi-sprint2.json`

## Validações executadas

- Backend: 128 testes, 0 falhas
- Android: 206 testes, 0 falhas
- `lintVitalRelease`, `assembleDebug` e `assembleRelease`: sucesso
- Smoke HTTP 400/401/403/404 e três perfis: sucesso
- Fluxo API ideia → IA/fallback → aprovação → projeto → dashboard: sucesso
- Login no emulador como Operador, Gestor e Líder: sucesso
- APK release: alinhado, assinado, verificado e instalado com sucesso
- ZIPs: teste de integridade sem erros
- Pacotes: sem `.env`, `local.properties`, caches, `build/` ou `target/`

## Observação sobre a IA

A entrega implementa e documenta duas opções do enunciado:

- **IA-A:** avaliação, scoring e priorização de ideias para o Gestor;
- **IA-C:** relatório executivo dos resultados do dashboard para o Líder.

A integração chegou ao Google Gemini durante a validação. O provedor respondeu HTTP 429
por esgotamento da quota gratuita; a resposta degradada `fallback=true` foi acionada e
manteve o fluxo funcional. Para demonstrar o modelo real, utilize uma chave com quota
disponível em `GEMINI_API_KEY`.

## SHA-256

```text
6c01f0d546b2ec522ed93c82de4af95edb672f117264235213c9f22d0d33f492  InovaGAB_Documentacao_Tecnica_Sprint2.pdf
75ac7ffea0a134a40d0a88f8ed9e9a0d486d97a063b122570384aea753c2de33  openapi-sprint2.json
87b565fbe2cf1a9578142da723e7f8a9b1e6d7344e6ab31865b665ddace91db3  InovaGAB_Sprint2.apk
919ec11d06ac350eec5cc1d2b03c6928ac7e1e6a67c1149c3b36f63c60ad8ef2  inovagab-backend-sprint2.zip
b86fc5b6bfd03d7ee5348495cac1d6837c114345d6950ccd0b3c84b5ac2c4b13  inovagab-mobile-sprint2.zip
```

Para conferir:

```bash
sha256sum -c SHA256SUMS.txt
```
