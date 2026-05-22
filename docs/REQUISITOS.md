# Levantamento Completo de Requisitos
## Plataforma de Inovação Corporativa - Grupo Águia Branca

> Documento extraído do material oficial do Challenge FIAP 2026

---

## 1. SOBRE A EMPRESA - GRUPO ÁGUIA BRANCA

### 1.1 Dados Corporativos

| Informação | Valor |
|------------|-------|
| **Setor** | Transporte e Logística |
| **Porte** | Um dos maiores conglomerados de transporte e logística do Brasil |
| **Colaboradores** | +20.000 funcionários |
| **Empresas** | Mais de 25 empresas no grupo |
| **Propósito** | "Mover o mundo com excelência e respeito às pessoas" |
| **Valores** | Inovação, Ética, Respeito às pessoas |

### 1.2 Divisões de Negócios

O grupo opera em três divisões independentes:

1. **PASSAGEIROS** - Transporte de pessoas
2. **COMÉRCIO** - Atividades comerciais
3. **LOGÍSTICA** - Transporte de cargas e operações logísticas

### 1.3 Contexto de Inovação Atual

- Já possui iniciativas de inovação que geram resultados
- Possui um sistema de suporte atual (legado)
- Busca ampliar integração entre estratégia, execução e mensuração
- Necessidade de maior visibilidade, escalabilidade e captura de valor

---

## 2. PROBLEMA DE NEGÓCIO

### 2.1 Desafios Identificados

| Desafio | Descrição |
|---------|-----------|
| **Conexão Estratégica** | Conectar o direcionamento da alta gestão à execução prática na ponta |
| **Visibilidade do Funil** | Acompanhar jornada completa: ideia inicial → projetos → resultados |
| **Engajamento Operacional** | Incluir colaboradores de TODOS os níveis no processo criativo |
| **Mensuração de Valor** | Demonstrar impacto real e ROI das iniciativas |

### 2.2 O Que NÃO Buscam

> "NÃO BUSCAMOS APENAS UM APP DE IDEIAS"
> 
> "Buscamos um sistema capaz de conectar estratégia, execução e resultados, transformando inovação em valor real."

---

## 3. SÍNTESE DO DESAFIO

Desenvolver uma **solução mobile de gestão de inovação corporativa** estruturada para integrar:

- **Estratégia**
- **Pessoas**
- **Processos**
- **Tecnologia**

Em um único ambiente, promovendo engajamento de colaboradores em **todos os níveis**.

### 3.1 Três Pilares Funcionais

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│   CAPTURAR  │ ───► │  ESTRUTURAR │ ───► │ ACOMPANHAR  │
│             │      │             │      │             │
│ Dores e     │      │ Transformar │      │ Monitorar   │
│ oportunida- │      │ em inicia-  │      │ até resul-  │
│ des do dia  │      │ tivas ali-  │      │ tados men-  │
│ a dia       │      │ nhadas      │      │ suráveis    │
└─────────────┘      └─────────────┘      └─────────────┘
```

---

## 4. OS CINCO PILARES DA SOLUÇÃO

| # | Pilar | Descrição |
|---|-------|-----------|
| 01 | **DIRECIONAMENTO** | Alinhamento com objetivos estratégicos do GAB para foco no que gera valor |
| 02 | **GESTÃO DE IDEIAS** | Captura estruturada de dores e sugestões de todos os níveis |
| 03 | **INOVAÇÃO ABERTA** | Conexão com ecossistema externo para acelerar soluções |
| 04 | **GESTÃO DE PROJETOS** | Estruturação de ideias validadas em iniciativas reais com cronograma e acompanhamento |
| 05 | **MENSURAÇÃO** | Acompanhamento de indicadores e ROI para demonstrar impacto real |

---

## 5. PERFIS DE USUÁRIO (CRÍTICO)

### 5.1 OPERADOR - Nível Operacional

**Papel**: Registro e Captura

**Características**:
- Colaboradores de base
- Precisam de registro RÁPIDO
- Interface deve ser SIMPLES e INTUITIVA
- Captura de dores operacionais SEM FRICÇÃO

**Funcionalidades PERMITIDAS**:
- ✅ Consultar orientações sobre estratégias da empresa (somente leitura)
- ✅ Cadastrar ideias de inovação/problemas do dia a dia
- ✅ Acompanhar status das PRÓPRIAS ideias cadastradas

**Funcionalidades PROIBIDAS**:
- ❌ Criar projetos
- ❌ Aprovar/reprovar ideias
- ❌ Gerenciar orientações estratégicas
- ❌ Ver dashboard executivo completo

---

### 5.2 GESTOR - Nível Tático

**Papel**: Gestão e Curadoria

**Características**:
- Gestores e coordenadores
- Responsáveis pela avaliação e priorização
- Precisam de visão clara do funil
- Ferramentas para decisões ágeis

**Funcionalidades PERMITIDAS**:
- ✅ Consultar orientações sobre estratégias (somente leitura)
- ✅ Consultar ideias cadastradas pelos operadores
- ✅ **PRIORIZAR** ideias
- ✅ **APROVAR/REPROVAR** ideias
- ✅ **CADASTRAR** projetos/iniciativas
- ✅ **ATUALIZAR** dados dos projetos (progresso, resultados)
- ✅ Acompanhar progresso dos projetos

**Funcionalidades PROIBIDAS**:
- ❌ Gerenciar orientações estratégicas
- ❌ Ver dashboard executivo completo (apenas da sua área)

---

### 5.3 LÍDER - Nível Estratégico

**Papel**: Decisão e Visão

**Características**:
- Lideranças da empresa
- Necessitam visão de portfólio
- Suporte à decisão com dados consolidados

**Funcionalidades PERMITIDAS**:
- ✅ **GERENCIAR** orientações estratégicas (CRUD completo)
- ✅ Consultar andamento de TODOS os projetos
- ✅ Visualizar **DASHBOARD** com principais resultados

**Métricas do Dashboard**:
- ROI (Return on Investment)
- Redução de custos
- Ganho de produtividade
- Lucro obtido
- Prazo vs previsto
- Investimento realizado
- Retorno financeiro por projeto
- Resumo geral consolidado

---

## 6. REQUISITOS FUNCIONAIS DETALHADOS

### 6.1 Módulo de Autenticação

| ID | Requisito | Prioridade |
|----|-----------|------------|
| AUTH-01 | Login com 3 perfis de usuários (operador, gestor, líder) | OBRIGATÓRIO |
| AUTH-02 | Gerenciamento seguro de sessão | OBRIGATÓRIO |
| AUTH-03 | Restrições por nível de acesso | OBRIGATÓRIO |

### 6.2 Módulo de Orientações Estratégicas

| ID | Requisito | Quem Usa | Prioridade |
|----|-----------|----------|------------|
| ORI-01 | CRUD completo de orientações | LÍDER | OBRIGATÓRIO |
| ORI-02 | Consultar orientações (leitura) | TODOS | OBRIGATÓRIO |
| ORI-03 | Orientações devem guiar quais ideias são prioritárias | Sistema | OBRIGATÓRIO |

### 6.3 Módulo de Ideias/Problemas

| ID | Requisito | Quem Usa | Prioridade |
|----|-----------|----------|------------|
| IDE-01 | Cadastrar ideias/problemas (interface simples) | OPERADOR | OBRIGATÓRIO |
| IDE-02 | Consultar PRÓPRIAS ideias | OPERADOR | OBRIGATÓRIO |
| IDE-03 | Acompanhar status das ideias | OPERADOR | OBRIGATÓRIO |
| IDE-04 | Listar TODAS as ideias | GESTOR | OBRIGATÓRIO |
| IDE-05 | **PRIORIZAR** ideias | GESTOR | OBRIGATÓRIO |
| IDE-06 | **APROVAR** ideias | GESTOR | OBRIGATÓRIO |
| IDE-07 | **REPROVAR** ideias com feedback | GESTOR | OBRIGATÓRIO |
| IDE-08 | Converter ideia aprovada em projeto | GESTOR | OBRIGATÓRIO |

### 6.4 Módulo de Projetos

| ID | Requisito | Quem Usa | Prioridade |
|----|-----------|----------|------------|
| PRJ-01 | Cadastrar projetos/iniciativas | GESTOR | OBRIGATÓRIO |
| PRJ-02 | Atualizar dados do projeto | GESTOR | OBRIGATÓRIO |
| PRJ-03 | Atualizar progresso (%) | GESTOR | OBRIGATÓRIO |
| PRJ-04 | Adicionar resultados obtidos | GESTOR | OBRIGATÓRIO |
| PRJ-05 | Consultar etapa atual | LÍDER | OBRIGATÓRIO |
| PRJ-06 | Consultar status | LÍDER | OBRIGATÓRIO |
| PRJ-07 | Consultar investimento | LÍDER | OBRIGATÓRIO |
| PRJ-08 | Consultar prazo | LÍDER | OBRIGATÓRIO |
| PRJ-09 | Consultar retorno financeiro | LÍDER | OBRIGATÓRIO |

### 6.5 Módulo de Dashboard (LÍDER)

| ID | Requisito | Prioridade |
|----|-----------|------------|
| DSH-01 | Resumo estruturado dos resultados dos projetos | OBRIGATÓRIO |
| DSH-02 | Retornos específicos POR PROJETO | OBRIGATÓRIO |
| DSH-03 | Resumo GERAL consolidado | OBRIGATÓRIO |
| DSH-04 | Métrica: ROI | OBRIGATÓRIO |
| DSH-05 | Métrica: Lucro obtido | OBRIGATÓRIO |
| DSH-06 | Métrica: Prazo (previsto vs realizado) | OBRIGATÓRIO |
| DSH-07 | Métrica: Investimento | OBRIGATÓRIO |
| DSH-08 | Métrica: Aumento de produtividade | OBRIGATÓRIO |

---

## 7. REQUISITOS TÉCNICOS

### 7.1 Plataforma

- **Aplicativo nativo** para Android ou iOS
- **Escolhido**: Android
- **API mínima**: 26 (Android 8.0)

### 7.2 Conectividade com Serviços Externos (OBRIGATÓRIA)

Pelo menos **UMA** das opções deve ser implementada de forma **efetiva e funcional** (não demonstrativa):

| Opção | Descrição |
|-------|-----------|
| APIs REST | Consumo de APIs mockadas ou reais |
| BaaS | Firebase, OneSignal, Backendless, AWS Amplify |
| Database | Banco de dados em tempo real ou em nuvem |

### 7.3 Arquitetura Implementada

| Componente | Tecnologia |
|------------|------------|
| Arquitetura | Clean Architecture + MVVM |
| DI | Hilt |
| Persistência Local | Room |
| Network | Retrofit + OkHttp |
| Async | Coroutines + Flow |
| UI | Jetpack Compose |

---

## 8. SUGESTÕES DE INOVAÇÃO (DIFERENCIAL)

O documento convida a implementar melhorias para promover engajamento:

| Sugestão | Descrição |
|----------|-----------|
| **Dinâmicas de Priorização** | Mecânicas diferenciadas para filtrar e priorizar ideias |
| **Sistema de Reconhecimento** | Reconhecimentos para quem mais contribuir ou tiver ideias escolhidas (gamificação) |
| **Automatização de Fluxos** | Automatizar transições de status, notificações, etc |
| **Visualização de Resultados** | Visibilidade prática e diferenciada dos resultados |

---

## 9. CRITÉRIOS DE AVALIAÇÃO

| Critério | Peso |
|----------|------|
| Adequação ao problema proposto | 20% |
| **Implementação técnica funcional** | **30%** |
| **Qualidade do código** | **25%** |
| Apresentação e documentação | 15% |
| Criatividade e inovação | 10% |

---

## 10. ENTREGÁVEIS SPRINT 1

**Data de Entrega**: 26/05/2026 - 23:00h

| Item | Formato |
|------|---------|
| Aplicativo | APK (Android) |
| Código-fonte | .zip completo |
| Documentação técnica | PDF ou PPT |
| Vídeo demonstrativo | Máx 5 minutos |

---

## 11. SPRINT 2 - PREVIEW (2º Semestre)

| Item | Descrição |
|------|-----------|
| Backend | Java ou C# |
| APIs | APIs REST completas |
| Integração | Com app nativo |
| Segurança | Restrições por nível de acesso |
| Serviços externos | Consumo real |
| Observabilidade | Auditoria, logs, métricas |

---

## 12. IDENTIDADE VISUAL E CORES

### 12.1 Cores Corporativas

| Cor | Hex | Uso |
|-----|-----|-----|
| **Azul Principal** | `#1565C0` | Botões, links, ações primárias |
| **Azul Escuro** | `#0D47A1` | Headers, TopBar |
| **Azul Claro** | `#BBDEFB` | Containers de destaque |
| **Laranja** | `#FF8F00` | FABs, ações secundárias, energia |
| **Laranja Escuro** | `#E65100` | Variante |
| **Verde** | `#2E7D32` | Sustentabilidade, sucesso |
| **Verde Claro** | `#C8E6C9` | Containers verdes |

### 12.2 Cores de Status

| Status | Cor Hex | Nome |
|--------|---------|------|
| Pendente | `#9E9E9E` | Cinza |
| Em Análise | `#2196F3` | Azul |
| Aprovado | `#4CAF50` | Verde |
| Reprovado | `#F44336` | Vermelho |
| Em Andamento | `#FF9800` | Laranja |
| Concluído | `#2E7D32` | Verde Escuro |

### 12.3 Significado das Cores

- **Azul**: Confiança, profissionalismo
- **Laranja**: Energia, inovação
- **Verde**: Sustentabilidade (parte do challenge ESG)

---

## 13. RESULTADOS ESPERADOS PELO CLIENTE

| Resultado | Descrição |
|-----------|-----------|
| **Sistema Integrado** | Conectar estratégia, pessoas, processos e tecnologia |
| **Engajamento Contínuo** | Participação ativa de colaboradores de todos os níveis |
| **Execução Ágil** | Transformar ideias em projetos reais e estruturados |
| **Impacto Mensurável** | Eficiência operacional, redução de custos, geração de receita |

---

## 14. STATUS DA IMPLEMENTAÇÃO

### Requisitos Implementados

| Requisito | Status | Observação |
|-----------|--------|------------|
| Login com 3 perfis | ✅ Implementado | Operador, Gestor, Líder |
| Cadastro de ideias/problemas | ✅ Implementado | Com tipo (Ideia/Problema) e área |
| Consulta de próprias ideias | ✅ Implementado | Filtro por autor |
| Listagem de todas ideias | ✅ Implementado | Para Gestor/Líder |
| Aprovação/Reprovação de ideias | ✅ Implementado | IdeiaDetalheScreen com ações |
| Priorização de ideias | ✅ Implementado | Campos impacto/esforço + score |
| Cadastro de projetos | ✅ Implementado | Manual ou via conversão de ideia |
| Métricas financeiras em Projeto | ✅ Implementado | Investimento, retorno, ROI |
| Atualizar progresso do projeto | ✅ Implementado | Campo progresso 0-100% |
| Diferenciação por perfil | ✅ Implementado | Home e ações por perfil |
| Radar de Inovação | ✅ Implementado | Startups parceiras |

### Requisitos Pendentes

| Gap | Gravidade | Descrição |
|-----|-----------|-----------|
| CRUD de Orientações | **ALTA** | Telas de criar/editar orientações para Líder |
| Dashboard executivo | **ALTA** | Métricas consolidadas (ROI total, investimento, retorno) |
| Testes unitários | MÉDIA | Cobertura de testes para ViewModels e UseCases |
| Notificações | BAIXA | Avisos de mudança de status |

---

## 15. INFORMAÇÕES ADICIONAIS

### 15.1 Equipe

- Limite: 5 alunos por equipe
- Mesma turma obrigatória
- Entrega por um integrante apenas

### 15.2 Prazos

| Marco | Data |
|-------|------|
| Kick-off | 30/04/2026 |
| **Entrega Sprint 1** | **26/05/2026 - 23:00h** |

> ⚠️ **ATENÇÃO**: NÃO há prazo de atraso para o Challenge

### 15.3 Contexto

- Challenge FIAP 2026
- Global Solution
- Disciplina de Desenvolvimento Mobile

---

*Documento gerado a partir do material oficial "Plataforma de Inovação Corporativa - Grupo Águia Branca"*
