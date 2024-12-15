package y2024.day05

import AocPuzzle

fun main() = Day05.runAll()

private typealias Rule = Pair<Int, Int>

object Day05 : AocPuzzle<Int, Int>() {

    override fun solve1(input: List<String>): Int {
        val rules = input
            .takeWhile { it.isNotBlank() }
            .map { it.substringBefore('|').toInt() to it.substringAfter('|').toInt() }
        val updates = input
            .filter { it.isNotBlank() && "|" !in it }
            .map { str -> str.split(",").map { it.toInt() } }

        return updates
            .filter { update -> rules.all { it.isValid(update) } }
            .sumOf { it[it.size / 2] }
    }

    override fun solve2(input: List<String>): Int {
        val rules = input
            .takeWhile { it.isNotBlank() }
            .map { it.substringBefore('|').toInt() to it.substringAfter('|').toInt() }
        val incorrectUpdates = input
            .filter { it.isNotBlank() && "|" !in it }
            .map { str -> str.split(",").map { it.toInt() } }
            .filterNot { update -> rules.all { it.isValid(update) } }
            .map { it.toMutableList() }

        incorrectUpdates.forEach { update ->
            do {
                val corrections = rules.count { it.apply(update) }
            } while (corrections > 0)
        }

        return incorrectUpdates
            .filter { update -> rules.all { it.isValid(update) } }
            .sumOf { it[it.size / 2] }
    }

    private fun Rule.isValid(updates: List<Int>): Boolean {
        val indexA = updates.indexOf(first)
        val indexB = updates.indexOf(second)
        return when {
            indexA < 0 || indexB < 0 -> true
            else -> indexA < indexB
        }
    }

    private fun Rule.apply(updates: MutableList<Int>): Boolean {
        if (!isValid(updates)) {
            val indexA = updates.indexOf(first)
            val indexB = updates.indexOf(second)
            updates[indexA] = updates[indexB].also { updates[indexB] = updates[indexA] }
            return true
        }
        return false
    }
}
