package com.github.ahmednmahran.aitherapist.ui.chat

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.github.ahmednmahran.aitherapist.data.TherapistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class Message(val text: String, val isUser: Boolean)

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TherapistRepository(application)
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        
        val currentList = _messages.value
        _messages.value = currentList + Message(text, true)
        _isLoading.value = true

        viewModelScope.launch {
            val response = repository.analyzeText(text)
            _messages.value = _messages.value + Message(response, false)
            _isLoading.value = false
        }
    }
}
