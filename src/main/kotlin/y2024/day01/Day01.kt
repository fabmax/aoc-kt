package y2024.day01

import AocPuzzle
import extractNumbers
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs

fun main() = Day01.runAll()

object Day01 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val left = mutableListOf<Int>()
        val right = mutableListOf<Int>()
        input.map { it.extractNumbers() }.forEach { (lt, rt) ->
            left += lt
            right += rt
        }
        return left.sorted().zip(right.sorted()).sumOf { (l, r) -> abs(l-r) }
    }

    override fun solve2(input: List<String>): Int {
        val left = mutableListOf<Int>()
        val right = mutableMapOf<Int, AtomicInteger>()
        input.map { it.extractNumbers() }.forEach { (lt, rt) ->
            left += lt
            right.getOrPut(rt) { AtomicInteger() }.incrementAndGet()
        }
        return left.sumOf { it * (right[it]?.get() ?: 0) }
    }
}
