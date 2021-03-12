import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.time.Duration

fun main(): Unit = runBlocking {
    val timer = Timer(Duration.ofSeconds(4))
    launch {
        timer.durationState.collect {
            println(it.asString())
        }
    }
    launch {
        timer.start { println("ta-da!") }
    }
    launch {
        timer.start { println("ta-654654654!") }
    }
}
