package y2024.day03

import AocPuzzle
import extractNumbers

fun main() = Day03.runAll()

object Day03 : AocPuzzle<Int, Int>() {

    override fun solve1(input: List<String>): Int {
        val regex = Regex("""mul\(\d+,\d+\)""")
        return regex.findAll(input.joinToString("\n")).sumOf { match ->
            val (a, b) = match.value.extractNumbers()
            a * b
        }
    }

    override fun solve2(input: List<String>): Int {
        val regex = Regex("""(mul\(\d+,\d+\))|(do\(\))|(don't\(\))""")
        var enabled = true
        var sum = 0
        regex.findAll(input.joinToString("\n")).forEach { match ->
            when (match.value) {
                "do()" -> enabled = true
                "don't()" -> enabled = false
                else if enabled -> {
                    val (a, b) = match.value.extractNumbers()
                    sum += a * b
                }
            }
        }
        return sum
    }
}
