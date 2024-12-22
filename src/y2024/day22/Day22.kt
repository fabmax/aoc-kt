package y2024.day22

import AocPuzzle

fun main() = Day22.runAll()

object Day22 : AocPuzzle<Long, Int>() {
    override fun solve1(input: List<String>): Long {
        return input.map { it.toInt() }.sumOf { seed ->
            generateSequence(seed) { rng(it) }.drop(2000).first().toLong()
        }
    }

    override fun solve2(input: List<String>): Int {
        val maxPrices = mutableMapOf<List<Int>, Int>()
        input.map { it.toInt() }.forEach { seed ->
            generateSequence(seed) { rng(it) }
                .windowed(2)
                .map { (a, b) -> PriceItem(b.lastDigit, b.lastDigit - a.lastDigit) }
                .take(2001)
                .windowed(4)
                .map { wnd -> wnd.map { it.delta } to wnd.last().price }
                .distinctBy { it.first }
                .forEach { (deltas, price) ->
                    maxPrices[deltas] = (maxPrices[deltas] ?: 0) + price
                }
        }
        return maxPrices.values.max()
    }

    private fun rng(seed: Int): Int {
        var state = seed
        state = (state xor (state * 64)) and 0xffffff
        state = (state xor (state / 32)) and 0xffffff
        state = (state xor (state * 2048)) and 0xffffff
        return state
    }

    private val Int.lastDigit: Int get() = this % 10

    private data class PriceItem(val price: Int, val delta: Int)
}
