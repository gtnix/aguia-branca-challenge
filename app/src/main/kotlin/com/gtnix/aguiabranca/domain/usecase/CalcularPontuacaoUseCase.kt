package com.gtnix.aguiabranca.domain.usecase

import com.gtnix.aguiabranca.domain.model.Conquista
import com.gtnix.aguiabranca.domain.model.Estatisticas
import com.gtnix.aguiabranca.domain.model.NivelUsuario
import com.gtnix.aguiabranca.domain.model.PerfilUsuario
import com.gtnix.aguiabranca.domain.model.Pontuacao
import com.gtnix.aguiabranca.domain.model.StatusIdeia
import com.gtnix.aguiabranca.domain.model.StatusProjeto
import com.gtnix.aguiabranca.domain.model.TipoConquista
import com.gtnix.aguiabranca.domain.model.conquistasPorPerfil
import com.gtnix.aguiabranca.domain.model.defaultTier
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.repository.ProjetoRepository
import com.gtnix.aguiabranca.domain.repository.UsuarioRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalcularPontuacaoUseCase @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val projetoRepository: ProjetoRepository,
    private val orientacaoRepository: OrientacaoRepository,
    private val usuarioRepository: UsuarioRepository
) {

    suspend fun calcular(userId: String): Pontuacao {
        val usuario = usuarioRepository.buscarPorId(userId)
        val perfil = usuario?.perfil ?: PerfilUsuario.OPERADOR
        return calcular(userId, perfil)
    }

    suspend fun calcular(userId: String, perfil: PerfilUsuario): Pontuacao = when (perfil) {
        PerfilUsuario.OPERADOR -> calcularOperador(userId)
        PerfilUsuario.GESTOR -> calcularGestor(userId)
        PerfilUsuario.LIDER -> calcularLider(userId)
    }

    private suspend fun calcularOperador(userId: String): Pontuacao {
        val ideias = ideiaRepository.listarPorAutor(userId).first()

        var total = ideias.size * PONTOS_OPERADOR_POR_IDEIA

        val ideiasAprovadas = ideias.count { it.status == StatusIdeia.APROVADA }
        val ideiasConvertidas = ideias.count { it.status == StatusIdeia.CONVERTIDA_PROJETO }
        val ideiasComBonus = ideiasAprovadas + ideiasConvertidas
        total += ideiasComBonus * PONTOS_OPERADOR_BONUS_APROVADA

        val projetosParticipando = ideiasConvertidas
        total += projetosParticipando * PONTOS_POR_PROJETO

        val nivel = NivelUsuario.fromPontuacao(total)
        val progresso = calcularProgresso(total, nivel)

        val conquistas = filtrarConquistasPerfil(
            perfil = PerfilUsuario.OPERADOR,
            todas = calcularConquistasOperador(
                totalIdeias = ideias.size,
                ideiasAprovadas = ideiasAprovadas + ideiasConvertidas,
                ideiasEmProjeto = ideiasConvertidas
            )
        )

        val estatisticas = Estatisticas(
            totalIdeias = ideias.size,
            ideiasAprovadas = ideiasAprovadas + ideiasConvertidas,
            projetosParticipando = projetosParticipando
        )

        return Pontuacao(
            total = total,
            nivel = nivel,
            progressoProximoNivel = progresso,
            pontosParaProximoNivel = calcularPontosRestantes(total, nivel),
            conquistas = conquistas,
            estatisticas = estatisticas
        )
    }

    private suspend fun calcularGestor(userId: String): Pontuacao {
        val projetos = projetoRepository.listarPorUsuario(userId).first()
        val usuario = usuarioRepository.buscarPorId(userId)
        val ideiasDaArea = if (usuario != null) {
            ideiaRepository.listarPorArea(usuario.area).first()
        } else {
            ideiaRepository.listarTodas().first()
        }
        val ideiasAvaliadas = ideiasDaArea.count {
            it.dataAvaliacao != null && it.status != StatusIdeia.PENDENTE
        }
        val ideiasAprovadasEquipe = ideiasDaArea.count {
            it.status == StatusIdeia.APROVADA || it.status == StatusIdeia.CONVERTIDA_PROJETO
        }
        val projetosAtivosEquipe = projetos.count {
            it.status == StatusProjeto.EM_ANDAMENTO || it.status == StatusProjeto.PLANEJADO
        }

        var total = projetos.size * PONTOS_GESTOR_POR_PROJETO
        total += ideiasAvaliadas * PONTOS_GESTOR_POR_AVALIACAO

        val nivel = NivelUsuario.fromPontuacao(total)

        val conquistas = filtrarConquistasPerfil(
            perfil = PerfilUsuario.GESTOR,
            todas = calcularConquistasGestor(
                ideiasAvaliadas = ideiasAvaliadas,
                ideiasAprovadasEquipe = ideiasAprovadasEquipe,
                projetosAtivosEquipe = projetosAtivosEquipe
            )
        )

        val estatisticas = Estatisticas(
            totalIdeias = ideiasAvaliadas,
            ideiasAprovadas = ideiasAprovadasEquipe,
            projetosParticipando = projetos.size
        )

        return Pontuacao(
            total = total,
            nivel = nivel,
            progressoProximoNivel = calcularProgresso(total, nivel),
            pontosParaProximoNivel = calcularPontosRestantes(total, nivel),
            conquistas = conquistas,
            estatisticas = estatisticas
        )
    }

    private suspend fun calcularLider(userId: String): Pontuacao {
        val orientacoes = orientacaoRepository.listarTodas().first()
            .filter { it.criadoPor == userId }
        val orientacoesAtivas = orientacoes.count { it.ativa }
        val orientacaoIds = orientacoes.map { it.id }.toSet()
        val todasIdeias = ideiaRepository.listarTodas().first()
        val todosProjetos = projetoRepository.listarTodos().first()

        var total = orientacoes.size * PONTOS_LIDER_POR_ORIENTACAO

        val ideiasDivisao = todasIdeias.size
        val ideiasAlinhadas = todasIdeias.count { it.orientacaoId in orientacaoIds }
        val projetosDirecionados = todosProjetos.count { it.orientacaoId in orientacaoIds }
        val projetosConcluidosDivisao = todosProjetos.count {
            it.orientacaoId in orientacaoIds && it.status == StatusProjeto.CONCLUIDO
        }

        val nivel = NivelUsuario.fromPontuacao(total)

        val conquistas = filtrarConquistasPerfil(
            perfil = PerfilUsuario.LIDER,
            todas = calcularConquistasLider(
                orientacoesCriadas = orientacoes.size,
                ideiasDivisao = ideiasDivisao,
                projetosConcluidosDivisao = projetosConcluidosDivisao
            )
        )

        val estatisticas = Estatisticas(
            totalIdeias = orientacoesAtivas,
            ideiasAprovadas = ideiasAlinhadas,
            projetosParticipando = projetosDirecionados
        )

        return Pontuacao(
            total = total,
            nivel = nivel,
            progressoProximoNivel = calcularProgresso(total, nivel),
            pontosParaProximoNivel = calcularPontosRestantes(total, nivel),
            conquistas = conquistas,
            estatisticas = estatisticas
        )
    }

    private fun filtrarConquistasPerfil(
        perfil: PerfilUsuario,
        todas: List<Conquista>
    ): List<Conquista> {
        val tiposPerfil = conquistasPorPerfil(perfil)
        return tiposPerfil.mapNotNull { tipo ->
            todas.find { it.tipo == tipo }
        }
    }

    private fun calcularConquistasGestor(
        ideiasAvaliadas: Int,
        ideiasAprovadasEquipe: Int,
        projetosAtivosEquipe: Int
    ): List<Conquista> {
        return listOf(
            criarConquista("bem_vindo", TipoConquista.BEM_VINDO, true),
            criarConquista("curador", TipoConquista.CURADOR, ideiasAvaliadas >= 1),
            criarConquista("mentor", TipoConquista.MENTOR, ideiasAvaliadas >= 10),
            criarConquista(
                "primeira_aprovacao",
                TipoConquista.PRIMEIRA_APROVACAO,
                ideiasAprovadasEquipe >= 1
            ),
            criarConquista(
                "lider_resultados",
                TipoConquista.LIDER_RESULTADOS,
                ideiasAprovadasEquipe >= 5
            ),
            criarConquista("executor", TipoConquista.EXECUTOR, projetosAtivosEquipe >= 3)
        )
    }

    private fun calcularConquistasLider(
        orientacoesCriadas: Int,
        ideiasDivisao: Int,
        projetosConcluidosDivisao: Int
    ): List<Conquista> {
        return listOf(
            criarConquista("bem_vindo", TipoConquista.BEM_VINDO, true),
            criarConquista("estrategista", TipoConquista.ESTRATEGISTA, orientacoesCriadas >= 1),
            criarConquista(
                "visionario_estrategico",
                TipoConquista.VISIONARIO_ESTRATEGICO,
                orientacoesCriadas >= 3
            ),
            criarConquista(
                "lider_inovacao",
                TipoConquista.LIDER_INOVACAO,
                ideiasDivisao >= 50
            ),
            criarConquista(
                "transformador_estrategico",
                TipoConquista.TRANSFORMADOR_ESTRATEGICO,
                projetosConcluidosDivisao >= 10
            )
        )
    }

    private fun calcularConquistasOperador(
        totalIdeias: Int,
        ideiasAprovadas: Int,
        ideiasEmProjeto: Int
    ): List<Conquista> {
        return listOf(
            criarConquista("bem_vindo", TipoConquista.BEM_VINDO, true),
            criarConquista("primeira_ideia", TipoConquista.PRIMEIRA_IDEIA, totalIdeias >= 1),
            criarConquista("inovador", TipoConquista.INOVADOR, totalIdeias >= 5),
            criarConquista("visionario_ideias", TipoConquista.VISIONARIO_IDEIAS, totalIdeias >= 10),
            criarConquista("primeira_aprovacao", TipoConquista.PRIMEIRA_APROVACAO, ideiasAprovadas >= 1),
            criarConquista("influenciador", TipoConquista.INFLUENCIADOR, ideiasAprovadas >= 3),
            criarConquista("em_projeto", TipoConquista.EM_PROJETO, ideiasEmProjeto >= 1),
            criarConquista(
                "transformador",
                TipoConquista.TRANSFORMADOR,
                totalIdeias >= 20 && ideiasAprovadas >= 5
            )
        )
    }

    private fun criarConquista(
        id: String,
        tipo: TipoConquista,
        desbloqueada: Boolean
    ): Conquista = Conquista(
        id = id,
        tipo = tipo,
        desbloqueada = desbloqueada,
        tier = tipo.defaultTier()
    )

    private fun calcularProgresso(total: Int, nivel: NivelUsuario): Float {
        if (nivel == NivelUsuario.TRANSFORMADOR) return 1f
        val range = nivel.pontoMaximo - nivel.pontoMinimo + 1
        val within = total - nivel.pontoMinimo
        return (within.toFloat() / range).coerceIn(0f, 1f)
    }

    private fun calcularPontosRestantes(total: Int, nivel: NivelUsuario): Int {
        if (nivel == NivelUsuario.TRANSFORMADOR) return 0
        return nivel.pontoMaximo - total + 1
    }

    companion object {
        const val PONTOS_OPERADOR_POR_IDEIA = 10
        const val PONTOS_OPERADOR_BONUS_APROVADA = 5
        const val PONTOS_POR_PROJETO = 20

        const val PONTOS_GESTOR_POR_AVALIACAO = 5
        const val PONTOS_GESTOR_POR_PROJETO = 20

        const val PONTOS_LIDER_POR_ORIENTACAO = 15
    }
}
