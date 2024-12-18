package y2024.day18

import AocPuzzle
import de.fabmax.kool.math.Vec2i
import extractNumbers
import neighbors

fun main() {
    Day18.runAll()
    Day18.runBenchmark()
}

object Day18 : AocPuzzle<Int, String>() {
    override fun solve1(input: List<String>): Int {
        val from = Vec2i(0, 0)
        val to = if (isTestRun()) Vec2i(6, 6) else Vec2i(70, 70)
        val nBytes = if (isTestRun()) 12 else 1024
        val grid = makeGrid(to.x + 1, to.y + 1, input.take(nBytes))
        val dist = dijkstra(grid, from, to)
        return dist ?: 0
    }

    override fun solve2(input: List<String>): String {
        val from = Vec2i(0, 0)
        val to = if (isTestRun()) Vec2i(6, 6) else Vec2i(70, 70)

        var step = input.size / 2
        var pos = step
        var prevHadPath = false
        while (true) {
            val grid = makeGrid(to.x + 1, to.y + 1, input.take(pos))
            val hasPath = dijkstra(grid, from, to) != null

            if (step == 1 && hasPath != prevHadPath) {
                if (!hasPath) pos--
                break
            }
            prevHadPath = hasPath
            step = (step / 2).coerceAtLeast(1)
            pos = if (hasPath) pos + step else pos - step
        }

        return input[pos]
    }

    private fun makeGrid(width: Int, height: Int, input: List<String>): List<String> {
        val bytes = input.map { it.extractNumbers() }.map { (x, y) -> Vec2i(x, y) }.toSet()

        return buildList {
            for (y in 0 until height) {
                add(buildString(width) {
                    for (x in 0 until width) {
                        if (Vec2i(x, y) in bytes) append('#') else append('.')
                    }
                })
            }
        }
    }

    private fun dijkstra(grid: List<String>, from: Vec2i, to: Vec2i): Int? {
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
        return visited[to]?.distance
    }

    data class PathNode(val pos: Vec2i, val distance: Int)

    operator fun List<String>.get(pos: Vec2i): Char? = getOrNull(pos.y)?.getOrNull(pos.x)
}
