package com.gtnix.aguiabranca.presentation.screens.ideias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NovaIdeiaViewModel @Inject constructor(
    private val ideiaRepository: IdeiaRepository,
    private val orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NovaIdeiaUiState())
    val uiState: StateFlow<NovaIdeiaUiState> = _uiState.asStateFlow()

    init {
        carregarOrientacoes()
    }

    private fun carregarOrientacoes() {
        viewModelScope.launch {
            try {
                orientacaoRepository.listarAtivas().collect { orientacoes ->
                    _uiState.update { it.copy(orientacoes = orientacoes) }
                }
            } catch (_: Exception) { }
        }
    }

    fun onTituloChange(titulo: String) {
        _uiState.update { it.copy(titulo = titulo, errorMessage = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _uiState.update { it.copy(descricao = descricao, errorMessage = null) }
    }

    fun onAreaChange(area: AreaAtuacao) {
        _uiState.update { it.copy(area = area) }
    }

    fun onTipoChange(tipo: TipoIdeia) {
        _uiState.update { it.copy(tipo = tipo) }
    }

    fun onTranscribedTextChange(text: String) {
        _uiState.update { it.copy(transcribedText = text) }
        analyzeTextForAISuggestions(text)
    }

    fun toggleRecording() {
        val current = _uiState.value
        if (current.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        _uiState.update { it.copy(isRecording = true, transcribedText = "") }
        simulateVoiceTranscription()
    }

    private fun stopRecording() {
        _uiState.update { current ->
            val finalText = current.transcribedText
            current.copy(
                isRecording = false,
                titulo = if (current.titulo.isBlank() && finalText.isNotBlank()) {
                    finalText.take(60).let { if (it.length == 60) "$it..." else it }
                } else current.titulo,
                descricao = if (current.descricao.isBlank()) finalText else current.descricao
            )
        }
    }

    private fun simulateVoiceTranscription() {
        val sampleTexts = listOf(
            "Sugiro implementar um sistema de rastreamento por GPS nos veículos da frota para otimizar rotas e reduzir custos com combustível",
            "Podemos criar um aplicativo para motoristas reportarem problemas mecânicos em tempo real",
            "Implementar painéis solares nas garagens para reduzir custos de energia",
            "Automatizar o processo de check-in de passageiros usando QR codes"
        )
        val selectedText = sampleTexts.random()
        
        viewModelScope.launch {
            var currentText = ""
            for (char in selectedText) {
                if (!_uiState.value.isRecording) break
                currentText += char
                _uiState.update { it.copy(transcribedText = currentText) }
                analyzeTextForAISuggestions(currentText)
                kotlinx.coroutines.delay(30L)
            }
        }
    }

    private fun analyzeTextForAISuggestions(text: String) {
        val lowerText = text.lowercase()
        
        val suggestedArea = when {
            lowerText.contains("frota") || lowerText.contains("veículo") || 
            lowerText.contains("motorista") || lowerText.contains("rota") -> AreaAtuacao.LOGISTICA
            lowerText.contains("cliente") || lowerText.contains("passageiro") || 
            lowerText.contains("atendimento") -> AreaAtuacao.COMERCIAL
            lowerText.contains("energia") || lowerText.contains("custo") || 
            lowerText.contains("economia") -> AreaAtuacao.FINANCEIRO
            lowerText.contains("sistema") || lowerText.contains("app") || 
            lowerText.contains("software") -> AreaAtuacao.TI
            else -> null
        }
        
        val suggestedImpact = when {
            lowerText.contains("automatizar") || lowerText.contains("reduzir custo") ||
            lowerText.contains("economia") || text.length > 100 -> "Alto"
            lowerText.contains("melhorar") || lowerText.contains("otimizar") -> "Médio"
            else -> null
        }
        
        _uiState.update { 
            it.copy(
                aiSuggestedArea = suggestedArea,
                aiSuggestedImpact = suggestedImpact,
                area = suggestedArea ?: it.area
            ) 
        }
    }

    fun toggleManualForm() {
        _uiState.update { it.copy(isManualFormExpanded = !it.isManualFormExpanded) }
    }

    fun applyAISuggestedArea() {
        _uiState.value.aiSuggestedArea?.let { area ->
            _uiState.update { it.copy(area = area) }
        }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _uiState.value
        val user = sessionManager.getCurrentUser()
        
        if (user == null) {
            _uiState.update { it.copy(errorMessage = "Usuário não logado") }
            return
        }
        
        val tituloFinal = current.titulo.ifBlank { 
            current.transcribedText.take(60).let { if (it.length == 60) "$it..." else it }
        }
        val descricaoFinal = current.descricao.ifBlank { current.transcribedText }
        
        if (tituloFinal.isBlank() || descricaoFinal.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Preencha todos os campos ou grave uma ideia por voz") }
            return
        }

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaIdeia = Ideia(
                    id = UUID.randomUUID().toString(),
                    titulo = tituloFinal.trim(),
                    descricao = descricaoFinal.trim(),
                    tipo = current.tipo,
                    area = current.area,
                    autorId = user.id,
                    autorNome = user.nome
                )

                ideiaRepository.salvar(novaIdeia)

                _uiState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro: ${e.message ?: "desconhecido"}") }
            }
        }
    }
}

data class NovaIdeiaUiState(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: TipoIdeia = TipoIdeia.IDEIA,
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val isRecording: Boolean = false,
    val transcribedText: String = "",
    val aiSuggestedArea: AreaAtuacao? = null,
    val aiSuggestedImpact: String? = null,
    val isManualFormExpanded: Boolean = false
)
