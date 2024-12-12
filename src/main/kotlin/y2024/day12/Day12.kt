package y2024.day12

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i

fun main() = Day12.runAll()

object Day12 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val garden = Garden(input)
        val patches = garden.getPatches()
        return patches.sumOf { it.positions.size * it.calcPerimeter() }
    }

    override fun solve2(input: List<String>): Int {
        val garden = Garden(input)
        val patches = garden.getPatches()
        return patches.sumOf { it.positions.size * it.calcBulkPerimeter() }
    }
}

class Garden(val input: List<String>) {
    val width = input[0].length
    val height = input.size

    fun typeAt(pos: Vec2i): Char? = input.getOrNull(pos.y)?.getOrNull(pos.x)

    fun getPatches(): List<Patch> {
        val visited = mutableSetOf<Vec2i>()
        return buildList {
            coordSequence(width, height).forEach { pos ->
                if (pos !in visited) {
                    val patch = getPatch(pos)
                    visited += patch.positions
                    add(patch)
                }
            }
        }
    }

    fun getPatch(pos: Vec2i): Patch {
        val type = typeAt(pos)!!

        val positions = mutableSetOf<Vec2i>()
        val open = mutableSetOf(pos)
        while (open.isNotEmpty()) {
            val p = open.first()
            open -= p
            positions += p

            DIRS
                .map { p + it }
                .filter { typeAt(it) == type && it !in positions }
                .forEach { open += it }
        }
        return Patch(positions)
    }

    companion object {
        val DIRS = listOf(
            Vec2i(0, 1),
            Vec2i(0, -1),
            Vec2i(1, 0),
            Vec2i(-1, 0),
        )
    }

    class Patch(val positions: Set<Vec2i>) {
        fun calcPerimeter(): Int {
            return positions.sumOf { pos ->
                DIRS.map { pos + it }.count { it !in positions }
            }
        }

        fun calcBulkPerimeter(): Int {
            val borders = positions
                .filter { pos -> DIRS.any { (pos + it) !in positions } }
                .flatMap { pos -> DIRS.map { pos to (it + pos) } }
                .filter { (_, outside) -> outside !in positions }
                .toMutableSet()

            var bulkCount = 0
            while (borders.isNotEmpty()) {
                val p = borders.first()
                val bulk = traversePerimeter(p)
                borders -= bulk
                bulkCount++
            }
            return bulkCount
        }

        fun traversePerimeter(start: Pair<Vec2i, Vec2i>): List<Pair<Vec2i, Vec2i>> {
            val inside = start.first
            val outside = start.second
            val dir = when {
                inside.x != outside.x -> Vec2i.Y_AXIS
                else -> Vec2i.X_AXIS
            }

            var top = start
            while (true) {
                val next = (top.first - dir) to (top.second - dir)
                if (isBorder(next)) {
                    top = next
                } else {
                    break
                }
            }

            return buildList {
                while (isBorder(top)) {
                    add(top)
                    top = (top.first + dir) to (top.second + dir)
                }
            }
        }

        fun isBorder(border: Pair<Vec2i, Vec2i>): Boolean {
            val inside = border.first
            val outside = border.second
            return inside in positions && outside !in positions
        }
    }
}
