package com.xunfos.tour.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TourServerApplication

fun main(args: Array<String>) {
    runApplication<TourServerApplication>(*args)
}
