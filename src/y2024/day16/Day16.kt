package y2024.day16

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i
import java.util.*

fun main() = Day16.runAll()

object Day16 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int = solve(input, false).first
    override fun solve2(input: List<String>): Int = solve(input, true).second

    private fun solve(input: List<String>, computePt2: Boolean): Pair<Int, Int> {
        val width = input[0].length
        val height = input.size
        val start = Reindeer(coordSequence(width, height).first { input[it] == 'S' }, EAST)
        val end = coordSequence(width, height).first { input[it] == 'E' }
        val inputClean = input.map { it.replace('E', '.') }
        return findPath(inputClean, start, end, computePt2)
    }

    private fun findPath(input: List<String>, start: Reindeer, end: Vec2i, computePt2: Boolean): Pair<Int, Int> {
        var bestCost = Int.MAX_VALUE
        val costs = mutableMapOf<Reindeer, Int>()
        val open = PriorityQueue<Pair<Int, Reindeer>> { a, b -> a.first.compareTo(b.first) }
        open.add(0 to start)

        while (open.isNotEmpty()) {
            val (cost, rnd) = open.poll()
            val oldCost = costs.getOrPut(rnd) { cost }

            if (rnd.position == end) {
                if (cost < bestCost) {
                    bestCost = cost
                }
                continue
            }

            if (cost <= oldCost && cost < bestCost) {
                val dirStraight = rnd.heading
                val dirLeft = rnd.heading.turnLeft()
                val dirRight = rnd.heading.turnRight()

                if (input[rnd.position + dirStraight] == '.') {
                    open.add((cost + 1) to rnd.copy(position = rnd.position + dirStraight))
                }
                if (input[rnd.position + dirLeft] == '.') {
                    open.add((cost + 1001) to rnd.copy(position = rnd.position + dirLeft, heading = dirLeft))
                }
                if (input[rnd.position + dirRight] == '.') {
                    open.add((cost + 1001) to rnd.copy(position = rnd.position + dirRight, heading = dirRight))
                }
            }
        }

        val endRnd = DIRS.map { Reindeer(end, it) }.filter { it in costs }.minBy { costs[it]!! }
        val bests = if (computePt2) collectBestPaths(start, endRnd, costs).map { it.position }.toSet() else emptySet()

        return bestCost to bests.size
    }

    private fun collectBestPaths(start: Reindeer, end: Reindeer, costs: Map<Reindeer, Int>): Set<Reindeer> {
        val bests = mutableSetOf(start)
        var pos = listOf(end)

        while (pos.any { it != start }) {
            pos = buildList {
                pos.forEach { toPos ->
                    val toCost = costs[toPos]!!
                    bests.add(toPos)
                    DIRS
                        .flatMap { dir -> DIRS.map { Reindeer(toPos.position + dir, it) } }
                        .filter { from -> isValidMove(from, toPos, costs[from], toCost) }
                        .forEach { add(it) }
                }
            }
        }

        return bests
    }

    private fun isValidMove(from: Reindeer, to: Reindeer, fromCost: Int?, toCost: Int): Boolean {
        if (fromCost == null) {
            return false
        }

        val deltaCost = toCost - fromCost
        val validStraight = from.position + from.heading == to.position && deltaCost == 1
        val validLeft = from.position + from.heading.turnLeft() == to.position && deltaCost == 1001
        val validRight = from.position + from.heading.turnRight() == to.position && deltaCost == 1001
        return validStraight || validLeft || validRight
    }

    private operator fun List<String>.get(pos: Vec2i): Char? = getOrNull(pos.y)?.getOrNull(pos.x)

    val EAST = Vec2i(1, 0)
    val SOUTH = Vec2i(0, 1)
    val WEST = Vec2i(-1, 0)
    val NORTH = Vec2i(0, -1)

    val DIRS = listOf(EAST, SOUTH, WEST, NORTH)

    private fun Vec2i.turnRight(): Vec2i = when (this) {
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
        NORTH -> EAST
        else -> error("invalid heading")
    }

    private fun Vec2i.turnLeft(): Vec2i = when (this) {
        EAST -> NORTH
        SOUTH -> EAST
        WEST -> SOUTH
        NORTH -> WEST
        else -> error("invalid heading")
    }
}

data class Reindeer(val position: Vec2i, val heading: Vec2i)
