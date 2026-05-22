// ============================================================================
// APP BUILD CONFIGURATION - Aguia Branca Challenge
// ============================================================================
// Este arquivo configura como o app será compilado e empacotado.
// Conforme material FIAP 05A, aqui declaramos as dependências que o Gradle
// irá baixar e disponibilizar para o projeto.
// ============================================================================

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.gtnix.aguiabranca"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.gtnix.aguiabranca"
        // API 26 (Android 8.0) - Conforme material FIAP 02A:
        // Escolher um SDK mínimo garante maior alcance de usuários
        // enquanto mantemos recursos modernos
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Room Schema Export - útil para migrations
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // ========================================================================
    // CORE ANDROID
    // ========================================================================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // ========================================================================
    // JETPACK COMPOSE (via BOM)
    // ========================================================================
    // O BOM (Bill of Materials) é um conceito importante:
    // Ele garante que todas as bibliotecas Compose usem versões compatíveis
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    // Material Icons Extended - ícones adicionais como Delete, Add, Person etc.
    implementation(libs.androidx.material.icons.extended)

    // ========================================================================
    // NAVIGATION COMPOSE - Material FIAP 05A
    // ========================================================================
    // Permite navegação entre telas usando:
    // - NavHost: contêiner que gerencia a pilha de navegação
    // - NavController: controla a navegação entre destinos
    // - Rotas type-safe: evita erros de digitação em strings
    implementation(libs.androidx.navigation.compose)

    // ========================================================================
    // ROOM DATABASE - Material FIAP 07A
    // ========================================================================
    // Room é uma camada de abstração sobre SQLite que facilita:
    // - @Entity: define tabelas do banco
    // - @Dao: define operações CRUD
    // - Flow<List<T>>: reatividade automática na UI
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // ========================================================================
    // HILT - Dependency Injection
    // ========================================================================
    // Conceito fundamental de Clean Architecture:
    // - ViewModel não cria seus próprios repositórios
    // - Hilt "injeta" as dependências automaticamente
    // - Facilita testes e manutenção
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.android.compiler)

    // ========================================================================
    // COROUTINES - Material FIAP 10A
    // ========================================================================
    // Programação assíncrona é essencial para não travar a Main Thread
    // - viewModelScope.launch { } executa código em background
    // - Flow mantém canal aberto para atualizações reativas
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // ========================================================================
    // RETROFIT & OKHTTP (Sprint 2)
    // ========================================================================
    // Preparado para integração com API REST no segundo semestre
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // ========================================================================
    // SERIALIZATION
    // ========================================================================
    implementation(libs.kotlinx.serialization.json)

    // ========================================================================
    // TESTING
    // ========================================================================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
