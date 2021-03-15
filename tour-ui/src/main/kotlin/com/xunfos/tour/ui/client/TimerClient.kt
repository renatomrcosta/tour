package com.xunfos.tour.ui.client

import com.xunfos.tour.common.timer.TimerState
import kotlinx.coroutines.flow.Flow
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveAndAwait
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.messaging.rsocket.sendAndAwait
import org.springframework.util.MimeTypeUtils

class TimerClient {
    private val requestBuilder = RSocketRequester.builder()
        .rsocketStrategies {
            it.encoders { it.add(Jackson2JsonEncoder())}
            it.decoders { it.add(Jackson2JsonDecoder()) }
        }
        .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
//        .tcp("localhost", 9090)
        .tcp("4.tcp.eu.ngrok.io", 19424)

    fun connectToRemoteTimer(id: String) =
        requestBuilder
            .route("track")
            .data(id)
            .retrieveFlow<TimerState>()

    suspend fun createRemoteTimer(minutes: Long) =
        requestBuilder
            .route("add")
            .data(minutes.toSeconds())
            .retrieveAndAwait<String>()

    suspend fun toggleTimerState(id: String) {
        requestBuilder
            .route("toggle")
            .data(id)
            .sendAndAwait()
    }

    suspend fun deleteTimer(id: String) {
        requestBuilder
            .route("delete")
            .data(id)
            .sendAndAwait()
    }
}

private fun Long.toSeconds()= this * 60L

