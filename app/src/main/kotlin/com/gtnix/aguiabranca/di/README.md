# DI Layer - Dependency Injection

## Conceito FIAP - Hilt/Dagger

A pasta **di** contém os módulos Hilt que configuram como as dependências são criadas e injetadas.

### Estrutura

```
di/
├── DatabaseModule.kt     # Configura Room Database
├── RepositoryModule.kt   # Bind de interfaces -> implementações
└── NetworkModule.kt      # Retrofit, OkHttp (Sprint 2)
```

### O que é Dependency Injection?

Em vez de criar objetos assim:

```kotlin
// ❌ Ruim: ViewModel cria suas próprias dependências
class LoginViewModel : ViewModel() {
    private val database = Room.databaseBuilder(...).build()
    private val dao = database.usuarioDao()
    private val repository = UsuarioRepositoryImpl(dao)
}
```

Usamos injeção de dependências:

```kotlin
// ✅ Bom: Hilt injeta as dependências
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: UsuarioRepository
) : ViewModel()
```

### Módulos Hilt

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "aguiabranca.db"
        ).build()
    }
    
    @Provides
    fun provideUsuarioDao(db: AppDatabase): UsuarioDao {
        return db.usuarioDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindUsuarioRepository(
        impl: UsuarioRepositoryImpl
    ): UsuarioRepository
}
```

### Scopes

- `@Singleton`: Uma instância para todo o app
- `@ViewModelScoped`: Uma instância por ViewModel
- `@ActivityScoped`: Uma instância por Activity

### Benefícios

1. **Testabilidade**: Em testes, substituímos por mocks
2. **Desacoplamento**: Classes não conhecem implementações concretas
3. **Manutenção**: Mudanças centralizadas nos módulos
