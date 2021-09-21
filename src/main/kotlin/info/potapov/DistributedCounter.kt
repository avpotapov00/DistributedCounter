package info.potapov

import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 *  Counter implementation with a distributed array of internal counters
 */
class DistributedCounter(
    arraySize: Int = 4
) {

    private val array = Array(arraySize) { AtomicInteger(0) }
    private val random = Random(0)

    val count: Int
        get() = array.sumOf { it.get() }

    fun increment(delta: Int) {
        val cell = random.nextInt(array.size)
        array[cell].addAndGet(delta)
    }

}