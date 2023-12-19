package day19

import AocPuzzle
import splitByBlankLines
import kotlin.math.max
import kotlin.math.min

fun main() = Day19().start()

typealias Part = List<Int>

class Day19 : AocPuzzle() {

    override fun solve(input: List<String>): Pair<Any?, Any?> {
        val (workflowDefs, partsDefs) = input.splitByBlankLines()

        val workflows = workflowDefs.map { Workflow(it) }.associateBy { it.name } + ("A" to ACCEPT) + ("R" to REJECT)
        val parts = partsDefs.map { partDef ->
            NUMBERS.findAll(partDef).map { it.value.toInt() }.toList()
        }

        return part1(workflows, parts) to part2(workflows)
    }

    fun part2(workflows: Map<String, Workflow>): Long {
        val acceptedRanges = mutableListOf<PartRange>()

        fun Workflow.traverse(inRange: PartRange) {
            inRange.path += this
            rules.forEach {
                val outRange = inRange.copy()
                it.applyToRange(outRange)
                if (outRange.isPossible) {
                    val nextWorkflow = workflows[it.next]!!
                    if (nextWorkflow === ACCEPT) {
                        outRange.path += ACCEPT
                        acceptedRanges += outRange
                    } else if (nextWorkflow !== REJECT) {
                        nextWorkflow.traverse(outRange)
                    }
                }
                it.excludeFromRange(inRange)
            }
            val elseWf = workflows[elseNext]
            if (elseWf === ACCEPT) {
                acceptedRanges += inRange
            } else if (elseWf !== REJECT) {
                elseWf!!.traverse(inRange)
            }
        }

        workflows["in"]!!.traverse(PartRange())
        //println("${acceptedRanges.size} accepted ranges")
        //acceptedRanges.forEach { println("$it  => ${it.path.map { wf -> wf.name }.joinToString(" -> ")}") }
        return acceptedRanges.sumOf { it.getCombinations() }
    }

    fun part1(workflows: Map<String, Workflow>, parts: List<Part>): Int {
        val accepted = parts.filter { part ->
            var wf: Workflow = workflows["in"]!!
            var isAccepted = false
            while (!isAccepted && wf != REJECT) {
                wf = workflows[wf.nextWorkflow(part)]!!
                if (wf == ACCEPT) {
                    isAccepted = true
                }
            }
            isAccepted
        }
        return accepted.sumOf { it.sum() }
    }

    fun Workflow.nextWorkflow(part: Part): String {
        return rules.firstOrNull { it.test(part) }?.next ?: elseNext
    }

    fun Workflow(line: String): Workflow {
        val (name, compares) = line.split("{")
        val elseNext = compares.substringAfterLast(",").removeSuffix("}")
        val rules = compares.substringBeforeLast(",")
            .split(",")
            .map { WorkflowRule(it) }
        return Workflow(name, rules, elseNext)
    }

    fun WorkflowRule(encoded: String): WorkflowRule {
        fun Char.propIndex(): Int = when(this) {
            'x' -> 0
            'm' -> 1
            'a' -> 2
            's' -> 3
            else -> error("unreachable")
        }

        val (compare, next) = encoded.split(":")
        return if ('<' in encoded) {
            val (prop, value) = compare.split("<")
            WorkflowRule(prop[0].propIndex(), '<', value.toInt(), next)
        } else {
            val (prop, value) = compare.split(">")
            WorkflowRule(prop[0].propIndex(), '>', value.toInt(), next)
        }
    }

    data class Workflow(val name: String, val rules: List<WorkflowRule>, val elseNext: String)

    data class WorkflowRule(val prop: Int, val op: Char, val thresh: Int, val next: String) {
        fun test(part: Part): Boolean {
            return if (op == '>') part[prop] > thresh else part[prop] < thresh
        }

        fun applyToRange(range: PartRange) {
            if (op == '>') {
                range.lo[prop] = max(range.lo[prop], thresh)
            } else {
                range.hi[prop] = min(range.hi[prop], thresh-1)
            }
        }

        fun excludeFromRange(range: PartRange) {
            if (op == '<') {
                range.lo[prop] = max(range.lo[prop], thresh-1)
            } else {
                range.hi[prop] = min(range.hi[prop], thresh)
            }
        }
    }

    class PartRange {
        val lo = intArrayOf(0, 0, 0, 0)
        val hi = intArrayOf(4000, 4000, 4000, 4000)

        val path = mutableListOf<Workflow>()

        val isPossible: Boolean
            get() = hi.mapIndexed { i, h -> h > lo[i] }.all { it }

        fun copy(): PartRange {
            val range = PartRange()
            for (i in 0..3) {
                range.lo[i] = lo[i]
                range.hi[i] = hi[i]
            }
            range.path += path
            return range
        }

        fun getCombinations(): Long {
            return (0..3).map { hi[it] - lo[it] }.fold(1L) { acc, it -> acc * it }
        }

        override fun toString(): String = buildString {
            listOf("x", "m", "a", "s").forEachIndexed { i, n ->
                append("%4d < $n < %4d | ".format(lo[i], hi[i]))
            }
        }
    }

    companion object {
        val NUMBERS = Regex("""(\d+)""")
        val ACCEPT = Workflow("A", emptyList(), "")
        val REJECT = Workflow("R", emptyList(), "")
    }
}