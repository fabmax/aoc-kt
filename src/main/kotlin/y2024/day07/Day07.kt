package y2024.day07

import AocPuzzle
import extractLongNumbers

fun main() = Day07.runAll()

object Day07 : AocPuzzle<Long, Long>() {
    override fun solve1(input: List<String>): Long {
        val testVals = input.map { it.substringBefore(":").toLong() }
        val operands = input.map { it.substringAfter(":").extractLongNumbers() }

        return testVals.filterIndexed { index, t ->
            t in makeValues(operands[index], listOf(Operand.Plus, Operand.Mul)).toSet()
        }.sum()
    }

    override fun solve2(input: List<String>): Long {
        val testVals = input.map { it.substringBefore(":").toLong() }
        val operands = input.map { it.substringAfter(":").extractLongNumbers() }

        return testVals.filterIndexed { index, t ->
            t in makeValues(operands[index], listOf(Operand.Plus, Operand.Mul, Operand.Concat)).toSet()
        }.sum()
    }

    fun makeValues(nums: List<Long>, ops: List<Operand>) = sequence {
        val opIndices = IntArray(nums.size - 1)
        while (opIndices[0] < ops.size) {
            val applyOps = List(opIndices.size) { ops[opIndices[it]] }
            yield(makeValue(nums, applyOps))

            opIndices[opIndices.lastIndex]++
            for (i in opIndices.lastIndex downTo 1) {
                if (opIndices[i] == ops.size) {
                    opIndices[i] = 0
                    opIndices[i-1]++
                }
            }
        }
    }

    fun makeValue(nums: List<Long>, ops: List<Operand>) =
        nums.subList(1, nums.size).foldIndexed(nums.first()) { i, acc, num -> ops[i].apply(acc, num) }

    enum class Operand {
        Plus {
            override fun apply(a: Long, b: Long) = a + b
        },
        Mul {
            override fun apply(a: Long, b: Long) = a * b
        },
        Concat {
            override fun apply(a: Long, b: Long) = "$a$b".toLong()
        },
        ;

        abstract fun apply(a: Long, b: Long): Long
    }
}
