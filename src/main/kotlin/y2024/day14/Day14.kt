package y2024.day14

import AocPuzzle
import de.fabmax.kool.math.Vec2i
import extractNumbers

fun main() = Day14.runAll()

object Day14 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val size = if (isTestRun()) Vec2i(11, 7) else Vec2i(101, 103)
        val bathroom = Bathroom(parseRobots(input), size.x, size.y)
        repeat(100) { bathroom.simulateStep() }
        return bathroom.computeSafetyFactor()
    }

    override fun solve2(input: List<String>): Int {
        val bathroom = Bathroom(parseRobots(input), 101, 103)
        var i = 0
        while (bathroom.countNeighbors() < 100) {
            bathroom.simulateStep()
            i++
        }
        //bathroom.print()
        return i
    }

    fun parseRobots(input: List<String>): List<Robot> {
        return input.map { it.extractNumbers() }.map { (px, py, vx, vy) ->
            Robot(Vec2i(px, py), Vec2i(vx, vy))
        }
    }
}

data class Robot(val pos: Vec2i, val velocity: Vec2i)

class Bathroom(var robots: List<Robot>, val width: Int, val height: Int) {

    fun simulateStep() {
        robots = robots.map { it.nextPosition() }
    }

    fun Robot.nextPosition(): Robot {
        val x = (pos.x + velocity.x + width) % width
        val y = (pos.y + velocity.y + height) % height
        return copy(pos = Vec2i(x, y))
    }

    fun computeSafetyFactor(): Int {
        val xh = width / 2
        val yh = height / 2

        val q1 = robots.count { it.pos.x < xh && it.pos.y < yh }
        val q2 = robots.count { it.pos.x > xh && it.pos.y < yh }
        val q3 = robots.count { it.pos.x < xh && it.pos.y > yh }
        val q4 = robots.count { it.pos.x > xh && it.pos.y > yh }

        return q1 * q2 * q3 * q4
    }

    fun countNeighbors(): Int {
        val positions = robots.map { it.pos }.toSet()
        return positions.count { it + Vec2i(1, 0) in positions }
    }

    fun print() {
        println("\n\n\n")
        val positions = robots.map { it.pos }.toSet()
        for (y in 0 until height) {
            for (x in 0 until width) {
                if (Vec2i(x, y) in positions) print("#") else print(".")
            }
            println()
        }
    }
}
