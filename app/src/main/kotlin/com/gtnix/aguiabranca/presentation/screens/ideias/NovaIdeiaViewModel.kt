package com.gtnix.aguiabranca.presentation.screens.ideias

import android.content.Context
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gtnix.aguiabranca.R
import com.gtnix.aguiabranca.domain.model.AreaAtuacao
import com.gtnix.aguiabranca.domain.model.Ideia
import com.gtnix.aguiabranca.domain.model.OrientacaoEstrategica
import com.gtnix.aguiabranca.domain.model.TipoIdeia
import com.gtnix.aguiabranca.domain.repository.IdeiaRepository
import com.gtnix.aguiabranca.domain.repository.OrientacaoRepository
import com.gtnix.aguiabranca.domain.session.SessionManager
import com.gtnix.aguiabranca.presentation.util.SpeechRecognizerHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NovaIdeiaViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ideiaRepository: IdeiaRepository,
    orientacaoRepository: OrientacaoRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _formState = MutableStateFlow(NovaIdeiaFormState())
    private var speechRecognizerHelper: SpeechRecognizerHelper? = null

    val uiState: StateFlow<NovaIdeiaUiState> = combine(
        _formState,
        orientacaoRepository.listarAtivas()
            .catch { emit(emptyList()) }
    ) { form, orientacoes ->
        NovaIdeiaUiState(
            titulo = form.titulo,
            descricao = form.descricao,
            tipo = form.tipo,
            area = form.area,
            orientacaoId = form.orientacaoId,
            isLoading = form.isLoading,
            errorMessageRes = form.errorMessageRes,
            orientacoes = orientacoes,
            isRecording = form.isRecording,
            transcribedText = form.transcribedText,
            aiSuggestedArea = form.aiSuggestedArea,
            aiSuggestedImpact = form.aiSuggestedImpact,
            isManualFormExpanded = form.isManualFormExpanded
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NovaIdeiaUiState()
    )

    fun onTituloChange(titulo: String) {
        _formState.update { it.copy(titulo = titulo, errorMessageRes = null) }
    }

    fun onDescricaoChange(descricao: String) {
        _formState.update { it.copy(descricao = descricao, errorMessageRes = null) }
    }

    fun onAreaChange(area: AreaAtuacao) {
        _formState.update { it.copy(area = area) }
    }

    fun onOrientacaoChange(orientacaoId: String?) {
        _formState.update { it.copy(orientacaoId = orientacaoId) }
    }

    fun onTipoChange(tipo: TipoIdeia) {
        _formState.update { it.copy(tipo = tipo) }
    }

    fun onTranscribedTextChange(text: String) {
        _formState.update { it.copy(transcribedText = text) }
        analyzeTextForAISuggestions(text)
    }

    fun toggleRecording() {
        val current = _formState.value
        if (current.isRecording) {
            stopRecording()
        } else {
            startRecording()
        }
    }

    fun onMicrophonePermissionDenied() {
        _formState.update {
            it.copy(
                isRecording = false,
                errorMessageRes = R.string.error_speech_permission_denied
            )
        }
    }

    private fun startRecording() {
        _formState.update {
            it.copy(isRecording = true, transcribedText = "", errorMessageRes = null)
        }

        if (!SpeechRecognizerHelper.isAvailable(context)) {
            simulateVoiceTranscription()
            return
        }

        getOrCreateSpeechHelper().startListening()
    }

    private fun stopRecording() {
        speechRecognizerHelper?.stopListening()
        applyTranscriptionToForm()
    }

    private fun applyTranscriptionToForm() {
        _formState.update { current ->
            val finalText = current.transcribedText
            current.copy(
                isRecording = false,
                titulo = if (current.titulo.isBlank() && finalText.isNotBlank()) {
                    finalText.take(60).let { if (it.length == 60) "$it..." else it }
                } else {
                    current.titulo
                },
                descricao = if (current.descricao.isBlank()) finalText else current.descricao
            )
        }
    }

    private fun getOrCreateSpeechHelper(): SpeechRecognizerHelper {
        return speechRecognizerHelper ?: SpeechRecognizerHelper(
            context = context,
            onPartialResult = ::onTranscribedTextChange,
            onFinalResult = ::onTranscribedTextChange,
            errorCallback = ::handleSpeechError
        ).also { speechRecognizerHelper = it }
    }

    private fun handleSpeechError(errorCode: Int) {
        Log.w(TAG, "Speech recognition error: $errorCode")

        when (errorCode) {
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> {
                _formState.update {
                    it.copy(
                        isRecording = false,
                        errorMessageRes = R.string.error_speech_permission_denied
                    )
                }
            }
            SpeechRecognizer.ERROR_NO_MATCH,
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> {
                val hadTranscription = _formState.value.transcribedText.isNotBlank()
                _formState.update {
                    it.copy(
                        isRecording = false,
                        errorMessageRes = if (!hadTranscription) {
                            R.string.error_speech_no_match
                        } else {
                            null
                        }
                    )
                }
                if (hadTranscription) {
                    applyTranscriptionToFormFieldsOnly()
                }
            }
            SpeechRecognizer.ERROR_CLIENT -> {
                fallbackAfterRecognizerFailure()
            }
            else -> {
                fallbackAfterRecognizerFailure()
            }
        }
    }

    private fun fallbackAfterRecognizerFailure() {
        speechRecognizerHelper?.destroy()
        speechRecognizerHelper = null

        val hadTranscription = _formState.value.transcribedText.isNotBlank()
        if (hadTranscription) {
            applyTranscriptionToForm()
        } else {
            _formState.update { it.copy(isRecording = true, errorMessageRes = null) }
            simulateVoiceTranscription()
        }
    }

    private fun applyTranscriptionToFormFieldsOnly() {
        _formState.update { current ->
            val finalText = current.transcribedText
            current.copy(
                titulo = if (current.titulo.isBlank() && finalText.isNotBlank()) {
                    finalText.take(60).let { if (it.length == 60) "$it..." else it }
                } else {
                    current.titulo
                },
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
                if (!_formState.value.isRecording) break
                currentText += char
                _formState.update { it.copy(transcribedText = currentText) }
                analyzeTextForAISuggestions(currentText)
                delay(30L)
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

        _formState.update {
            it.copy(
                aiSuggestedArea = suggestedArea,
                aiSuggestedImpact = suggestedImpact,
                area = suggestedArea ?: it.area
            )
        }
    }

    fun toggleManualForm() {
        _formState.update { it.copy(isManualFormExpanded = !it.isManualFormExpanded) }
    }

    fun applyAISuggestedArea() {
        _formState.value.aiSuggestedArea?.let { area ->
            _formState.update { it.copy(area = area) }
        }
    }

    fun salvar(onSuccess: () -> Unit) {
        val current = _formState.value
        val user = sessionManager.getCurrentUser()

        if (user == null) {
            _formState.update { it.copy(errorMessageRes = R.string.error_user_not_logged) }
            return
        }

        val tituloFinal = current.titulo.ifBlank {
            current.transcribedText.take(60).let { if (it.length == 60) "$it..." else it }
        }
        val descricaoFinal = current.descricao.ifBlank { current.transcribedText }

        if (tituloFinal.isBlank() || descricaoFinal.isBlank()) {
            _formState.update { it.copy(errorMessageRes = R.string.error_nova_ideia_fields) }
            return
        }

        _formState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaIdeia = Ideia(
                    id = UUID.randomUUID().toString(),
                    titulo = tituloFinal.trim(),
                    descricao = descricaoFinal.trim(),
                    tipo = current.tipo,
                    area = current.area,
                    autorId = user.id,
                    autorNome = user.nome,
                    orientacaoId = current.orientacaoId
                )

                ideiaRepository.salvar(novaIdeia)

                _formState.update { it.copy(isLoading = false) }
                onSuccess()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to save idea", e)
                _formState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_save_idea) }
            }
        }
    }

    override fun onCleared() {
        speechRecognizerHelper?.destroy()
        speechRecognizerHelper = null
        super.onCleared()
    }

    companion object {
        private const val TAG = "NovaIdeiaViewModel"
    }
}

private data class NovaIdeiaFormState(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: TipoIdeia = TipoIdeia.IDEIA,
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val orientacaoId: String? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val isRecording: Boolean = false,
    val transcribedText: String = "",
    val aiSuggestedArea: AreaAtuacao? = null,
    val aiSuggestedImpact: String? = null,
    val isManualFormExpanded: Boolean = false
)

data class NovaIdeiaUiState(
    val titulo: String = "",
    val descricao: String = "",
    val tipo: TipoIdeia = TipoIdeia.IDEIA,
    val area: AreaAtuacao = AreaAtuacao.OPERACOES,
    val orientacaoId: String? = null,
    val isLoading: Boolean = false,
    val errorMessageRes: Int? = null,
    val orientacoes: List<OrientacaoEstrategica> = emptyList(),
    val isRecording: Boolean = false,
    val transcribedText: String = "",
    val aiSuggestedArea: AreaAtuacao? = null,
    val aiSuggestedImpact: String? = null,
    val isManualFormExpanded: Boolean = false
)
