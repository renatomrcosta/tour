package com.xunfos.tour.ui

//import androidx.compose.desktop.Window
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.window.Notifier
//import kotlinx.coroutines.*
//import java.time.Duration
//
//@ExperimentalCoroutinesApi
//fun main() =
//    Window {
//        val timer = Timer(Duration.ofSeconds(5))
//        val timerState = timer.durationState
//            .map(Duration::asString)
//            .collectAsState("--:--")
//
//        val statusState = timer.statusState
//            .map { it.toLabel() }
//            .collectAsState(timer.statusState.value.toLabel())
//
//        val timerLabel by remember { timerState }
//        val btnStartStopLabel by remember { statusState }
//
//        val notifier = Notifier()
//
//        // Connect to firebase
//        // Make sure timer updates firebase and vice versa, and stops when done
//
//        // Then connect multiple people in the same instance and check their synchro
//        // Then add create clock / join clock buttons
//
//        val composableScope = rememberCoroutineScope()
//
//        MaterialTheme {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text(text = timerLabel)
//                Box(modifier= Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
//                    Button(
//                        onClick = {
//                            composableScope.launch {
//                                timer.start {
//                                    notifier.notify(
//                                        title = "Timer has ended!",
//                                        message = "Next person in line iiiiss: Banana!"
//                                    )
//                                }
//                            }
//                        }) {
//                        Text(btnStartStopLabel)
//                    }
//                }
//            }
//        }
//    }
//
//private fun TimerStatus.toLabel() = when(this) {
//    TimerStatus.STOPPED -> "Start"
//    TimerStatus.PAUSED -> "Continue"
//    TimerStatus.PLAYING -> "Pause"
//}
//
//
//fun Duration.asString(): String {
//    return "${this.toMinutesPart().padded()}:${this.toSecondsPart().padded()}"
//}
//
//fun Int.padded(): String =
//    this.toString().padStart(2, '0')
