package com.example.geminichatbot

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.geminichatbot.ui.theme.ChatUiEvent
import com.example.geminichatbot.ui.theme.ChatViewModel
import com.example.geminichatbot.ui.theme.GeminiChatBotTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MainActivity : ComponentActivity() {

    private val uriState = MutableStateFlow("")

    private val imagePicker =
        registerForActivityResult<PickVisualMediaRequest, Uri>(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            uri?.let {
                uriState.update {
                    uri.toString()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GeminiChatBotTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primary)
                                    .height(55.dp)
                                    .padding(horizontal = 16.dp)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.CenterStart),
                                    text = "Gemini Chat App",
                                    fontSize = 19.sp,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        },
                        content = { ChatScreen(paddingValues = it) }
                    )
                }
            }
        }
    }

    @Composable
    fun ChatScreen(paddingValues: PaddingValues) {
        val chatViewModel = viewModel<ChatViewModel>()
        val chatState = chatViewModel.chatState.collectAsState().value
        val bitmap = getBitMap()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding()),
            verticalArrangement = Arrangement.Bottom,

            ) {


            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                reverseLayout = true
            ) {
                itemsIndexed(chatState.chatList) { index, chat ->
                    if (chat.isFromUser) {
                        UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap)
                    } else {
                        ModelChatItem(response = chat.prompt)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                Column {
                    bitmap?.let {
                        Image(
                            modifier = Modifier
                                .size(40.dp)
                                .padding(bottom = 2.dp)
                                .clip(RoundedCornerShape(6.dp)),
                            contentDescription = "Picked Image",
                            contentScale = ContentScale.Crop,
                            bitmap = it.asImageBitmap()
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest
                                        .Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            },
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    modifier = Modifier.weight(1f),
                    value = chatState.prompt,
                    onValueChange = {
                        chatViewModel.onEvent(ChatUiEvent.updatePrompt(it))
                    },
                    placeholder = {
                        Text(text = "Type a prompt")
                    }
                )
                Icon(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            chatViewModel.onEvent(ChatUiEvent.sendPrompt(chatState.prompt, bitmap))
                            uriState.update { "" }
                        },
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Send Photo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }



        Spacer(modifier = Modifier.width(8.dp))


    }


    @Composable
    fun UserChatItem(prompt: String, bitmap: Bitmap?,isUser :Boolean = true) {
        Column(
            modifier = Modifier
                .padding(start = if(isUser) 100.dp else 0.dp, bottom = 22.dp, end  = if(isUser) 0.dp else 100.dp)
        ) {
            if(isUser)
            bitmap?.let {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentDescription = "Image",
                    contentScale = ContentScale.Crop,
                    bitmap = it.asImageBitmap()
                )
            }

            Text(
                text = prompt,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
                    .clip(RoundedCornerShape(40.dp))
                    .padding(16.dp),
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }


    @Composable
    fun ModelChatItem(response: String) {
        Column(
            modifier = Modifier.padding(end = 100.dp, bottom = 22.dp)
        ) {
            Text(
                text = response,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(16.dp),
                fontSize = 17.sp,
                color = MaterialTheme.colorScheme.onTertiary
            )
        }
    }

    @Composable
    private fun getBitMap(): Bitmap? {
        val uri = uriState.collectAsState().value
        val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .data(uri)
                .size(Size.ORIGINAL)
                .build()
        ).state

        if (imageState is AsyncImagePainter.State.Success) {
            return imageState.result.drawable.toBitmap()
        }
        return null
    }
}
