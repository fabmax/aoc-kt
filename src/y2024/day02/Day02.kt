package y2024.day02

import AocPuzzle

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
            .count { levels ->
                levels.isSafe() || levels.indices.any { i -> levels.toMutableList().apply { removeAt(i) }.isSafe() }
            }
    }

    private fun List<Int>.isSafe(): Boolean = isIncreasingSafe() || isDecreasingSafe()
    private fun List<Int>.isIncreasingSafe(): Boolean = windowed(2).all { (a, b) -> (b - a) in (1..3) }
    private fun List<Int>.isDecreasingSafe(): Boolean = windowed(2).all { (a, b) -> (a - b) in (1..3) }
}
