# Águia Branca Challenge

Plataforma de Inovação Corporativa - Global Solution FIAP 2026

## Sobre o Projeto

Aplicativo móvel nativo Android para gestão de inovação corporativa do Grupo Águia Branca. Desenvolvido como parte do Challenge FIAP 2026.

### Desafio

Criar uma solução mobile que integre inovação corporativa e resultados, permitindo:

1. **Capturar** - Registrar dores e oportunidades do dia a dia operacional
2. **Estruturar** - Transformar problemas em iniciativas alinhadas à estratégia
3. **Acompanhar** - Monitorar evolução dos projetos até resultados mensuráveis

### Perfis de Usuário

| Perfil | Funcionalidades |
|--------|-----------------|
| **Operador** | Consultar estratégias, cadastrar ideias/problemas, acompanhar status |
| **Gestor** | Avaliar, priorizar e aprovar ideias, cadastrar e acompanhar projetos |
| **Líder** | Gerenciar orientações estratégicas, consultar projetos, visualizar dashboard |

## Stack Técnica

| Camada | Tecnologia |
|--------|------------|
| Plataforma | Android (API 26+) |
| Linguagem | Kotlin |
| UI | Jetpack Compose |
| Arquitetura | Clean Architecture + MVVM |
| DI | Hilt |
| Network | Retrofit + OkHttp |
| Local DB | Room |
| Async | Coroutines + Flow |

## Estrutura do Projeto

```
app/
├── src/main/kotlin/com/gtnix/aguiabranca/
│   ├── domain/           # Entities, Use Cases, Repository interfaces
│   ├── data/             # Repository implementations, Data sources, DTOs
│   ├── presentation/     # ViewModels, UI (Compose), Navigation
│   └── di/               # Hilt modules
└── src/main/res/         # Resources (strings, themes, etc.)
```

## Sprints

### Sprint 1 (Entrega: 26/05/2026)
- [ ] Setup do projeto Android
- [ ] Autenticação (3 perfis)
- [ ] CRUD Orientações Estratégicas
- [ ] Cadastro e listagem de Ideias
- [ ] Gestão de Projetos
- [ ] Dashboard básico

### Sprint 2 (2º Semestre)
- Backend Java/C#
- APIs REST
- Integração completa
- Observabilidade

## Equipe

- Giuliana Takara
- [Companheiro]

## Documentação

- [Arquitetura](docs/ARCHITECTURE.md)
- [Wireframes](docs/wireframes/)

---

*Challenge FIAP 2026 - Grupo Águia Branca*
