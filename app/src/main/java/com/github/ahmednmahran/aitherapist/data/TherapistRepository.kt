


package com.github.ahmednmahran.aitherapist.data

import android.graphics.Bitmap
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TherapistRepository {

    // Cloud AI (Firebase AI Logic SDK) - Rebranded version of Vertex AI in Firebase
    private val cloudModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-2.5-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    // ...existing code...

    suspend fun analyzeText(prompt: String): String = withContext(Dispatchers.IO) {
        try {
            val response = cloudModel.generateContent(prompt)
            response.text ?: "I am listening."
        } catch (e: Exception) {
            "Connection issue: ${e.localizedMessage}"
        }
    }

    suspend fun analyzeImage(prompt: String, image: Bitmap): String = withContext(Dispatchers.IO) {
        try {
            val inputContent = content {
                image(image)
                text(prompt)
            }
            val response = cloudModel.generateContent(inputContent)
            response.text ?: "I see the image, but I can't form an opinion yet."
        } catch (e: Exception) {
            "Error analyzing image: ${e.localizedMessage}"
        }
    }

    suspend fun analyzeAudio(prompt: String, audioBytes: ByteArray): String = withContext(Dispatchers.IO) {
        try {
            val inputContent = content {
                inlineData(audioBytes, "audio/mp4")
                text(prompt)
            }
            val response = cloudModel.generateContent(inputContent)
            response.text ?: "I heard you, but I'm not sure what to say."
        } catch (e: Exception) {
            "Error analyzing audio: ${e.localizedMessage}"
        }
    }
}

