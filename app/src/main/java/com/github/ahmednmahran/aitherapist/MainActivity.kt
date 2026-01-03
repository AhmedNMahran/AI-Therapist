package com.github.ahmednmahran.aitherapist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.ahmednmahran.aitherapist.ui.audio.AudioSessionScreen
import com.github.ahmednmahran.aitherapist.ui.chat.ChatScreen
import com.github.ahmednmahran.aitherapist.ui.home.HomeScreen
import com.github.ahmednmahran.aitherapist.ui.navigation.Screen
import com.github.ahmednmahran.aitherapist.ui.theme.AITherapistTheme
import com.github.ahmednmahran.aitherapist.ui.video.VideoSessionScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AITherapistTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AITherapistApp()
                }
            }
        }
    }
}

@Composable
fun AITherapistApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) { HomeScreen(navController) }
        composable(Screen.Chat.route) { ChatScreen() }
        composable(Screen.Audio.route) { AudioSessionScreen() }
        composable(Screen.Video.route) { VideoSessionScreen() }
    }
}