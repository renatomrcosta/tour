package com.xunfos.tour.ui

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Notifier
import com.xunfos.tour.common.timer.TimerStatus
import com.xunfos.tour.ui.client.TimerClient
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import uk.co.caprica.vlcj.player.component.AudioPlayerComponent
import java.time.Duration

fun getAlarmPathOrNull(): String? = {}.javaClass.classLoader.getResource("bell.mp3")?.path



fun main() {
    val audioPlayer = AudioPlayerComponent()
    val alarmAudio = getAlarmPathOrNull()
    val timerClient = TimerClient()
    val notifier = Notifier()

    Window(title = "tour stopwatch") {
        val composableScope = rememberCoroutineScope()

        var timerId by remember { mutableStateOf("test") }
        var timerTemp by remember { mutableStateOf("test") }
        var minutes by remember { mutableStateOf("12") }

        var remoteTimerState = timerClient.connectToRemoteTimer(timerId = timerId)

        val labelState = remoteTimerState
            ?.map { it.duration.asString() }
            ?.collectAsState("--:--")
            ?: mutableStateOf("connection error")

        val timerLabel by remember { labelState }

        val buttonState = remoteTimerState
            ?.map { it.status.toLabel() }
            ?.collectAsState("--")
            ?: mutableStateOf("connection error")

        val buttonLabel by remember { buttonState }

        composableScope.launch {
            remoteTimerState?.let { state ->
                state.map { it.status }
                    .filter { it == TimerStatus.FINISHED }
                    .collect {
                        launch { notifier.notify(title = "Time's up!", message = "Time to switch driver!") }
                        launch { alarmAudio?.let { audioPlayer.mediaPlayer().media().play(alarmAudio) } }
                    }
            }
        }

        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = timerTemp,
                        label = { Text("timer: ") },
                        onValueChange = { timerTemp = it }
                    )
                    Button(onClick = {
                        timerId = timerTemp
                    }) {
                        Text("Join remote timer")
                    }
                }
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column {
                    Text(text = timerLabel)
                    Row {
                        Button(onClick = {
                            composableScope.launch {
                                timerClient.toggleTimerState(timerId)
                            }
                        }) {
                            Text(buttonLabel)
                        }
                        Button(onClick = {
                            composableScope.launch {
                                timerClient.reset(timerId)
                            }
                        }) {
                            Text("Reset")
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
//                        Button(onClick = {
//                            composableScope.launch {
//                                val temp = timerId
//                                timerId = ""
//                                timerId = temp
//                            }
//                        }) {
//                            Text("Reconnect to Server")
//                        }

                        OutlinedTextField(
                            value = minutes,
                            label = { Text("Minutes in timer: ") },
                            onValueChange = { minutes = it }
                        )

                        Button(onClick = {
                            composableScope.launch {
                                timerTemp = timerClient.createRemoteTimer(minutes.toLong())
                                timerId = timerTemp
                            }
                        }) {
                            Text("Create remote timer")
                        }
                    }
                }
            }
        }
    }
}

private fun TimerStatus.toLabel() = when (this) {
    TimerStatus.STOPPED, TimerStatus.FINISHED -> "Start"
    TimerStatus.PAUSED -> "Resume"
    TimerStatus.PLAYING -> "Pause"
}


fun Duration.asString(): String {
    return "${this.toMinutesPart().padded()}:${this.toSecondsPart().padded()}"
}

fun Int.padded(): String =
    this.toString().padStart(2, '0')
