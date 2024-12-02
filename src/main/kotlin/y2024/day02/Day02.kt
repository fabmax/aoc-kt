package y2024.day02

import AocPuzzle
import extractNumbers
import y2024.day02.Day02.isSafe
import kotlin.math.abs

fun main() = Day02.runAll()

object Day02 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        return input
            .map { it.split(" ").map(String::toInt) }
            .count { it.isSafe() }
    }

    override fun solve2(input: List<String>): Int {
        return input
            .map { it.split(" ").map(String::toInt) }
            .count { it.isSafe2() }
    }

    private fun List<Int>.isSafe(): Boolean {
        return isIncreasingSafe() || isDecreasingSafe()
    }

    private fun List<Int>.isSafe2(): Boolean {
        for (i in indices) {
            val safer = toMutableList()
            safer.removeAt(i)
            if (safer.isSafe()) {
                return true
            }
        }
        return isSafe()
    }

    private fun List<Int>.isIncreasingSafe(): Boolean {
        var prev = first()
        for (i in 1 until size) {
            val n = get(i)
            if (n <= prev || n - prev > 3) {
                return false
            }
            prev = n
        }
        return true
    }

    private fun List<Int>.isDecreasingSafe(): Boolean {
        var prev = first()
        for (i in 1 until size) {
            val n = get(i)
            if (n >= prev || prev - n > 3) {
                return false
            }
            prev = n
        }
        return true
    }
}
