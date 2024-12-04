package y2024.day04

import AocPuzzle

fun main() = Day04.runAll()

object Day04 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val map = WordMap(input)
        var cnt = 0
        for (y in 0 until map.height) {
            for (x in 0 until map.width) {
                cnt += map.countXmas1(x, y)
            }
        }
        return cnt
    }

    override fun solve2(input: List<String>): Int {
        val map = WordMap(input)
        var cnt = 0
        for (y in 0 until map.height) {
            for (x in 0 until map.width) {
                cnt += map.countXmas2(x, y)
            }
        }
        return cnt
    }
}

private class WordMap(val lines: List<String>) {
    val width = lines[0].length
    val height = lines.size

    fun charAt(x: Int, y: Int): Char = lines.getOrNull(y)?.getOrNull(x) ?: ' '

    fun countXmas1(x: Int, y: Int): Int {
        if (charAt(x, y) != 'X') return 0

        var cnt = 0
        if (charAt(x+1, y) == 'M' && charAt(x+2, y) == 'A' && charAt(x+3, y) == 'S') cnt++
        if (charAt(x-1, y) == 'M' && charAt(x-2, y) == 'A' && charAt(x-3, y) == 'S') cnt++
        if (charAt(x, y+1) == 'M' && charAt(x, y+2) == 'A' && charAt(x, y+3) == 'S') cnt++
        if (charAt(x, y-1) == 'M' && charAt(x, y-2) == 'A' && charAt(x, y-3) == 'S') cnt++
        if (charAt(x+1, y+1) == 'M' && charAt(x+2, y+2) == 'A' && charAt(x+3, y+3) == 'S') cnt++
        if (charAt(x-1, y-1) == 'M' && charAt(x-2, y-2) == 'A' && charAt(x-3, y-3) == 'S') cnt++
        if (charAt(x+1, y-1) == 'M' && charAt(x+2, y-2) == 'A' && charAt(x+3, y-3) == 'S') cnt++
        if (charAt(x-1, y+1) == 'M' && charAt(x-2, y+2) == 'A' && charAt(x-3, y+3) == 'S') cnt++
        return cnt
    }

    fun countXmas2(x: Int, y: Int): Int {
        if (charAt(x, y) != 'A') return 0

        val tl = charAt(x-1, y-1)
        val tr = charAt(x+1, y-1)
        val bl = charAt(x-1, y+1)
        val br = charAt(x+1, y+1)
        if (!((tl == 'M' && br == 'S') || (tl == 'S' && br == 'M'))) return 0
        if ((bl == 'M' && tr == 'S') || (bl == 'S' && tr == 'M')) return 1
        return 0
    }
}
