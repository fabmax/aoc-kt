package y2024.day16

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i
import printColored
import java.util.PriorityQueue

fun main() = Day16.runBenchmark()

object Day16 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int = solve(input, false).first
    override fun solve2(input: List<String>): Int = solve(input, true).second

    private fun solve(input: List<String>, computePt2: Boolean): Pair<Int, Int> {
        val width = input[0].length
        val height = input.size
        val start = Reindeer(coordSequence(width, height).first { input[it] == 'S' }, EAST)
        val end = coordSequence(width, height).first { input[it] == 'E' }
        return findPath(input, start, end, computePt2)
    }

    private operator fun List<String>.get(pos: Vec2i): Char? = getOrNull(pos.y)?.getOrNull(pos.x)

    private fun findPath(input: List<String>, start: Reindeer, end: Vec2i, computePt2: Boolean): Pair<Int, Int> {
        val validFields = setOf('.', 'E')
        val costs = mutableMapOf<Reindeer, Int>()

        val open = PriorityQueue<Pair<Int, Reindeer>> { a, b -> a.first.compareTo(b.first) }
        open.add(0 to start)
        while (open.isNotEmpty()) {
            val (cost, rnd) = open.poll()
            val oldCost = costs.getOrPut(rnd) { cost }

            if (cost <= oldCost) {
                val dirStraight = rnd.heading
                val dirLeft = rnd.heading.turnLeft()
                val dirRight = rnd.heading.turnRight()

                if (input[rnd.position + dirStraight] in validFields) {
                    open.add((cost + 1) to rnd.copy(position = rnd.position + dirStraight))
                }
                if (input[rnd.position + dirLeft] in validFields) {
                    open.add((cost + 1001) to rnd.copy(position = rnd.position + dirLeft, heading = dirLeft))
                }
                if (input[rnd.position + dirRight] in validFields) {
                    open.add((cost + 1001) to rnd.copy(position = rnd.position + dirRight, heading = dirRight))
                }
            }
        }

        val endRnd = DIRS.map { Reindeer(end, it) }.filter { it in costs }.minBy { costs[it]!! }
        val bests = if (computePt2) collectBestPaths(start, endRnd, costs).map { it.position }.toSet() else emptySet()

        val cost = DIRS.mapNotNull { costs[Reindeer(end, it)] }.minOrNull() ?: -1
        return cost to bests.size
    }

    private fun collectBestPaths(start: Reindeer, end: Reindeer, costs: Map<Reindeer, Int>): Set<Reindeer> {
        val bests = mutableSetOf(start)
        var pos = listOf(end)

        while (pos.any { it != start }) {
            pos = buildList {
                pos.forEach { itPos ->
                    bests.add(itPos)
                    val nextCost = costs[itPos]!!

                    DIRS
                        .flatMap { dir -> DIRS.map { Reindeer(itPos.position + dir, it) } }
                        .filter { prev -> isValidMove(prev, itPos, costs[prev], nextCost) }
                        .forEach { add(it) }
                }
            }
        }

        return bests
    }

    private fun isValidMove(prev: Reindeer, next: Reindeer, prevCost: Int?, nextCost: Int): Boolean {
        if (prevCost == null) {
            return false
        }

        val deltaCost = nextCost - prevCost
        val validStraight = prev.position + prev.heading == next.position && deltaCost == 1
        val validLeft = prev.position + prev.heading.turnLeft() == next.position && deltaCost == 1001
        val validRight = prev.position + prev.heading.turnRight() == next.position && deltaCost == 1001
        return validStraight || validLeft || validRight
    }

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
