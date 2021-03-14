package com.xunfos.tour.server.repository

import com.xunfos.tour.common.timer.Timer
import org.springframework.stereotype.Repository
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class TimerRepository {
    private val inMemoryMap: ConcurrentMap<String, Timer> = ConcurrentHashMap()
    init {
        inMemoryMap["test"] = Timer(Duration.ofSeconds(5))
        inMemoryMap["test2"] = Timer(Duration.ofSeconds(65))
    }

    // TODO - consider not overwriting existing timers
    fun addTimer(timerId: String, timer: Timer) { inMemoryMap[timerId] = timer }
    fun deleteTimer(timerId: String)  { inMemoryMap.remove(key = timerId) }
    fun getTimerOrNull(timerId: String): Timer? = inMemoryMap.get(key = timerId)
}
