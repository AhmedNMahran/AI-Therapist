package com.github.ahmednmahran.aitherapist.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.github.ahmednmahran.aitherapist.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("AI Therapist") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "How would you like to express yourself?",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            SessionCard(
                title = "Chat Session",
                description = "Text-based therapy",
                icon = Icons.AutoMirrored.Filled.Chat,
                onClick = { navController.navigate(Screen.Chat.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SessionCard(
                title = "Audio Session",
                description = "Voice analysis & support",
                icon = Icons.Default.Mic,
                onClick = { navController.navigate(Screen.Audio.route) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SessionCard(
                title = "Video Session",
                description = "Visual & emotional analysis",
                icon = Icons.Default.Videocam,
                onClick = { navController.navigate(Screen.Video.route) }
            )
        }
    }
}

@Composable
fun SessionCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(24.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
                Text(text = description, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
