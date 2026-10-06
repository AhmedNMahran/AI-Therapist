package com.github.ahmednmahran.aitherapist.data

import com.google.firebase.ai.InferenceMode
import com.google.firebase.ai.OnDeviceConfig
import com.google.firebase.ai.type.PublicPreviewAPI

@OptIn(PublicPreviewAPI::class)
object SmartRouter {
    /**
     * Determines the hybrid inference mode based on:
     * 1. Connectivity (Online vs Offline)
     * 2. Complexity & Modality (Deep Reasoning, Audio, Text/Image)
     * 3. Hardware Support (Gemini Nano availability)
     */
    fun routeRequest(
        hasImage: Boolean = false,
        hasAudio: Boolean = false,
        isOnline: Boolean = true,
        requiresDeepReasoning: Boolean = false,
        isLocalSupported: Boolean = false
    ): OnDeviceConfig {
        val mode = when {
            // Audio or complex deep reasoning tasks prefer cloud inference
            (hasAudio || requiresDeepReasoning) && isOnline -> InferenceMode.PREFER_IN_CLOUD
            
            // When offline, attempt on-device inference if local hardware is supported
            !isOnline && isLocalSupported -> InferenceMode.ONLY_ON_DEVICE
            
            // If local processing is not supported on-device, use in-cloud inference
            !isLocalSupported -> InferenceMode.ONLY_IN_CLOUD
            
            // Default: prefer on-device with seamless fallback to in-cloud inference
            else -> InferenceMode.PREFER_ON_DEVICE
        }
        return OnDeviceConfig(mode = mode)
    }
}
