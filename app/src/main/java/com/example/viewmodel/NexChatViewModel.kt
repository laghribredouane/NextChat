package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.*
import com.example.repository.NexChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class Screen {
    data object ChatsList : Screen()
    data class ChatDetail(val conversationId: String) : Screen()
    data object Communities : Screen()
    data object Status : Screen()
    data object Calls : Screen()
    data object ActiveCall : Screen()
    data object Settings : Screen()
    data object AdminPanel : Screen()
    data object SecurityInfo : Screen()
}

class NexChatViewModel(
    val repository: NexChatRepository = NexChatRepository()
) : ViewModel() {

    // Navigation
    private val _currentScreen = MutableStateFlow<Screen>(Screen.ChatsList)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenStack = mutableListOf<Screen>(Screen.ChatsList)

    // Search query
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Voice recording simulation
    private val _isRecordingVoice = MutableStateFlow(false)
    val isRecordingVoice: StateFlow<Boolean> = _isRecordingVoice.asStateFlow()

    private val _recordingDurationSec = MutableStateFlow(0)
    val recordingDurationSec: StateFlow<Int> = _recordingDurationSec.asStateFlow()

    // Active call timer
    private val _callSeconds = MutableStateFlow(0)
    val callSeconds: StateFlow<Int> = _callSeconds.asStateFlow()

    private val _isMuted = MutableStateFlow(false)
    val isMuted: StateFlow<Boolean> = _isMuted.asStateFlow()

    private val _isVideoEnabled = MutableStateFlow(true)
    val isVideoEnabled: StateFlow<Boolean> = _isVideoEnabled.asStateFlow()

    private val _isSpeakerOn = MutableStateFlow(true)
    val isSpeakerOn: StateFlow<Boolean> = _isSpeakerOn.asStateFlow()

    fun navigateTo(screen: Screen) {
        screenStack.add(screen)
        _currentScreen.value = screen
    }

    fun navigateBack(): Boolean {
        if (screenStack.size > 1) {
            screenStack.removeAt(screenStack.size - 1)
            _currentScreen.value = screenStack.last()
            return true
        }
        return false
    }

    fun onSearchQueryChanged(q: String) {
        _searchQuery.value = q
    }

    fun sendMessage(conversationId: String, text: String, replyTo: Message? = null) {
        if (text.isBlank()) return
        repository.sendMessage(conversationId, text, MessageType.TEXT, replyTo)
    }

    fun startVoiceRecording() {
        _isRecordingVoice.value = true
        _recordingDurationSec.value = 0
        viewModelScope.launch {
            while (_isRecordingVoice.value) {
                delay(1000)
                _recordingDurationSec.value += 1
            }
        }
    }

    fun finishVoiceRecording(conversationId: String) {
        if (_isRecordingVoice.value) {
            val dur = _recordingDurationSec.value.coerceAtLeast(1)
            _isRecordingVoice.value = false
            _recordingDurationSec.value = 0

            val att = Attachment(
                id = "att_" + System.currentTimeMillis(),
                fileName = "voice_${System.currentTimeMillis()}.opus",
                fileType = "audio/ogg",
                fileSizeBytes = dur * 2500L,
                durationSeconds = dur,
                url = "voice",
                isEncrypted = true
            )
            repository.sendMessage(
                conversationId = conversationId,
                content = "Voice note (${dur}s)",
                type = MessageType.VOICE_NOTE,
                attachment = att
            )
        }
    }

    fun cancelVoiceRecording() {
        _isRecordingVoice.value = false
        _recordingDurationSec.value = 0
    }

    fun startCall(contactName: String, type: CallType) {
        repository.startCall(contactName, type)
        _callSeconds.value = 0
        _isMuted.value = false
        _isVideoEnabled.value = (type == CallType.VIDEO)
        _isSpeakerOn.value = true
        navigateTo(Screen.ActiveCall)

        viewModelScope.launch {
            while (repository.activeCall.value != null) {
                delay(1000)
                _callSeconds.value += 1
            }
        }
    }

    fun toggleMute() {
        _isMuted.value = !_isMuted.value
    }

    fun toggleVideo() {
        _isVideoEnabled.value = !_isVideoEnabled.value
    }

    fun toggleSpeaker() {
        _isSpeakerOn.value = !_isSpeakerOn.value
    }

    fun endCall() {
        repository.endCall()
        navigateBack()
    }
}
