package y2024.day20

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i
import extractNumbers
import manhattanDistance
import neighbors
import kotlin.math.abs
import kotlin.math.max

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
        val to = coordSequence(width, height).first { input[it] == 'E' }

        val map = input.map { it.replace('S', '.').replace('E', '.') }
        val nodes = dijkstra(map, from, to)

        var cnt = 0
        coordSequence(width, height).filter { it in nodes }.forEach { startCoord ->
            val startDist = nodes[startCoord]!!.distance
            startCoord.neighborhoodCoords(maxCheatLen, width, height)
                .filter { it.manhattanDistance(startCoord) <= maxCheatLen && it in nodes }
                .forEach { endCoord ->
                    val endDist = nodes[endCoord]!!.distance
                    val save = endDist - startDist - startCoord.manhattanDistance(endCoord)
                    if (save >= savedPicos) {
                        cnt++
                    }
                }
        }
        return cnt
    }

    private fun dijkstra(grid: List<String>, from: Vec2i, to: Vec2i): Map<Vec2i, PathNode> {
        val open = mutableSetOf(PathNode(from, 0))
        val visited = mutableMapOf<Vec2i, PathNode>()

        while (open.isNotEmpty()) {
            val curr = open.first().also { open.remove(it) }
            if (curr.distance >= (visited[curr.pos]?.distance ?: Int.MAX_VALUE)) {
                continue
            }
            visited[curr.pos] = curr

            curr.pos.neighbors()
                .filter { grid[it] == '.' }
                .map { PathNode(it, curr.distance + 1) }
                .filter { it.distance < (visited[it.pos]?.distance ?: Int.MAX_VALUE) }
                .forEach { open += it }
        }

        return visited
    }

    data class PathNode(val pos: Vec2i, val distance: Int)

    fun Vec2i.neighborhoodCoords(maxDist: Int, width: Int, height: Int): Sequence<Vec2i> {
        val fromX = (x - maxDist).coerceAtLeast(1)
        val fromY = (y - maxDist).coerceAtLeast(1)
        val toX = (x + maxDist).coerceAtMost(width - 2)
        val toY = (y + maxDist).coerceAtMost(height - 2)
        return coordSequence(fromX..toX, fromY..toY)
    }

    operator fun List<String>.get(pos: Vec2i): Char? = getOrNull(pos.y)?.getOrNull(pos.x)
}
