package y2024.day10

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i

fun main() = Day10.runAll()

object Day10 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val map = HikingMap(input)
        return map.heads.sumOf { map.collectPathNodes(it).count { it.height == 9 } }
    }

    override fun solve2(input: List<String>): Int {
        val map = HikingMap(input)
        return map.heads.sumOf { map.countPaths(it) }
    }
}

class HikingMap(input: List<String>) {

    val width = input[0].length
    val height = input.size

    val nodes = buildMap {
        coordSequence(width, height).forEach { pos ->
            put(pos, Node(pos, input[pos.y][pos.x].code - '0'.code))
        }
    }

    val heads = nodes.values.filter { it.height == 0 }

    fun collectPathNodes(from: Node): Set<Node> {
        fun recurse(from: Node, result: MutableSet<Node>) {
            DIRS.mapNotNull { nodes[from.pos + it] }
                .filter { it.height - from.height == 1 }
                .filter { result.add(it) }
                .forEach { recurse(it, result) }
        }

        val pathNodes = mutableSetOf<Node>()
        recurse(from, pathNodes)
        return pathNodes
    }

    fun countPaths(from: Node): Int {
        val nexts = DIRS.mapNotNull { nodes[from.pos + it] }.filter { it.height - from.height == 1 }
        return when {
            nexts.isNotEmpty() -> nexts.sumOf { countPaths(it) }
            from.height == 9 -> 1
            else -> 0
        }
    }

    data class Node(val pos: Vec2i, val height: Int)

    companion object {
        val LEFT = Vec2i(-1, 0)
        val RIGHT = Vec2i(1, 0)
        val UP = Vec2i(0, -1)
        val DOWN = Vec2i(0, 1)
        val DIRS = listOf(LEFT, RIGHT, UP, DOWN)
    }

    /*
    Benchmark               Mode  Cnt  Score    Error  Units
    DefaultBenchmark.part1  avgt    5  0,818 ±  0,045  ms/op
    DefaultBenchmark.part2  avgt    5  0,712 ±  0,026  ms/op
    DefaultBenchmark.test1  avgt    5  0,018 ±  0,003  ms/op
    DefaultBenchmark.test2  avgt    5  0,019 ±  0,001  ms/op
     */
}
