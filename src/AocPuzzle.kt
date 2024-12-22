import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MdColor

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
    
    fun runAll(alsoBenchmarks: Boolean = false) {
        if (alsoBenchmarks) {
            runBenchmark()
        }
        runTests()
        println()
        runPuzzle()
    }

    fun runPuzzle() {
        printColored("Day $day Puzzle:\n", MdColor.LIGHT_BLUE, bold = true)

        prepareRun(Run.PuzzleRun)
        runParts(part1 = true, part2 = true)
    }

    fun runTests(vararg tests: Int) {
        printColored("Day $day Tests:\n", MdColor.LIGHT_BLUE, bold = true)

        inputData.testInputs.forEachIndexed { i, test ->
            if (tests.isEmpty() || (i+1) in tests) {
                printColored(" [Test ${i + 1}]\n", MdColor.AMBER)
                prepareRun(Run.TestRun(i))

                val isTestPart1 = test.test1 != null
                val isTestPart2 = test.test2 != null
                runParts(isTestPart1, isTestPart2)
            }
        }
    }

    fun runBenchmark(warmupSecs: Int = 3, benchmarkSecs: Int = 5) {
        prepareRun(Run.PuzzleRun)

        printColored("Warming up part 1...\n", MdColor.DEEP_ORANGE)
        repeat(warmupSecs) { runTimed { solve1(input) } }
        printColored("Benchmarking part 1...\n", MdColor.LIGHT_BLUE tone 300)
        val result1 = (1..benchmarkSecs).map { runTimed { solve1(input) } }.sortedBy { it.second }

        printColored("Warming up part 2...\n", MdColor.DEEP_ORANGE)
        repeat(warmupSecs) { runTimed { solve2(input) } }
        printColored("Benchmarking part 2...\n", MdColor.LIGHT_BLUE tone 300)
        val result2 = (1..benchmarkSecs).map { runTimed { solve2(input) } }.sortedBy { it.second }

        val iterations1 = result1.sumOf { it.first }
        val iterations2 = result2.sumOf { it.first }
        val median1 = result1[benchmarkSecs / 2].second
        val median2 = result2[benchmarkSecs / 2].second

        println("----------------------------------------------------------------")
        printColored("Part 1 median: ", MdColor.LIME)
        printColored("%10.3f ms  ".format(median1), MdColor.LIGHT_BLUE, bold = true)
        printColored("($iterations1 benchmark iterations)\n", MdColor.GREY)

        printColored("Part 2 median: ", MdColor.LIME)
        printColored("%10.3f ms  ".format(median2), MdColor.LIGHT_BLUE, bold = true)
        printColored("($iterations2 benchmark iterations)\n", MdColor.GREY)
        println("----------------------------------------------------------------\n")
    }

    private inline fun runTimed(durationNanos: Long = 1_000_000_000L, block: () -> Any): Pair<Int, Double> {
        val start = System.nanoTime()
        var t = System.nanoTime()
        var count = 0
        var sink = 0
        while (t - start < durationNanos) {
            sink += block().hashCode()
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
            val elap = (System.nanoTime() - t) / 1e6
            val (pre, post) = deco(answer, expected)
            val time = coloredString("%9.3f ms".format(elap), MdColor.PURPLE tone 300, bold = true)
            val answerFmt = coloredString("%-20s".format(answer), Color.WHITE, bold = true)
            println("  %s Part %d: %s %s %s".format(pre, part, answerFmt, time, post))
        } catch (e: PartNotImplementedException) {
            printColored("  Part ${e.part} not yet implemented", MdColor.CYAN)
        }
    }

    private fun deco(answer: Any?, expected: String?): Pair<String, String> {
        return when(expected) {
            null -> coloredString("[??]", MdColor.CYAN, bold = true) to ""
            answer.toString() -> coloredString("[OK]", MdColor.LIGHT_GREEN, bold = true) to ""
            else -> coloredString("[NO]", MdColor.RED, bold = true) to coloredString("(expected: $expected)", MdColor.RED)
        }
    }

    sealed interface Run {
        data class TestRun(val testIdx: Int) : Run
        data object PuzzleRun : Run
    }
    
    class PartNotImplementedException(val part: Int) : IllegalStateException()
}