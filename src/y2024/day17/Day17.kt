package y2024.day17

import AocPuzzle
import extractNumbers

fun main() = Day17.runAll()

object Day17 : AocPuzzle<String, Long>() {
    override fun solve1(input: List<String>): String {
        val a = input[0].extractNumbers()[0].toLong()
        val b = input[1].extractNumbers()[0].toLong()
        val c = input[2].extractNumbers()[0].toLong()
        val program = input[4].extractNumbers()

        val computer = ChronospatialComputer(a, b, c, program)
        return computer.run().joinToString(",")
    }

    override fun solve2(input: List<String>): Long {
        val b = input[1].extractNumbers()[0].toLong()
        val c = input[2].extractNumbers()[0].toLong()
        val program = input[4].extractNumbers()

        fun findA(target: List<Int>): List<Long> {
            val prefixes = if (target.size == 1) listOf(0L) else findA(target.drop(1)).map { it shl 3 }
            return prefixes.flatMap { prefix ->
                (0..7).map { prefix + it }.filter { a -> ChronospatialComputer(a, b, c, program).run() == target }
            }
        }

        return findA(program).min()
    }
}

class ChronospatialComputer(
    var regA: Long,
    var regB: Long,
    var regC: Long,
    val program: List<Int>
) {
    var instructionPtr = 0
    val output = mutableListOf<Int>()

    fun run(): List<Int> {
        var clockCount = 0
        while (clock() && clockCount++ < MAX_CLOCKS);
        return output
    }

    fun clock(): Boolean {
        val code = program[instructionPtr]
        val opValue = program[instructionPtr + 1]

        when (code) {
            adv -> regA = regA shr opValue.combo.toInt()
            bxl -> regB = regB xor opValue.literal.toLong()
            bst -> regB = opValue.combo and 0x7
            jnz -> if (regA != 0L) instructionPtr = opValue.literal - 2
            bxc -> regB = regB xor regC
            out -> output += (opValue.combo and 0x7).toInt()
            bdv -> regB = regA shr opValue.combo.toInt()
            cdv -> regC = regA shr opValue.combo.toInt()
            else -> error("Invalid instruction: $code")
        }

        instructionPtr += 2
        return instructionPtr < program.size
    }

    private val Int.combo: Long get() = when (this) {
        in 0..3 -> toLong()
        4 -> regA
        5 -> regB
        6 -> regC
        else -> error("Invalid combo operand: $this")
    }

    private val Int.literal: Int get() = this

    companion object {
        const val MAX_CLOCKS = 10000

        const val adv = 0
        const val bxl = 1
        const val bst = 2
        const val jnz = 3
        const val bxc = 4
        const val out = 5
        const val bdv = 6
        const val cdv = 7

        val opNames = mapOf(
            adv to "adv",
            bxl to "bxl",
            bst to "bst",
            jnz to "jnz",
            bxc to "bxc",
            out to "out",
            bdv to "bdv",
            cdv to "cdv",
        )
    }
}

