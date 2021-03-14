package com.xunfos.tour.common.timer

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration

class Timer(private val duration: Duration = DEFAULT_DURATION) {
    private val _state: MutableStateFlow<TimerState> = MutableStateFlow(TimerState(duration, TimerStatus.STOPPED))
    val state: StateFlow<TimerState> get() = _state

    // Probably I could have a flow with a delay, instead of a do while()
    suspend fun start(onComplete: () -> Unit = {}) {
        if (state.value.status == TimerStatus.PLAYING) return
        resetDuration()
        changeStatus(TimerStatus.PLAYING)
        do {
            wait()
            decreaseSecond()
        } while (state.value.duration != Duration.ZERO)
        changeStatus(TimerStatus.FINISHED)
        onComplete()
    }

    suspend fun togglePauseOrResume() {
        _state.value = when(state.value.status) {
            TimerStatus.STOPPED,
            TimerStatus.FINISHED -> {
                start()
                state.value
            }
            TimerStatus.PAUSED -> state.value.copy(status = TimerStatus.PLAYING)
            TimerStatus.PLAYING -> state.value.copy(status = TimerStatus.PAUSED)
        }
    }

    private suspend fun wait() = delay(1_000)

    private fun resetDuration() {
        _state.value = state.value.copy(duration = duration)
    }

    private fun changeStatus(newStatus: TimerStatus) {
        _state.value = state.value.copy(status = newStatus)
    }

    private fun decreaseSecond() {
        if (state.value.status == TimerStatus.PLAYING)
            _state.value = state.value.copy(duration = state.value.duration.minusSeconds(1))
    }

    companion object {
        val DEFAULT_DURATION: Duration = Duration.ofMinutes(12)
    }
}
