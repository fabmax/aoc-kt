package y2024.day15

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i

fun main() = Day15.runAll()

object Day15 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val warehouse = SmallWarehouse(input.takeWhile { it.isNotBlank() })
        return warehouse.simulate(input)
    }

    override fun solve2(input: List<String>): Int {
        val largeInput = input
            .map {
                it.replace("#", "##")
                    .replace("O", "[]")
                    .replace(".", "..")
                    .replace("@", "@.")
            }
            .takeWhile { it.isNotBlank() }

        val warehouse = LargeWarehouse(largeInput)
        return warehouse.simulate(input)
    }

    fun Warehouse.simulate(input: List<String>): Int {
        val moves = input.filter { !it.startsWith("#") && it.isNotBlank() }.joinToString(separator = "")
        moves.forEach {
            when (it) {
                '>' -> moveRobot(Vec2i.X_AXIS)
                '<' -> moveRobot(Vec2i.NEG_X_AXIS)
                '^' -> moveRobot(Vec2i.NEG_Y_AXIS)
                'v' -> moveRobot(Vec2i.Y_AXIS)
            }
        }
        return gps()
    }
}

interface Warehouse {
    fun moveRobot(dir: Vec2i)
    fun gps(): Int
}

class SmallWarehouse(mapInput: List<String>) : Warehouse {
    val width = mapInput[0].length
    val height = mapInput.size

    val map = mapInput.map { it.replace('O', '.').replace('@', '.') }
    var robot: Vec2i = Vec2i.ZERO
    val boxes: MutableSet<Vec2i> = buildSet {
        coordSequence(width, height).forEach { pos ->
            when (mapInput[pos.y][pos.x]) {
                '@' -> robot = pos
                'O' -> add(Vec2i(pos))
            }
        }
    }.toMutableSet()

    fun canMoveTo(pos: Vec2i): Boolean = map.getOrNull(pos.y)?.getOrNull(pos.x) == '.'

    override fun moveRobot(dir: Vec2i) {
        val nextPos = robot + dir

        var checkPos = nextPos
        while (canMoveTo(checkPos) && checkPos in boxes) {
            checkPos += dir
        }
        if (canMoveTo(checkPos)) {
            while (checkPos != nextPos) {
                boxes += checkPos
                checkPos -= dir
                boxes -= checkPos
            }
            robot = nextPos
        }
    }

    override fun gps(): Int = boxes.sumOf { it.y * 100 + it.x }
}

class LargeWarehouse(mapInput: List<String>) : Warehouse {
    val width = mapInput[0].length
    val height = mapInput.size

    val map = mapInput.map { it.replace("[", ".").replace("]", ".").replace('@', '.') }
    var robot: Vec2i = Vec2i.ZERO
    val boxes: MutableSet<Vec2i> = buildSet {
        coordSequence(width, height).forEach { pos ->
            when (mapInput[pos.y][pos.x]) {
                '@' -> robot = pos
                '[' -> add(Vec2i(pos))
            }
        }
    }.toMutableSet()

    fun canMoveTo(pos: Vec2i): Boolean = map.getOrNull(pos.y)?.getOrNull(pos.x) == '.'

    override fun moveRobot(dir: Vec2i) {
        val collPositions = collectPositions(setOf(robot), dir)
        if (collPositions.all { canMoveTo(it + dir) }) {
            val collBoxes = collPositions.filter { it in boxes }.toSet()
            boxes -= collBoxes
            boxes += collBoxes.map { it + dir }
            robot = robot + dir
        }
    }

    fun collectPositions(positions: Set<Vec2i>, dir: Vec2i): Set<Vec2i> {
        val nextPositions = positions.flatMap { boxPositions(it + dir) }.filter { it !in positions }.toSet()
        val allPositions = positions + nextPositions
        return if (nextPositions.isEmpty()) allPositions else allPositions + collectPositions(nextPositions, dir)
    }

    fun boxPositions(pos: Vec2i): Set<Vec2i> {
        return when {
            pos in boxes -> setOf(pos, pos + Vec2i.X_AXIS)
            (pos - Vec2i.X_AXIS) in boxes -> setOf(pos - Vec2i.X_AXIS, pos)
            else -> emptySet()
        }
    }

    override fun gps(): Int = boxes.sumOf { it.y * 100 + it.x }
}
