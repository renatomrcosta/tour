package com.xunfos.tour.ui

import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Notifier
import com.xunfos.tour.ui.client.TimerClient
import kotlinx.coroutines.launch
import java.time.Duration
import com.xunfos.tour.common.timer.TimerStatus
import kotlinx.coroutines.flow.*


fun main() = Window(title="tour stopwatch") {

    val notifier = Notifier()
    println("Does this log anything?")
    var timerId by remember { mutableStateOf("test") }
    var timerTemp by remember { mutableStateOf("test") }
    var minutes by remember { mutableStateOf("12") }

    val timerClient = TimerClient()
    var remoteTimerState = timerClient.connectToRemoteTimer(id = timerId)

    val labelState = remoteTimerState
        .map { it.duration.asString() }
        .collectAsState("--:--")

    val buttonState = remoteTimerState
        .map { it.status.toLabel() }
        .collectAsState("--")

    val timerLabel by remember { labelState }
    val buttonLabel by remember { buttonState }

    val composableScope = rememberCoroutineScope()

    composableScope.launch {
        remoteTimerState.map { it.status }
            .filter { it == TimerStatus.FINISHED }
            .collect {
            notifier.notify(title = "Time's up!", message = "Time to switch driver!")
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = timerTemp,
                    label = { Text("timer: ") },
                    onValueChange = { timerTemp = it }
                )
                Button(onClick = {
                    timerId = timerTemp
//                    remoteTimerState = timerClient.connectToRemoteTimer(id = timerTemp)
                }) {
                    Text("Join remote timer")
                }
            }
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column {
                Text(text = timerLabel)
                Button(onClick = {
                    composableScope.launch {
                        timerClient.toggleTimerState(timerId)
                    }
                }) {
                    Text(buttonLabel)
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = minutes,
                        label = { Text("Minutes in timer: ") },
                        onValueChange = { minutes = it }
                    )

                    Button(onClick = {
                        composableScope.launch {
                            timerTemp = timerClient.createRemoteTimer(minutes.toLong())
                            timerId = timerTemp
//                            remoteTimerState = timerClient.connectToRemoteTimer(id = timerTemp)
                        }
                    }) {
                        Text("Create remote timer")
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
