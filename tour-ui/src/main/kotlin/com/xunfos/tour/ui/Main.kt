package com.xunfos.tour.ui

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Notifier
import com.xunfos.tour.ui.client.TimerClient
import kotlinx.coroutines.launch
import java.time.Duration
import androidx.compose.runtime.getValue
import com.xunfos.tour.common.timer.TimerStatus
import kotlinx.coroutines.flow.map


fun main() = Window(title="tour stopwatch") {
    // Connect to firebase
    // Make sure timer updates firebase and vice versa, and stops when done

    // Then connect multiple people in the same instance and check their synchro
    // Then add create clock / join clock buttons

    val timerClient = TimerClient()
    val remoteTimerState = timerClient.connectToRemoteTimer()

    val labelState = remoteTimerState
        .map { it.duration.asString() }
        .collectAsState("--:--")

    val buttonState = remoteTimerState
        .map { it.status.toLabel() }
        .collectAsState("--")

    val timerLabel by remember { labelState }
    val buttonLabel by remember { buttonState }
    val composableScope = rememberCoroutineScope()

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = timerLabel)
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Button(onClick = {}) {
                    Text(buttonLabel)
                }
            }
        }
    }
}

private fun TimerStatus.toLabel() = when (this) {
    TimerStatus.STOPPED -> "Start"
    TimerStatus.PAUSED -> "Continue"
    TimerStatus.PLAYING -> "Pause"
}


fun Duration.asString(): String {
    return "${this.toMinutesPart().padded()}:${this.toSecondsPart().padded()}"
}

fun Int.padded(): String =
    this.toString().padStart(2, '0')
