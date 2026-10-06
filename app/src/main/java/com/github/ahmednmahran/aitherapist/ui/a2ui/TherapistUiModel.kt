package com.github.ahmednmahran.aitherapist.ui.a2ui

import org.json.JSONObject

/**
 * Structured model representing an empathetic A2UI therapy response.
 */
data class TherapistUiModel(
    val headline: String,
    val empathyMessage: String,
    val moodTag: String,
    val copingSteps: List<String>,
    val affirmation: String,
    val suggestedActions: List<String>,
    val rawText: String
) {
    companion object {
        fun fromResponse(text: String): TherapistUiModel {
            // Attempt to parse JSON if model returned structured output
            return try {
                val clean = text.trim().removeSurrounding("```json", "```").trim()
                if (clean.startsWith("{") && clean.endsWith("}")) {
                    val obj = JSONObject(clean)
                    val headline = obj.optString("headline", "Therapeutic Insight")
                    val empathy = obj.optString("empathyMessage", obj.optString("message", text))
                    val mood = obj.optString("moodTag", "Compassionate Care")
                    val affirmation = obj.optString("affirmation", "You are resilient, worthy, and capable of healing.")
                    
                    val stepsList = mutableListOf<String>()
                    val stepsArray = obj.optJSONArray("copingSteps")
                    if (stepsArray != null) {
                        for (i in 0 until stepsArray.length()) {
                            stepsList.add(stepsArray.getString(i))
                        }
                    }

                    val actionsList = mutableListOf<String>()
                    val actionsArray = obj.optJSONArray("suggestedActions")
                    if (actionsArray != null) {
                        for (i in 0 until actionsArray.length()) {
                            actionsList.add(actionsArray.getString(i))
                        }
                    }

                    TherapistUiModel(
                        headline = headline,
                        empathyMessage = empathy,
                        moodTag = mood,
                        copingSteps = if (stepsList.isNotEmpty()) stepsList else defaultCopingSteps(),
                        affirmation = affirmation,
                        suggestedActions = if (actionsList.isNotEmpty()) actionsList else defaultActions(),
                        rawText = text
                    )
                } else {
                    parseFromText(text)
                }
            } catch (e: Exception) {
                parseFromText(text)
            }
        }

        private fun parseFromText(text: String): TherapistUiModel {
            // Extract sections or generate meaningful interactive components
            val headline = when {
                text.contains("anxiety", ignoreCase = true) || text.contains("anxious", ignoreCase = true) -> "Calming Anxiety & Grounding"
                text.contains("stress", ignoreCase = true) -> "Stress Relief & Restoration"
                text.contains("sad", ignoreCase = true) || text.contains("depress", ignoreCase = true) -> "Gentle Support & Compassion"
                text.contains("expression", ignoreCase = true) -> "Emotional Tone & Body Language"
                text.contains("voice", ignoreCase = true) || text.contains("audio", ignoreCase = true) -> "Vocal & Tone Reflection"
                else -> "Empathetic Reflection"
            }

            val moodTag = when {
                text.contains("anxiety", ignoreCase = true) -> "Deep Breath Needed"
                text.contains("stress", ignoreCase = true) -> "Decompressing"
                text.contains("smile", ignoreCase = true) || text.contains("happy", ignoreCase = true) -> "Positive Resonance"
                else -> "Safe Space"
            }

            val affirmation = when {
                text.contains("anxiety", ignoreCase = true) -> "Take it one breath at a time. This feeling will pass."
                text.contains("stress", ignoreCase = true) -> "You don't have to carry everything all at once. Rest is productive."
                else -> "Your feelings are valid, and you are taking meaningful steps forward."
            }

            return TherapistUiModel(
                headline = headline,
                empathyMessage = text,
                moodTag = moodTag,
                copingSteps = defaultCopingSteps(),
                affirmation = affirmation,
                suggestedActions = defaultActions(),
                rawText = text
            )
        }

        private fun defaultCopingSteps(): List<String> = listOf(
            "Inhale slowly through your nose for 4 seconds",
            "Hold your breath gently for 4 seconds",
            "Exhale smoothly through your mouth for 6 seconds",
            "Notice one soothing sensation in your body right now"
        )

        private fun defaultActions(): List<String> = listOf(
            "🫁 4-7-8 Breathing",
            "🧘 5-4-3-2-1 Grounding",
            "📝 Journal Thoughts",
            "💭 Tell me more"
        )
    }
}
