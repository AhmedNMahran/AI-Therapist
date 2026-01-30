


package com.github.ahmednmahran.aitherapist.data

import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TherapistRepository(private val context: Context) {

    private val onDeviceAI = OnDeviceAI(context)

    // Cloud AI (Firebase AI Logic SDK)
    private val cloudModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-2.0-flash", // Updated to a stable version if available, or keep as is
            generationConfig = generationConfig {
                temperature = 0.7f
            }
        )
    }

    private fun isOnline(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    suspend fun analyzeText(prompt: String, requiresDeepReasoning: Boolean = false): String = withContext(Dispatchers.IO) {
        val decision = SmartRouter.routeRequest(
            hasImage = false,
            hasAudio = false,
            isOnline = isOnline(),
            requiresDeepReasoning = requiresDeepReasoning,
            isLocalSupported = onDeviceAI.isSupported()
        )

        if (decision == RouterDecision.CLOUD) {
            try {
                val response = cloudModel.generateContent(prompt)
                response.text ?: "I am listening."
            } catch (e: Exception) {
                // Fallback to local if cloud fails
                onDeviceAI.generateResponse(prompt)
            }
        } else {
            onDeviceAI.generateResponse(prompt)
        }
    }

    suspend fun analyzeImage(prompt: String, image: Bitmap, requiresDeepReasoning: Boolean = true): String = withContext(Dispatchers.IO) {
        val decision = SmartRouter.routeRequest(
            hasImage = true,
            hasAudio = false,
            isOnline = isOnline(),
            requiresDeepReasoning = requiresDeepReasoning,
            isLocalSupported = onDeviceAI.isSupported()
        )

        if (decision == RouterDecision.CLOUD) {
            try {
                val inputContent = content {
                    image(image)
                    text(prompt)
                }
                val response = cloudModel.generateContent(inputContent)
                response.text ?: "I see the image, but I can't form an opinion yet."
            } catch (e: Exception) {
                onDeviceAI.describeImage(image)
            }
        } else {
            onDeviceAI.describeImage(image)
        }
    }

    suspend fun analyzeAudio(prompt: String, audioBytes: ByteArray): String = withContext(Dispatchers.IO) {
        // Audio usually routes to cloud in this version
        val decision = SmartRouter.routeRequest(
            hasImage = false,
            hasAudio = true,
            isOnline = isOnline(),
            requiresDeepReasoning = true,
            isLocalSupported = onDeviceAI.isSupported()
        )

        if (decision == RouterDecision.CLOUD) {
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
        } else {
            "Audio analysis is currently only available online."
        }
    }
}

