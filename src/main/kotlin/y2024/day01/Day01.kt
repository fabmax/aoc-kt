package y2024.day01

import AocPuzzle
import extractNumbers
import kotlin.math.abs

fun main() = Day01.runAll()

object Day01 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val (left, right) = input.map { it.extractNumbers() }.map { (lt, rt) -> lt to rt }.unzip()
        return left.sorted().zip(right.sorted()).sumOf { (l, r) -> abs(l-r) }
    }

    override fun solve2(input: List<String>): Int {
        val (left, right) = input.map { it.extractNumbers() }.map { (lt, rt) -> lt to rt }.unzip()
        val rightOccs = right.groupingBy { it }.eachCount()
        return left.sumOf { it * (rightOccs[it] ?: 0) }
    }
}
