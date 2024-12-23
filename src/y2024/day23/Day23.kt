package y2024.day23

import AocPuzzle
import extractNumbers
import kotlin.math.abs

fun main() = Day23.runAll()

object Day23 : AocPuzzle<Int, String>() {
    override fun solve1(input: List<String>): Int {
        val computers = makeComputers(input)
        val setsOfThree = mutableSetOf<List<Computer>>()

        computers.values.forEach { a ->
            a.connections.forEach { b ->
                b.connections
                    .filter { a in it.connections }
                    .forEach { c ->
                        val network = listOf(a, b, c).sortedBy { it.name }
                        if (network.any { it.name.startsWith("t") }) {
                            setsOfThree += network
                        }
                    }
            }
        }
        return setsOfThree.size
    }

    override fun solve2(input: List<String>): String {
        val computers = makeComputers(input)
        val visited = mutableSetOf<Computer>()
        val interconnected = mutableSetOf<List<Computer>>()

        computers.values.forEach { c ->
            if (c !in visited) {
                val largestSet = largestSet(c.connections + c, emptySet()).sortedBy { it.name }
                visited += largestSet
                interconnected += largestSet
            }
        }

        return interconnected.maxBy { it.size }.joinToString(",")
    }

    fun largestSet(candidates: Set<Computer>, best: Set<Computer>): Set<Computer> {
        return when {
            areAllConnected(candidates) -> candidates
            candidates.size < best.size -> best
            else -> {
                var better = best
                for (remove in candidates) {
                    val result = largestSet(candidates - remove, better)
                    if (result.size > better.size) {
                        better = result
                    }
                }
                better
            }
        }
    }

    fun areAllConnected(computers: Set<Computer>): Boolean {
        return computers.all {
            (setOf(it) + it.connections).containsAll(computers)
        }
    }

    fun makeComputers(input: List<String>): Map<String, Computer> = buildMap {
        input.map {
            val (a, b) = it.split("-")
            val ca = getOrPut(a) { Computer(a) }
            val cb = getOrPut(b) { Computer(b) }

            ca.connections += cb
            cb.connections += ca
        }
    }

    class Computer(val name: String) {
        val connections = mutableSetOf<Computer>()
        override fun toString(): String = name
    }
}
