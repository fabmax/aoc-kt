package y2024.day07

import AocPuzzle
import extractLongNumbers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import permutationSequence

fun main() = Day07.runAll()

object Day07 : AocPuzzle<Long, Long>() {
    override fun solve1(input: List<String>): Long {
        val testVals = input.map { it.substringBefore(":").toLong() }
        val numbers = input.map { it.substringAfter(":").extractLongNumbers() }
        val ops = listOf(Operator.Plus, Operator.Mul)

        return testVals
            .zip(numbers) { value, nums -> valueSequence(nums, ops).find { it == value } }
            .filterNotNull().sum()
    }

    override fun solve2(input: List<String>): Long {
        val testVals = input.map { it.substringBefore(":").toLong() }
        val numbers = input.map { it.substringAfter(":").extractLongNumbers() }
        val ops = listOf(Operator.Plus, Operator.Mul, Operator.Concat)

        return runBlocking(Dispatchers.Default) {
            testVals.indices.map { i ->
                async {
                    valueSequence(numbers[i], ops).find { it == testVals[i] }
                }
            }.awaitAll().filterNotNull().sum()
        }
    }

    fun valueSequence(nums: List<Long>, operators: List<Operator>) =
        permutationSequence(operators, nums.size - 1).map { applyOps ->
            nums.subList(1, nums.size).foldIndexed(nums.first()) { i, acc, num -> applyOps[i](acc, num) }
        }

    enum class Operator {
        Plus { override fun invoke(a: Long, b: Long) = a + b },
        Mul { override fun invoke(a: Long, b: Long) = a * b },
        Concat { override fun invoke(a: Long, b: Long) = "$a$b".toLong() };

        abstract operator fun invoke(a: Long, b: Long): Long
    }

    /*
    Benchmark             Mode  Cnt    Score    Error  Units
    Day07Benchmark.part1  avgt    5   20,264 ±  0,579  ms/op
    Day07Benchmark.part2  avgt    5  152,256 ±  8,489  ms/op
    */

}
