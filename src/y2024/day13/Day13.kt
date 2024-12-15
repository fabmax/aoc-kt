package y2024.day13

import AocPuzzle
import de.fabmax.kool.math.Vec2i
import extractNumbers
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import kotlin.math.roundToLong

fun main() = Day13.runAll()

object Day13 : AocPuzzle<Long, Long>() {
    override fun solve1(input: List<String>): Long = solve(input, 0L)
    override fun solve2(input: List<String>): Long = solve(input, 10000000000000L)

    private fun solve(input: List<String>, offset: Long): Long {
        return input.chunked(4).map { (a, b, prize) ->
            val mvA = a.extractNumbers()
            val mvB = b.extractNumbers()
            val prz = prize.extractNumbers()
            Machine(Vec2i(mvA[0], mvA[1]), Vec2i(mvB[0], mvB[1]), Vec2i(prz[0], prz[1]))
        }.sumOf { it.computeMoves(offset) }
    }
}

data class Machine(val moveA: Vec2i, val moveB: Vec2i, val prize: Vec2i)

fun Machine.computeMoves(offset: Long): Long {
    val coeffs = Array2DRowRealMatrix(2, 2)
    coeffs.setEntry(0, 0, moveA.x.toDouble())
    coeffs.setEntry(1, 0, moveA.y.toDouble())
    coeffs.setEntry(0, 1, moveB.x.toDouble())
    coeffs.setEntry(1, 1, moveB.y.toDouble())

    val consts = ArrayRealVector(doubleArrayOf((prize.x + offset).toDouble(), (prize.y + offset).toDouble()))
    val solver = LUDecomposition(coeffs).solver
    val result = solver.solve(consts)
    val a = result.getEntry(0).roundToLong()
    val b = result.getEntry(1).roundToLong()

    if (a * moveA.x + b * moveB.x == prize.x + offset && a * moveA.y + b * moveB.y == prize.y + offset) {
        return 3 * a + b
    }
    return 0
}
