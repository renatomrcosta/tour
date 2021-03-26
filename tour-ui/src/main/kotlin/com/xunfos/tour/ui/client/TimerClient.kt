package com.xunfos.tour.ui.client

import com.xunfos.tour.common.timer.TimerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.messaging.rsocket.sendAndAwait
import org.springframework.util.MimeTypeUtils
import java.lang.Exception
import java.net.ConnectException

class TimerClient {
    private val requestBuilder = RSocketRequester.builder()
        .rsocketStrategies {
            it.encoders { it.add(Jackson2JsonEncoder()) }
            it.decoders { it.add(Jackson2JsonDecoder()) }
        }
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
        .tcp("localhost", 9090)

    // TODO - make it suspend so exceptions aren't throwing on main
    fun connectToRemoteTimer(id: String) = try {
        requestBuilder
            .route("track")
            .data(id)
            .retrieveFlow<TimerState>()
    } catch (e: Exception) {
        null
    }

    suspend fun createRemoteTimer(minutes: Long) = withContext(Dispatchers.IO) {
        requestBuilder
            .route("add")
            .data(minutes.toSeconds())
            .retrieveAndAwait<String>()
    }

    suspend fun toggleTimerState(id: String) = withContext(Dispatchers.IO) {
        requestBuilder
            .route("toggle")
            .data(id)
            .sendAndAwait()
    }

    suspend fun deleteTimer(id: String) = withContext(Dispatchers.IO) {
        requestBuilder
            .route("delete")
            .data(id)
            .sendAndAwait()
    }
}

private fun Long.toSeconds() = this * 60L

