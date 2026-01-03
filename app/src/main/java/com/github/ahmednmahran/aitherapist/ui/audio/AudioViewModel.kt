package com.github.ahmednmahran.aitherapist.ui.audio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ahmednmahran.aitherapist.data.TherapistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AudioViewModel : ViewModel() {
    private val repository = TherapistRepository()
    private val _analysisResult = MutableStateFlow<String>("Press record to start a session.")
    val analysisResult = _analysisResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun analyzeRecording(file: File) {
        _isLoading.value = true
        _analysisResult.value = "Listening..."
        viewModelScope.launch {
            if (file.exists()) {
                val bytes = file.readBytes()
                val result = repository.analyzeAudio("Listen to this audio clip. Analyze the speaker's tone, pitch, and content to determine their emotional state. Provide empathetic feedback.", bytes)
                _analysisResult.value = result
            } else {
                _analysisResult.value = "Error: File not found."
            }
            _isLoading.value = false
        }
    }
}
