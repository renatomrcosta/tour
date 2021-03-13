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

    @MessageMapping("add")
    suspend fun addTimer(timerId: String, durationSeconds: Long) {
        require(timerId.isNotBlank())
        require(durationSeconds > 0L)

        // TODO consider adding a service layer
        val timer = Timer(duration = Duration.ofSeconds(durationSeconds))
        timerRepository.addTimer(timerId = timerId, timer = timer)
    }

    @MessageMapping("remove")
    suspend fun removeTimer(timerId: String) {
        require(timerId.isNotBlank())
        timerRepository.deleteTimer(timerId = timerId)
    }
}
