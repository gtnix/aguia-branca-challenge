# Domain Layer - Camada de Domínio

## Conceito FIAP - Clean Architecture

A camada de **Domain** é o coração da aplicação. Ela contém:

- **Regras de negócio** puras
- **Entities** que representam os conceitos do domínio
- **Interfaces (Ports)** que definem contratos

### Princípio Fundamental

> A camada de domínio **NÃO depende de nada externo**.
> Ela não conhece Android, Room, Retrofit, ou qualquer framework.

### Estrutura

```
domain/
├── model/       # Entities de domínio
├── repository/  # Interfaces (Ports) dos repositórios
└── usecase/     # Casos de uso (regras de negócio)
```

### Por que essa separação?

1. **Testabilidade**: Podemos testar regras de negócio sem emulador
2. **Reusabilidade**: O domínio pode ser usado em outro projeto
3. **Manutenção**: Mudanças no banco não afetam regras de negócio

### Exemplo Prático

```kotlin
// Entity de domínio - NÃO tem anotações de Room
data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val perfil: PerfilUsuario
)

// Interface do repositório - SEM implementação
interface UsuarioRepository {
    suspend fun buscarPorEmail(email: String): Usuario?
    fun listarTodos(): Flow<List<Usuario>>
}
```

A **implementação** do UsuarioRepository fica na camada `data/`.
