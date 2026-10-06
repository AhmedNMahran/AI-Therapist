package com.github.ahmednmahran.aitherapist.data

enum class RouterDecision { CLOUD, LOCAL }

object SmartRouter {
    /**
     * Decisions based on:
     * 1. Connectivity (Online vs Offline)
     * 2. Complexity (Deep Reasoning vs Simple Task)
     * 3. Hardware Support (Gemini Nano availability)
     */
    fun routeRequest(
        hasImage: Boolean,
        hasAudio: Boolean,
        isOnline: Boolean,
        requiresDeepReasoning: Boolean,
        isLocalSupported: Boolean
    ): RouterDecision {
        return when {
            // Audio is generally not supported well on-device yet in this beta, so cloud it is
            hasAudio && isOnline -> RouterDecision.CLOUD
            
            // If we are offline, we HAVE to go local if supported
            !isOnline && isLocalSupported -> RouterDecision.LOCAL
            
            // If local is not supported at all, we must go cloud
            !isLocalSupported && isOnline -> RouterDecision.CLOUD
            
            // Deep reasoning (complex therapy plans) usually needs the Cloud Giant
            requiresDeepReasoning && isOnline -> RouterDecision.CLOUD
            
            // Simple text or image description? Local Hero is faster
            else -> RouterDecision.LOCAL
        }
    }
}
