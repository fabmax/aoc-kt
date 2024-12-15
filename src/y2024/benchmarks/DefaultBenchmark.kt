/*@file:Suppress("unused")

package y2024.benchmarks

import AocPuzzle
import kotlinx.benchmark.*
import y2024.day10.Day10

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class DefaultBenchmark {

    val target = Day10

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
}*/