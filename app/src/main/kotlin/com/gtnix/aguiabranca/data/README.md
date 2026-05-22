# Data Layer - Camada de Dados

## Conceito FIAP - Material 07A (Room) e 10A (Repository)

A camada de **Data** implementa os contratos definidos no Domain.

### Estrutura

```
data/
├── local/           # Banco de dados local (Room)
│   ├── database/    # AppDatabase, TypeConverters
│   ├── dao/         # Data Access Objects
│   └── entity/      # Entities do Room (@Entity)
├── remote/          # API REST (Sprint 2)
│   ├── api/         # Interfaces Retrofit
│   └── dto/         # Data Transfer Objects
├── repository/      # Implementações dos Ports
└── mapper/          # Conversões Entity <-> Domain
```

### Room Database (Material FIAP 07A)

Room é uma **abstração sobre SQLite** que facilita:

```kotlin
// @Entity define uma tabela no banco
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val email: String,
    val perfil: String
)

// @Dao define operações CRUD
@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun listarTodos(): Flow<List<UsuarioEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(usuario: UsuarioEntity)
}
```

### Repository Pattern (Material FIAP 10A)

O Repository **conecta** Domain e Data:

```kotlin
class UsuarioRepositoryImpl(
    private val dao: UsuarioDao,
    private val mapper: UsuarioMapper
) : UsuarioRepository {
    
    override fun listarTodos(): Flow<List<Usuario>> {
        return dao.listarTodos().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }
}
```

### Mappers

Convertem entre Entity (Room) e Domain:

```kotlin
class UsuarioMapper {
    fun toDomain(entity: UsuarioEntity): Usuario { ... }
    fun toEntity(domain: Usuario): UsuarioEntity { ... }
}
```
