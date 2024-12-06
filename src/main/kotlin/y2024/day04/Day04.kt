package y2024.day04

import AocPuzzle
import gridSequence

fun main() = Day04.runAll()

object Day04 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val map = WordMap(input)
        return gridSequence(map.width, map.height).sumOf { map.countXmas1(it.x, it.y) }
    }

    override fun solve2(input: List<String>): Int {
        val map = WordMap(input)
        return gridSequence(map.width, map.height).sumOf { map.countXmas2(it.x, it.y) }
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

    /*
    Benchmark              Mode  Cnt        Score        Error  Units
    Day04Benchmark.part1  thrpt    5     3131,892 ±    238,967  ops/s
    Day04Benchmark.part2  thrpt    5     7509,873 ±   1914,497  ops/s
    Day04Benchmark.test1  thrpt    5  1155511,848 ±  19618,304  ops/s
    Day04Benchmark.test2  thrpt    5  2442548,709 ± 102560,468  ops/s
     */
}
