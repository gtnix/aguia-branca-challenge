package com.gtnix.aguiabranca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.gtnix.aguiabranca.presentation.navigation.AppNavGraph
import com.gtnix.aguiabranca.presentation.theme.InovagabTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity - Ponto de entrada do app.
 *
 * ## Conceitos FIAP Aplicados
 *
 * ### Material 02A - Dominando o Ambiente Android
 *
 * O Android usa o conceito de Activity como "tela" do app.
 * Com Jetpack Compose, temos apenas UMA Activity e navegamos
 * entre "telas" (Composables) usando Navigation Compose.
 *
 * ### Estrutura Single Activity
 *
 * ```
 * MainActivity
 *   └── setContent { }        // Define o conteúdo Compose
 *       └── InovagabTheme  // Aplica o tema Material 3
 *           └── Surface      // Background da aplicação
 *               └── AppNavGraph  // Gerencia navegação entre telas
 * ```
 *
 * ### @AndroidEntryPoint
 *
 * Esta anotação permite que o Hilt injete dependências nesta Activity.
 * Qualquer ViewModel usado aqui receberá suas dependências automaticamente.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Habilita Edge-to-Edge: conteúdo se estende até as bordas da tela
        enableEdgeToEdge()

        // setContent define o conteúdo Compose da Activity
        // Tudo dentro deste bloco é uma função @Composable
        setContent {
            // InovagabTheme aplica cores, tipografia e shapes do Material 3
            InovagabTheme {
                // Surface é o container base que aplica a cor de fundo do tema
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // AppNavGraph gerencia toda a navegação do app
                    AppNavGraph()
                }
            }
        }
    }
}
