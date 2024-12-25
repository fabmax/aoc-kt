package y2024.day25

import AocPuzzle
import extractNumbers
import kotlin.math.abs

fun main() = Day25.runAll()

object Day25 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val locksAndKeys = input.chunked(8).map { it.filter { l -> l.isNotBlank() } }
        val locks = locksAndKeys.filter { it[0] == "#####" }.map { Lock(it) }
        val keys = locksAndKeys.filter { it[0] == "....." }.map { Key(it) }
        return keys.sumOf { key -> locks.count { lock -> lock.fits(key) } }
    }

    fun Lock(schematic: List<String>): Lock {
        require(schematic[0] == "#####" && schematic.last() == ".....")
        return Lock(determineHeights(schematic, '.'))
    }

    fun Key(schematic: List<String>): Key {
        require(schematic[0] == "....." && schematic.last() == "#####")
        return Key(determineHeights(schematic, '#'))
    }

    private fun determineHeights(schematic: List<String>, c: Char) = buildList {
        for (i in 0..4) {
            for (h in schematic.indices) {
                if (schematic[h][i] == c) {
                    add(h - 1)
                    break
                }
            }
        }
    }

    fun Lock.fits(key: Key): Boolean = key.heights.zip(heights).all { (kh, lh) -> kh >= lh }

    data class Lock(val heights: List<Int>)

    data class Key(val heights: List<Int>)
}
