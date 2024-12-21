package y2024.day21

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i
import kotlin.math.abs
import kotlin.math.min

fun main() = Day21.runAll()

object Day21 : AocPuzzle<Long, Long>() {
    override fun solve1(input: List<String>): Long = Solver().solve(input, 2)
    override fun solve2(input: List<String>): Long = Solver().solve(input, 25)
}

private class Solver {
    val cache: MutableMap<CacheKey, Long> = mutableMapOf()

    fun solve(codes: List<String>, robots: Int): Long {
        return codes.sumOf { code ->
            val codeNum = code.substring(0..2).toInt()
            codeNum * "A$code".zipWithNext().sumOf { (from, to) ->
                recurse(from, to, Keyboard.num, robots)
            }
        }
    }

    fun recurse(from: Char, to: Char, keyboard: Keyboard, depth: Int): Long {
        val key = CacheKey(from, to, depth)
        cache[key]?.let { return it }

        val fromPos = keyboard.getButtonPos(from)
        val toPos = keyboard.getButtonPos(to)
        val moves = toPos - fromPos

        val minLen = if (depth == 0) {
            abs(moves.x) + abs(moves.y) + 1L
        } else {
            val xKeys = (if (moves.x > 0) ">" else "<").repeat(abs(moves.x))
            val yKeys = (if (moves.y > 0) "v" else "^").repeat(abs(moves.y))
            val xLen = "$xKeys$yKeys".computeLen(fromPos, toPos, keyboard, depth)
            val yLen = "$yKeys$xKeys".computeLen(fromPos, toPos, keyboard, depth)
            min(xLen, yLen)
        }
        return minLen.also { cache[key] = it }
    }

    fun String.computeLen(fromPos: Vec2i, toPos: Vec2i, keyboard: Keyboard, depth: Int): Long {
        if (!keyboard.isValid(fromPos, toPos, this)) {
            return Long.MAX_VALUE
        }
        return "A${this}A".zipWithNext().sumOf { (a, b) ->
            recurse(a, b, Keyboard.dir, depth - 1)
        }
    }
}

private data class CacheKey(val from: Char, val to: Char, val remaining: Int)

private class Keyboard(buttons: List<String>) {
    val buttonMap = coordSequence(3, buttons.size).map { buttons[it.y][it.x] to it }.toMap()
    val forbidden = getButtonPos(' ')

    fun getButtonPos(button: Char): Vec2i = buttonMap[button]!!

    fun isValid(from: Vec2i, to: Vec2i, seq: String): Boolean {
        var it = from
        seq.forEach { dir ->
            if (it == forbidden) {
                return false
            }
            when (dir) {
                '<' -> it += Vec2i.NEG_X_AXIS
                '>' -> it += Vec2i.X_AXIS
                '^' -> it += Vec2i.NEG_Y_AXIS
                'v' -> it += Vec2i.Y_AXIS
            }
        }
        check(it == to)
        return true
    }

    companion object {
        val dir = Keyboard(listOf(" ^A", "<v>"))
        val num = Keyboard(listOf("789", "456", "123", " 0A"))
    }
}
