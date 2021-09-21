package info.potapov

import org.jetbrains.kotlinx.lincheck.LincheckAssertionError
import org.jetbrains.kotlinx.lincheck.annotations.Operation
import org.jetbrains.kotlinx.lincheck.annotations.Param
import org.jetbrains.kotlinx.lincheck.check
import org.jetbrains.kotlinx.lincheck.paramgen.IntGen
import org.jetbrains.kotlinx.lincheck.strategy.managed.modelchecking.ModelCheckingOptions
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions
import org.jetbrains.kotlinx.lincheck.verifier.VerifierState
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Test that checks non-linearizable behavior for a counter with an arbitrary increment
 */
internal class DistributedRandomCounterTest {

    private val counter = DistributedCounter(4)

    @Operation
    fun increment(@Param(gen = IntGen::class, conf = "1:4") delta: Int) = counter.increment(delta)

    val count: Int @Operation get() = counter.count

    @Test
    fun modelCheckingTest() {
        assertThrows<LincheckAssertionError> {
            ModelCheckingOptions()
                .iterations(10)
                .invocationsPerIteration(100_000)
                .threads(3)
                .actorsPerThread(4)
                .sequentialSpecification(CounterVerifierState::class.java)
                .check(this::class.java)
        }
    }

    @Test
    fun stressTest() {
        assertThrows<LincheckAssertionError> {
            StressOptions()
                .iterations(100)
                .invocationsPerIteration(100_000)
                .actorsBefore(2)
                .actorsAfter(2)
                .threads(3)
                .actorsPerThread(3)
                .sequentialSpecification(CounterVerifierState::class.java)
                .check(this::class.java)
        }
    }


    class CounterVerifierState : VerifierState() {
        private var counter = 0

        fun increment(delta: Int) {
            counter += delta
        }

        val count: Int get() = counter

        override fun extractState() = counter
    }

}