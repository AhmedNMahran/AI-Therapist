package com.github.ahmednmahran.aitherapist.data

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.Firebase
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.OnDeviceConfig
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.PublicPreviewAPI
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(PublicPreviewAPI::class)
class TherapistRepository(private val context: Context) {

    private fun getHybridModel(onDeviceConfig: OnDeviceConfig): GenerativeModel {
        return Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                temperature = 0.7f
            },
            onDeviceConfig = onDeviceConfig
        )
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun buildPromptWithUiInstruction(prompt: String, modalityContext: String = ""): String {
        return """
            You are a compassionate, professional AI Therapist and mental wellness companion.
            $modalityContext
            Respond warmly, empathetically, and constructively.
            Format your response strictly as a JSON object with this structure:
            {
              "headline": "Brief empathetic title (e.g., Calming Your Anxiety, Finding Peace)",
              "empathyMessage": "Your warm, thoughtful therapeutic response and reflections",
              "moodTag": "Short emotional focus tag (e.g., Mindfulness, Calm, Grounding, Healing)",
              "copingSteps": [
                "Actionable grounding or coping step 1",
                "Actionable grounding or coping step 2",
                "Actionable grounding or coping step 3"
              ],
              "affirmation": "A brief uplifting and comforting affirmation",
              "suggestedActions": [
                "Suggested response or question 1",
                "Suggested response or question 2",
                "Suggested response or question 3"
              ]
            }

            User input / context:
            $prompt
        """.trimIndent()
    }

    suspend fun analyzeText(prompt: String, requiresDeepReasoning: Boolean = false): String = withContext(Dispatchers.IO) {
        val onDeviceConfig = SmartRouter.routeRequest(
            hasImage = false,
            hasAudio = false,
            isOnline = isOnline(),
            requiresDeepReasoning = requiresDeepReasoning
        )

        try {
            val model = getHybridModel(onDeviceConfig)
            val fullPrompt = buildPromptWithUiInstruction(prompt)
            val response = model.generateContent(fullPrompt)
            response.text ?: "I am listening."
        } catch (e: Exception) {
            "Firebase AI error: ${e.localizedMessage ?: e.message}"
        }
    }

    suspend fun analyzeImage(prompt: String, image: Bitmap, requiresDeepReasoning: Boolean = true): String = withContext(Dispatchers.IO) {
        val onDeviceConfig = SmartRouter.routeRequest(
            hasImage = true,
            hasAudio = false,
            isOnline = isOnline(),
            requiresDeepReasoning = requiresDeepReasoning
        )

        try {
            val model = getHybridModel(onDeviceConfig)
            val fullPrompt = buildPromptWithUiInstruction(prompt, "Observe the provided image carefully to detect mood, expression, or emotional indicators.")
            val inputContent = content {
                image(image)
                text(fullPrompt)
            }
            val response = model.generateContent(inputContent)
            response.text ?: "I see the image, but I can't form an opinion yet."
        } catch (e: Exception) {
            "Firebase AI error: ${e.localizedMessage ?: e.message}"
        }
    }

    suspend fun analyzeAudio(prompt: String, audioBytes: ByteArray): String = withContext(Dispatchers.IO) {
        val onDeviceConfig = SmartRouter.routeRequest(
            hasImage = false,
            hasAudio = true,
            isOnline = isOnline(),
            requiresDeepReasoning = true
        )

        try {
            val model = getHybridModel(onDeviceConfig)
            val fullPrompt = buildPromptWithUiInstruction(prompt, "Listen to the attached voice recording and analyze vocal tone, cadence, and emotion.")
            val inputContent = content {
                inlineData(audioBytes, "audio/mp4")
                text(fullPrompt)
            }
            val response = model.generateContent(inputContent)
            response.text ?: "I heard you, but I'm not sure what to say."
        } catch (e: Exception) {
            "Firebase AI error: ${e.localizedMessage ?: e.message}"
        }
    }
}
