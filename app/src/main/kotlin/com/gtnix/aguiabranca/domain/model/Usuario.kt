package com.gtnix.aguiabranca.domain.model

/**
 * Entity de Domínio: Usuario
 *
 * ## Conceito FIAP - Clean Architecture
 *
 * Esta é uma **Entity de Domínio** - uma classe Kotlin pura que representa
 * um conceito de negócio. Note que:
 *
 * - NÃO tem anotações @Entity (isso é do Room, camada Data)
 * - NÃO depende de nenhum framework Android
 * - PODE ser testada sem emulador
 *
 * ## Perfis de Usuário (Requisito do Projeto)
 *
 * O sistema possui três perfis conforme especificação:
 *
 * 1. **OPERADOR**: Funcionário comum que submete ideias/problemas
 * 2. **GESTOR**: Avalia ideias e gerencia projetos da sua área
 * 3. **LIDER**: Define orientações estratégicas e vê dashboard consolidado
 *
 * @property id Identificador único (UUID)
 * @property nome Nome completo do colaborador
 * @property email E-mail corporativo (@aguiabranca.com.br)
 * @property perfil Nível de acesso no sistema
 * @property area Área de atuação na empresa
 * @property fotoPerfil URL da foto (opcional)
 */
data class Usuario(
    val id: String,
    val nome: String,
    val email: String,
    val perfil: PerfilUsuario,
    val area: AreaAtuacao,
    val fotoPerfil: String? = null,
    val dataCadastro: Long = System.currentTimeMillis()
)

/**
 * Enum que define os perfis de acesso no sistema.
 *
 * Cada perfil tem permissões diferentes:
 *
 * | Perfil   | Submeter Ideia | Avaliar | Criar Projeto | Ver Dashboard |
 * |----------|----------------|---------|---------------|---------------|
 * | OPERADOR | ✅             | ❌      | ❌            | Próprio       |
 * | GESTOR   | ✅             | ✅      | ✅            | Área          |
 * | LIDER    | ✅             | ✅      | ✅            | Completo      |
 */
enum class PerfilUsuario {
    OPERADOR,
    GESTOR,
    LIDER
}

/**
 * Áreas de atuação na empresa Águia Branca.
 *
 * Conforme especificação do projeto, as ideias e projetos
 * são organizados por área de atuação.
 */
enum class AreaAtuacao {
    OPERACOES,
    LOGISTICA,
    COMERCIAL,
    FINANCEIRO,
    RH,
    TI,
    MARKETING,
    QUALIDADE
}
