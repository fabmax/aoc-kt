@file:Suppress("unused")

package y2024.benchmarks

import AocPuzzle
import kotlinx.benchmark.*
import y2024.day07.Day07

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class Day07Benchmark {

    val target = Day07

    @Benchmark
    fun test1(): Long {
        target.prepareRun(AocPuzzle.Run.TestRun(0))
        return target.solve1(target.input)
    }

    @Benchmark
    fun test2(): Long {
        target.prepareRun(AocPuzzle.Run.TestRun(0))
        return target.solve2(target.input)
    }

    @Benchmark
    fun part1(): Long {
        target.prepareRun(AocPuzzle.Run.PuzzleRun)
        return target.solve1(target.input)
    }

    @Benchmark
    fun part2(): Long {
        target.prepareRun(AocPuzzle.Run.PuzzleRun)
        return target.solve2(target.input)
    }
}