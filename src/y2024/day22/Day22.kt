package y2024.day22

import AocPuzzle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import java.util.concurrent.atomic.AtomicInteger

fun main() = Day22.runAll()

object Day22 : AocPuzzle<Long, Int>() {
    override fun solve1(input: List<String>): Long {
        return input.map { it.toInt() }.sumOf { seed ->
            generateSequence(seed) { rng(it) }.drop(2000).first().toLong()
        }
    }

    override fun solve2(input: List<String>): Int = part2Fast(input)

    fun part2Idiomatic(input: List<String>): Int {
        val maxPrices = mutableMapOf<List<Int>, Int>()
        input.map { it.toInt() }.forEach { seed ->
            generateSequence(seed) { rng(it) }
                .windowed(2)
                .map { (a, b) -> b.lastDigit - a.lastDigit to b.lastDigit }
                .take(2001)
                .windowed(4)
                .map { wnd -> wnd.map { it.first } to wnd.last().second }
                .distinctBy { it.first }
                .forEach { (deltas, price) ->
                    maxPrices[deltas] = (maxPrices[deltas] ?: 0) + price
                }
        }
        return maxPrices.values.max()
    }

    private fun rng(seed: Int): Int {
        var state = seed
        state = (state xor (state shl 6)) and 0xffffff
        state = (state xor (state shr 5)) and 0xffffff
        state = (state xor (state shl 11)) and 0xffffff
        return state
    }

    private val Int.lastDigit: Int get() = this % 10

    private fun part2Fast(input: List<String>): Int {
        val sumPrices = List(19 * 18 * 18 * 18) { AtomicInteger() }
        return runBlocking(Dispatchers.Default) {
            input
                .chunked((input.size / 32).coerceAtLeast(1))
                .map { workload -> async { process(workload, sumPrices) } }
                .awaitAll().max()
        }
    }

    private fun process(workload: List<String>, sums: List<AtomicInteger>): Int {
        val localSums = IntArray(sums.size)

        for (seed in workload) {
            val seenFirst = BooleanArray(19 * 18 * 18 * 18)

            var state = seed.toInt()
            var prevPrice = 0
            var d1 = 0; var d2 = 0; var d3 = 0; var d4 = 0

            for (i in 1..2000) {
                state = rng(state)
                val price = state.lastDigit

                d1 = d2; d2 = d3; d3 = d4
                d4 = price - prevPrice
                prevPrice = price

                if (i >= 4) {
                    val idx = index(d1, d2, d3, d4)
                    if (!seenFirst[idx]) {
                        seenFirst[idx] = true
                        val p = localSums[idx] + price
                        localSums[idx] = p
                    }
                }
            }
        }

        var maxPrice = 0
        for (i in localSums.indices) {
            val p = sums[i].addAndGet(localSums[i])
            if (p > maxPrice) {
                maxPrice = p
            }
        }
        return maxPrice
    }

    private fun index(d1: Int, d2: Int, d3: Int, d4: Int): Int {
        return (d1 + 9) * (18*18*18) + (d2 + 9) * (18*18) + (d3 + 9) * 18 + (d4 + 9)
    }
}
