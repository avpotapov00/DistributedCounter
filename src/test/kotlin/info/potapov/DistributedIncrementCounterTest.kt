package info.potapov

import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.junit.jupiter.api.Test

/**
 * Test that checks linearizable behavior for a counter with an increment of 1
 */
internal class DistributedIncrementCounterTest {

    private val counter = DistributedCounter(4)

    @Operation
    fun increment() = counter.increment(1)

    val count: Int @Operation get() = counter.count

    @Test
    fun modelCheckingTest() = ModelCheckingOptions()
        .iterations(10)
        .invocationsPerIteration(100_000)
        .threads(3)
        .actorsPerThread(4)
        .sequentialSpecification(CounterVerifierState::class.java)
        .check(this::class.java)

    @Test
    fun stressTest() = StressOptions()
        .iterations(100)
        .invocationsPerIteration(100_000)
        .actorsBefore(2)
        .actorsAfter(2)
        .threads(3)
        .actorsPerThread(3)
        .sequentialSpecification(CounterVerifierState::class.java)
        .check(this::class.java)


    class CounterVerifierState : VerifierState() {
        private var counter = 0

        fun increment() {
            counter++
        }

        val count: Int get() = counter

        override fun extractState() = counter
    }

}