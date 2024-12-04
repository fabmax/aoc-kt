@file:Suppress("unused")

package y2024.benchmarks

import AocPuzzle
import kotlinx.benchmark.*
import y2024.day04.Day04

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class Day04Benchmark {

    val target = Day04

    @Benchmark
    fun test1(): Int {
        target.prepareRun(AocPuzzle.Run.TestRun(0))
        return target.solve1(target.input)
    }

    @Benchmark
    fun test2(): Int {
        target.prepareRun(AocPuzzle.Run.TestRun(0))
        return target.solve2(target.input)
    }

    @Benchmark
    fun part1(): Int {
        target.prepareRun(AocPuzzle.Run.PuzzleRun)
        return target.solve1(target.input)
    }

    @Benchmark
    fun part2(): Int {
        target.prepareRun(AocPuzzle.Run.PuzzleRun)
        return target.solve2(target.input)
    }
}