package y2024.day20

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i
import manhattanDistance
import neighbors

fun main() = Day20.runAll()

object Day20 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val saves = if (isTestRun()) 20 else 100
        return countCheats(input, 2, saves)
    }

    override fun solve2(input: List<String>): Int {
        val saves = if (isTestRun()) 72 else 100
        return countCheats(input, 20, saves)
    }

    private fun countCheats(input: List<String>, maxCheatLen: Int, savedPicos: Int): Int {
        val width = input[0].length
        val height = input.size
        val from = coordSequence(width, height).first { input[it] == 'S' }

        val map = input.map { it.replace('S', '.').replace('E', '.') }
        val nodes = path(map, from)

        return nodes.values.sumOf { start ->
            start.pos.neighborhoodCoords(maxCheatLen, width, height)
                .filter { it.manhattanDistance(start.pos) <= maxCheatLen && it in nodes }
                .count { endCoord ->
                    val endDist = nodes[endCoord]!!.distance
                    val save = endDist - start.distance - start.pos.manhattanDistance(endCoord)
                    save >= savedPicos
                }
        }
    }

    private fun path(grid: List<String>, from: Vec2i): Map<Vec2i, PathNode> {
        val startNode = PathNode(from, 0)
        return generateSequence(startNode to startNode) { (pp, p) ->
            val next = p.pos.neighbors().find { it != pp.pos && grid[it] == '.' }
            next?.let { p to PathNode(it, p.distance + 1) }
        }.map { it.second }.associateBy { it.pos }
    }

    data class PathNode(val pos: Vec2i, val distance: Int)

    private fun Vec2i.neighborhoodCoords(maxDist: Int, width: Int, height: Int): Sequence<Vec2i> {
        val fromX = (x - maxDist).coerceAtLeast(1)
        val fromY = (y - maxDist).coerceAtLeast(1)
        val toX = (x + maxDist).coerceAtMost(width - 2)
        val toY = (y + maxDist).coerceAtMost(height - 2)
        return coordSequence(fromX..toX, fromY..toY)
    }

    operator fun List<String>.get(pos: Vec2i): Char? = getOrNull(pos.y)?.getOrNull(pos.x)
}
