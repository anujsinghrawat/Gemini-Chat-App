package com.example.geminichatbot.data

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

object ChatData {
    val api_key = "AIzaSyCVyBcpcv6eZeqEZ3Yxf-iNwKKG1ou4mt4"

    suspend fun getResponse(prompt :String) :Chat{
        val generativeModel = GenerativeModel(
            // Use a model that's applicable for your use case (see "Implement basic use cases" below)
            modelName = "gemini-pro",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = api_key
        )
        try {
            val response = withContext(Dispatchers.IO){
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e:Exception){
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }

    suspend fun getResponseWithImage(prompt :String,bitmap: Bitmap) :Chat{

        val generativeModel = GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey = api_key
        )

        try {

            val inputContent = content {
                image(bitmap)
                text(prompt)
            }
            val response = withContext(Dispatchers.IO){
                generativeModel.generateContent(inputContent)
            }
            return Chat(
                prompt = response.text ?: "error",
                bitmap = null,
                isFromUser = false
            )

        } catch (e:Exception){
            return Chat(
                prompt = e.message ?: "error",
                bitmap = null,
                isFromUser = false
            )
        }
    }
}