package y2024.day11

import AocPuzzle
import extractLongNumbers

fun main() = Day11.runAll()

object Day11 : AocPuzzle<Long, Long>() {
    override fun solve1(input: List<String>): Long {
        return input[0].extractLongNumbers().sumOf { blinkMemoized(it, 25) }
    }

    override fun solve2(input: List<String>): Long {
        return input[0].extractLongNumbers().sumOf { blinkMemoized(it, 75) }
    }

    val memo = mutableMapOf<Pair<Int, Long>, Long>()

    fun blinkMemoized(stone: Long, iteration: Int): Long {
        if (iteration == 0) { return 1L }
        val key = iteration to stone
        memo[key]?.let { return it }
        return stone.blinkSingle().sumOf { blinkMemoized(it, iteration - 1) }.also { memo[key] = it }
    }

    fun Long.blinkSingle(): List<Long> = buildList {
        val stone = this@blinkSingle
        val stoneString = "$stone"
        when {
            stone == 0L -> add(1L)
            stoneString.length % 2 == 0 -> {
                add(stoneString.substring(0, stoneString.length / 2).toLong())
                add(stoneString.substring(stoneString.length / 2, stoneString.length).toLong())
            }
            else -> add(stone * 2024)
        }
    }
}
