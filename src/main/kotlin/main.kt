import androidx.compose.desktop.Window
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Notifier
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Duration

@ExperimentalCoroutinesApi
fun main() =
    Window {
        val timer = Timer(Duration.ofSeconds(5))
        val timerState = timer.durationState
            .map(Duration::asString)
            .collectAsState("--:--")

        val statusState = timer.statusState
            .map { it.toLabel() }
            .collectAsState(timer.statusState.value.toLabel())

        val timerLabel by remember { timerState }
        val btnStartStopLabel by remember { statusState }

        val notifier = Notifier()

        // Connect to firebase
        // Make sure timer updates firebase and vice versa, and stops when done

        // Then connect multiple people in the same instance and check their synchro
        // Then add create clock / join clock buttons

        val composableScope = rememberCoroutineScope()

        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = timerLabel)
                Box(modifier= Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                    Button(
                        onClick = {
                            composableScope.launch {
                                timer.start {
                                    notifier.notify(
                                        title = "Timer has ended!",
                                        message = "Next person in line iiiiss: Banana!"
                                    )
                                }
                            }
                        }) {
                        Text(btnStartStopLabel)
                    }
                }
            }
        }
    }

private fun TimerStatus.toLabel() = when(this) {
    TimerStatus.STOPPED -> "Start"
    TimerStatus.PAUSED -> "Continue"
    TimerStatus.PLAYING -> "Pause"
}


fun Duration.asString(): String {
    return "${this.toMinutesPart().padded()}:${this.toSecondsPart().padded()}"
}

private fun Int.padded(): String =
    this.toString().padStart(2, '0')

class Timer(private val duration: Duration = Duration.ofMinutes(12)) {
    private val durationMutex = Mutex()
    private val _durationState: MutableStateFlow<Duration> = MutableStateFlow(duration)
    val durationState: StateFlow<Duration> get() = _durationState


    private val statusMutex = Mutex()
    private var _statusState = MutableStateFlow(TimerStatus.STOPPED)
    val statusState:StateFlow<TimerStatus> get() = _statusState

    suspend fun start(onComplete: () -> Unit = {}) {
        if(_statusState.value == TimerStatus.PLAYING) return
        resetDuration()
        changeStatus(TimerStatus.PLAYING)
        do {
            wait()
            decreaseSecond()
        } while (durationState.value != Duration.ZERO)
        changeStatus(TimerStatus.STOPPED)
        onComplete()
    }

    private suspend fun resetDuration() {
        durationMutex.withLock {
            _durationState.value = duration
        }
    }

    private suspend fun changeStatus(newStatus: TimerStatus) {
        statusMutex.withLock {
            _statusState.value = newStatus
        }
    }

    private suspend fun wait() = delay(1_000)
    private suspend fun decreaseSecond() {
        durationMutex.withLock {
            _durationState.value = _durationState.value.minusSeconds(1)
        }
    }
}

enum class TimerStatus { STOPPED, PAUSED, PLAYING}
