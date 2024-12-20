package y2024.day20

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.MutableVec2i
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.partition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import manhattanDistance
import neighbors
import kotlin.math.max
import kotlin.math.min

fun main() = Day20Kd.runAll(true)

object Day20Kd : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val maxPicos = if (isTestRun()) 20 else 100
        return countCheats(input, 2, maxPicos)
    }

    override fun solve2(input: List<String>): Int {
        val maxPicos = if (isTestRun()) 72 else 100
        return countCheats(input, 20, maxPicos)
    }

    private fun countCheats(input: List<String>, maxCheatLen: Int, maxPicos: Int): Int {
        val width = input[0].length
        val height = input.size
        val from = coordSequence(width, height).first { input[it] == 'S' }

        val map = input.map { it.replace('S', '.').replace('E', '.') }
        val nodes = path(map, from)
        val kdTree = KdNode(nodes, 0, nodes.size)

        return runBlocking(Dispatchers.Default) {
            nodes.chunked(nodes.size / 64).map { chunk ->
                async {
                    chunk.sumOf { center ->
                        var cnt = 0
                        kdTree.forEachInDistance(center.pos, maxCheatLen) { other ->
                            val save = other.distance - center.distance - center.pos.manhattanDistance(other.pos)
                            if (save >= maxPicos) {
                                cnt++
                            }
                        }
                        cnt
                    }
                }
            }.awaitAll().sum()
        }
    }

    private fun path(grid: List<String>, from: Vec2i): MutableList<PathNode> {
        val startNode = PathNode(from, 0)
        return generateSequence(startNode to startNode) { (pp, p) ->
            val next = p.pos.neighbors().find { it != pp.pos && grid[it] == '.' }
            next?.let { p to PathNode(it, p.distance + 1) }
        }.map { it.second }.toMutableList()
    }

    data class PathNode(val pos: Vec2i, val distance: Int)

    operator fun List<String>.get(pos: Vec2i): Char? = getOrNull(pos.y)?.getOrNull(pos.x)

    class KdNode(parentNodes: MutableList<PathNode>, from: Int, to: Int) {
        val min: Vec2i
        val max: Vec2i
        val nodes: MutableList<PathNode>
        val left: KdNode?
        val right: KdNode?
        val isLeaf: Boolean get() = left == null

        init {
            val mi = MutableVec2i(1000, 1000).also { min = it }
            val ma = MutableVec2i(0, 0).also { max = it }
            parentNodes.forEach {
                mi.x = min(mi.x, it.pos.x)
                mi.y = min(mi.y, it.pos.y)
                ma.x = max(ma.x, it.pos.x)
                ma.y = max(ma.y, it.pos.y)
            }
            val szX = max.x - min.x
            val szY = max.y - min.y
            nodes = parentNodes.subList(from, to)
            if (nodes.size > 32) {
                nodes.partition(nodes.size / 2, if (szX > szY) cmpX else cmpY)
                left = KdNode(nodes, 0, nodes.size / 2)
                right = KdNode(nodes, nodes.size / 2, nodes.size)
            } else {
                left = null
                right = null
            }
        }

        fun forEachInDistance(center: Vec2i, dist: Int, block: (PathNode) -> Unit) {
            if (isLeaf) {
                nodes.forEach {
                    if (it.pos.manhattanDistance(center) <= dist) {
                        block(it)
                    }
                }
            } else {
                left!!.let { if (it.isInDistance(center, dist)) it.forEachInDistance(center, dist, block) }
                right!!.let { if (it.isInDistance(center, dist)) it.forEachInDistance(center, dist, block) }
            }
        }

        fun isInDistance(center: Vec2i, dist: Int): Boolean {
            return when {
                min.x - center.x > dist || min.y - center.y > dist -> false
                center.x - max.x > dist || center.y - max.y > dist -> false
                else -> true
            }
        }

        companion object {
            val cmpX: (PathNode, PathNode) -> Int = { a, b -> a.pos.x.compareTo(b.pos.x) }
            val cmpY: (PathNode, PathNode) -> Int = { a, b -> a.pos.y.compareTo(b.pos.y) }
        }
    }
}
