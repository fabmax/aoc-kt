abstract class AocPuzzle<A: Any, B: Any> {

    val year = Regex("""\Ay(\d+)""").find(this::class.qualifiedName!!)?.groups?.get(1)!!.value.toInt()
    val day = Regex("""\d+""").find(this::class.simpleName!!)!!.value.toInt()

    var run: Run = Run.TestRun(0)
        private set

    val inputData = InputData(year, day)

    val input: List<String>
        get() = when(val r = run) {
            is Run.TestRun -> inputData.testInputs[r.testIdx].testInput
            is Run.PuzzleRun -> inputData.puzzleInput
        }
    val rawInput: String
        get() = when(val r = run) {
            is Run.TestRun -> inputData.testInputs[r.testIdx].testInputRaw
            is Run.PuzzleRun -> inputData.puzzleInputRaw
        }

    val expected1: String?
        get() = when(val r = run) {
            is Run.TestRun -> inputData.testInputs[r.testIdx].test1
            is Run.PuzzleRun -> inputData.answerPart1
        }

    val expected2: String?
        get() = when(val r = run) {
            is Run.TestRun -> inputData.testInputs[r.testIdx].test2
            is Run.PuzzleRun -> inputData.answerPart2
        }

    fun isTestRun(idx: Int = -1): Boolean {
        return when (val r = run) {
            is Run.TestRun -> idx < 0 || idx == r.testIdx
            is Run.PuzzleRun -> false
        }
    }

    open fun prepareRun(run: Run) {
        this.run = run
    }
    
    open fun solve1(input: List<String>): A {
        throw PartNotImplementedException(1)
    }

    open fun solve2(input: List<String>): B {
        throw PartNotImplementedException(2)
    }
    
    fun runAll() {
        runTests()
        println()
        runPuzzle()
    }

    fun runPuzzle() {
        println("Day $day Puzzle:")

        prepareRun(Run.PuzzleRun)
        runParts(part1 = true, part2 = true)
    }

    fun runTests(vararg tests: Int) {
        println("Day $day Tests:")

        inputData.testInputs.forEachIndexed { i, test ->
            if (tests.isEmpty() || (i+1) in tests) {
                println("  [Test ${i + 1}]:")
                prepareRun(Run.TestRun(i))

                val isTestPart1 = test.test1 != null
                val isTestPart2 = test.test2 != null
                runParts(isTestPart1, isTestPart2)
            }
        }
    }

    fun runBenchmark(warmupSecs: Int = 3, benchmarkSecs: Int = 5) {
        prepareRun(Run.PuzzleRun)

        println("Warming up part 1...")
        repeat(warmupSecs) { runTimed { solve1(input) } }
        println("Benchmarking part 1...")
        val result1 = (1..benchmarkSecs).map { runTimed { solve1(input) } }.sortedBy { it.second }

        println("Warming up part 2...")
        repeat(warmupSecs) { runTimed { solve2(input) } }
        println("Benchmarking part 2...")
        val result2 = (1..benchmarkSecs).map { runTimed { solve2(input) } }.sortedBy { it.second }

        println("-----------------------------")
        val iterations1 = result1.sumOf { it.first }
        val iterations2 = result2.sumOf { it.first }
        val median1 = result1[benchmarkSecs / 2].second
        val median2 = result2[benchmarkSecs / 2].second
        System.out.printf("Part 1 median:%10.3f ms  (%d benchmark iterations)\n", median1, iterations1)
        System.out.printf("Part 2 median:%10.3f ms  (%d benchmark iterations)\n", median2, iterations2)
    }

    private inline fun runTimed(durationNanos: Long = 1_000_000_000L, block: () -> Unit): Pair<Int, Double> {
        val start = System.nanoTime()
        var t = System.nanoTime()
        var count = 0
        while (t - start < durationNanos) {
            block()
            count++
            t = System.nanoTime()
        }

        val millisPerOp = (t - start) / (1e6 * count)
        System.out.printf("%10.3f ms\n", millisPerOp, millisPerOp)
        return count to millisPerOp
    }

    private fun runParts(part1: Boolean, part2: Boolean) {
        if (part1) {
            runPart(1, expected1)
        }
        if (part2) {
            runPart(2, expected2)
        }
    }

    private fun runPart(part: Int, expected: String?) {
        try {
            val t = System.nanoTime()
            val answer: Any = if (part == 1) {
                solve1(input)
            } else {
                solve2(input)
            }
            val t1 = (System.nanoTime() - t) / 1e6
            val answerStr = "${prefix(answer, expected)}Answer part $part: $answer"
            println("  %-36s %9.3f ms".format(answerStr, t1))
        } catch (e: PartNotImplementedException) {
            println("  Part ${e.part} not yet implemented")
        }
    }

    private fun prefix(answer: Any?, expected: String?): String {
        return when(expected) {
            null -> coloredString("[??] ", AnsiColor.CYAN)
            answer.toString() -> coloredString("[OK] ", AnsiColor.BRIGHT_GREEN)
            else -> coloredString("[NO] ", AnsiColor.BRIGHT_RED)

            //null -> "❔ "
            //answer.toString() -> "✅ "
            //else -> "❌ "
        }
    }

    sealed interface Run {
        data class TestRun(val testIdx: Int) : Run
        data object PuzzleRun : Run
    }
    
    class PartNotImplementedException(val part: Int) : IllegalStateException()
}