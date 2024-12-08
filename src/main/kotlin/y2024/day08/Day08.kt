package y2024.day08

import AocPuzzle
import de.fabmax.kool.math.Vec2i
import isInLimits

fun main() = Day08.runAll()

object Day08 : AocPuzzle<Int, Int>() {

    override fun solve1(input: List<String>): Int = solve(input, false)
    override fun solve2(input: List<String>): Int = solve(input, true)

    private fun solve(input: List<String>, multiples: Boolean): Int {
        val width = input[0].length
        val height = input.size
        val antennaPositions = input
            .flatMapIndexed { y, line -> line.mapIndexed { x, char -> char to Vec2i(x, y) } }
            .groupBy { it.first }
            .filterKeys { it != '.' }
            .mapValues { (_, v) -> v.map { it.second } }

        return antennaPositions.values.flatMap { positions ->
            positions.indices
                .flatMap { i -> positions.indices.map { j -> i to j } }
                .filter { it.first != it.second }
                .map { positions[it.first] to positions[it.second] }
                .flatMap { (posA, posB) ->
                    val d = posA - posB
                    if (multiples) {
                        generateSequence(posA) { pos -> pos + d }.takeWhile { it.isInLimits(width, height) }
                    } else {
                        sequenceOf(posA + d)
                    }
                }
        }.filter { it.isInLimits(width, height) }.distinct().count()
    }
}
