package com.github.ahmednmahran.aitherapist.ui.video

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.ahmednmahran.aitherapist.data.TherapistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VideoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TherapistRepository(application)
    private val _analysisResult = MutableStateFlow<String>("Tap the button to analyze your current expression.")
    val analysisResult = _analysisResult.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun analyzeFrame(bitmap: Bitmap) {
        _isLoading.value = true
        _analysisResult.value = "Analyzing..."
        viewModelScope.launch {
            val result = repository.analyzeImage("You are looking at a user during a therapy session. Analyze their facial expression and body language to deduce their emotional state. Be supportive and empathetic.", bitmap)
            _analysisResult.value = result
            _isLoading.value = false
        }
    }
}
