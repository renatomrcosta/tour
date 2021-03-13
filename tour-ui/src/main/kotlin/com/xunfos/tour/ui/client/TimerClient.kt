package com.xunfos.tour.ui.client

import com.xunfos.tour.common.timer.TimerState
import kotlinx.coroutines.flow.Flow
import org.springframework.http.codec.json.Jackson2JsonDecoder
import org.springframework.http.codec.json.Jackson2JsonEncoder

import org.springframework.messaging.rsocket.RSocketRequester
import org.springframework.messaging.rsocket.retrieveFlow
import org.springframework.util.MimeTypeUtils

class TimerClient {
    fun connectToRemoteTimer(): Flow<TimerState> {
        return RSocketRequester.builder()
            .rsocketStrategies {
                it.encoders {it.add(Jackson2JsonEncoder())}
                it.decoders { it.add(Jackson2JsonDecoder()) }
            }
            .dataMimeType(MimeTypeUtils.APPLICATION_JSON)
            .tcp("localhost", 9090)
            .route("track")
            .data("test")
            .retrieveFlow()
    }
}

