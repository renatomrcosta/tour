package com.xunfos.tour.server.controller

import com.xunfos.tour.server.repository.TimerRepository
import com.xunfos.tour.common.timer.Timer;
import com.xunfos.tour.common.timer.TimerState;
import kotlinx.coroutines.flow.Flow
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import java.time.Duration

@Controller
class TimerController(private val timerRepository: TimerRepository) {
    @MessageMapping("track")
    suspend fun trackTimer(timerId: String): Flow<TimerState> {
        val timer = timerRepository.getTimerOrNull(timerId) ?: error("failed to find timer")
        return timer.state
    }

    @MessageMapping("start")
    suspend fun start(timerId: String) {
        val timer = timerRepository.getTimerOrNull(timerId) ?: error("failed to find timer")
        timer.start()
    }

    @MessageMapping("reset")
    suspend fun reset(timerId: String) {
        val timer = timerRepository.getTimerOrNull(timerId) ?: error("failed to find timer")
        timer.reset()
    }

    @MessageMapping("toggle")
    suspend fun toggle(timerId: String) {
        val timer = timerRepository.getTimerOrNull(timerId) ?: error("failed to find timer")
        timer.togglePauseOrResume()
    }

    private val charRange = ('a'..'z')

    @MessageMapping("add")
    suspend fun addTimer(durationSeconds: Long): String {
        // TODO consider adding a service layer
        require(durationSeconds > 0L)
        val id = (1..4).map { charRange.random() }.joinToString("")
        val timer = Timer(duration = Duration.ofSeconds(durationSeconds))
        timerRepository.addTimer(timerId = id, timer = timer)
        return id
    }

    @MessageMapping("remove")
    suspend fun removeTimer(timerId: String) {
        require(timerId.isNotBlank())
        timerRepository.deleteTimer(timerId = timerId)
    }
}
