package com.xunfos.tour.ui

import uk.co.caprica.vlcj.player.component.AudioPlayerComponent

fun main() {
    val path = {}.javaClass.classLoader.getResource("bell.mp3")?.path ?: ""

    println(path)
    val audioPlayer = AudioPlayerComponent()
    audioPlayer.mediaPlayer().media().play(path)
}
