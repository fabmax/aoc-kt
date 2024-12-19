package y2024.day19

import AocPuzzle

fun main() = Day19.runAll()

object Day19 : AocPuzzle<Int, Long>() {
    override fun solve1(input: List<String>): Int {
        val towels = input[0].split(", ")
        val memo = mutableMapOf("" to 1L)
        return input.drop(2).count { memo.countCombos(it, towels) > 0 }
    }

    override fun solve2(input: List<String>): Long {
        val towels = input[0].split(", ")
        val memo = mutableMapOf("" to 1L)
        return input.drop(2).sumOf { memo.countCombos(it, towels) }
    }

    private fun MutableMap<String, Long>.countCombos(design: String, towels: List<String>): Long = getOrPut(design) {
        towels.filter { design.startsWith(it) }.sumOf { countCombos(design.removePrefix(it), towels) }
    }
}
