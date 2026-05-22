# Presentation Layer - Camada de Apresentação

## Conceitos FIAP - Materiais 02A, 04A, 05A, 10A

A camada de **Presentation** contém tudo relacionado à UI.

### Estrutura

```
presentation/
├── navigation/      # Rotas e NavGraph (05A)
├── theme/           # Material 3 Theme (cores, tipografia)
├── components/      # Componentes reutilizáveis
└── screens/         # Telas organizadas por feature
    ├── auth/        # Login, Cadastro
    ├── home/        # Dashboard principal
    ├── ideias/      # CRUD de ideias
    └── projetos/    # CRUD de projetos
```

### MVVM Pattern (Material FIAP 10A)

Cada tela segue o padrão Model-View-ViewModel:

```
┌─────────────────────────────────────────────────────┐
│                        VIEW                          │
│              (Composable Functions)                  │
│  - Renderiza UI baseada no State                    │
│  - Envia eventos para ViewModel                     │
└───────────────────────┬─────────────────────────────┘
                        │
                   observes
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                    VIEWMODEL                         │
│  - Mantém o state da tela                           │
│  - Processa eventos da View                         │
│  - Chama UseCases/Repositories                      │
└───────────────────────┬─────────────────────────────┘
                        │
                     calls
                        │
                        ▼
┌─────────────────────────────────────────────────────┐
│                   REPOSITORY                         │
│  - Acessa dados (Room, API)                         │
│  - Retorna Flow para reatividade                    │
└─────────────────────────────────────────────────────┘
```

### State Management (Material FIAP 02A/10A)

```kotlin
// Estado da tela
sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val usuario: Usuario) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

// ViewModel mantém o estado
class LoginViewModel : ViewModel() {
    private val _uiState = mutableStateOf<LoginUiState>(LoginUiState.Idle)
    val uiState: State<LoginUiState> = _uiState
}

// Composable observa o estado
@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.uiState
    
    when (state) {
        is LoginUiState.Loading -> CircularProgressIndicator()
        is LoginUiState.Success -> { /* navegar */ }
        // ...
    }
}
```

### Navigation (Material FIAP 05A)

```kotlin
// Rotas type-safe
sealed class Destination(val route: String) {
    object Login : Destination("login")
    object Home : Destination("home/{perfil}")
}

// NavHost gerencia a pilha de navegação
NavHost(navController, startDestination = Destination.Login.route) {
    composable(Destination.Login.route) { LoginScreen(...) }
    // ...
}
```
