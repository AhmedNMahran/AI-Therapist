package com.github.ahmednmahran.aitherapist.ui.audio

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.ahmednmahran.aitherapist.ui.a2ui.A2uiTherapistCard
import com.github.ahmednmahran.aitherapist.ui.a2ui.TherapistUiModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.io.File
import java.io.IOException

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AudioSessionScreen(viewModel: AudioViewModel = viewModel()) {
    val permissionState = rememberPermissionState(Manifest.permission.RECORD_AUDIO)
    val result by viewModel.analysisResult.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    LaunchedEffect(Unit) {
        if (!permissionState.status.isGranted) {
            permissionState.launchPermissionRequest()
        }
    }

    if (permissionState.status.isGranted) {
        AudioRecorderContent(viewModel, result, isLoading)
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Microphone permission needed")
            Button(onClick = { permissionState.launchPermissionRequest() }) {
                Text("Grant Permission")
            }
        }
    }
}

@Composable
fun AudioRecorderContent(viewModel: AudioViewModel, result: String, isLoading: Boolean) {
    val context = LocalContext.current
    var isRecording by remember { mutableStateOf(false) }
    var recorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var audioFile by remember { mutableStateOf<File?>(null) }
    val scrollState = rememberScrollState()

    DisposableEffect(Unit) {
        onDispose {
            recorder?.release()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Icon(
            if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = {
                if (isRecording) {
                    try {
                        recorder?.stop()
                    } catch (e: RuntimeException) {
                        // Handle stop called too early
                    }
                    recorder?.release()
                    recorder = null
                    isRecording = false
                    audioFile?.let { viewModel.analyzeRecording(it) }
                } else {
                    val file = File(context.cacheDir, "audio_session.mp4")
                    audioFile = file
                    recorder = (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        MediaRecorder(context)
                    } else {
                        @Suppress("DEPRECATION")
                        MediaRecorder()
                    }).apply {
                        setAudioSource(MediaRecorder.AudioSource.MIC)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                        setOutputFile(file.absolutePath)
                        try {
                            prepare()
                            start()
                            isRecording = true
                        } catch (e: IOException) {
                           // Handle error
                        }
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isRecording) "Stop & Analyze" else "Start Recording")
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading) {
            CircularProgressIndicator()
        } else if (result.isNotBlank()) {
            val uiModel = remember(result) { TherapistUiModel.fromResponse(result) }
            A2uiTherapistCard(
                model = uiModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
