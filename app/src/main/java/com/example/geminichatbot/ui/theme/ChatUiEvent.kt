package com.example.geminichatbot.ui.theme

import android.graphics.Bitmap

sealed class ChatUiEvent {
    data class updatePrompt(val newPrompt: String) : ChatUiEvent()
    data class sendPrompt(val prompt: String, val bitmap: Bitmap?) : ChatUiEvent()

}