package y2024.day15

import AocPuzzle
import coordSequence
import de.fabmax.kool.math.Vec2i

fun main() = Day15.runAll()

object Day15 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val mapInput = input.takeWhile { it.isNotBlank() }

        val warehouse = SmallWarehouse(mapInput)
        warehouse.simulate(input)
        return warehouse.gps()
    }

    override fun solve2(input: List<String>): Int {
        val largeInput = input
            .map { it.replace("#", "##").replace("O", "[]").replace(".", "..").replace("@", "@.") }
            .takeWhile { it.isNotBlank() }

        val warehouse = LargeWarehouse(largeInput)
        warehouse.simulate(input)
        return warehouse.gps()
    }

    fun Warehouse.simulate(input: List<String>) {
        val moves = input.filter { !it.startsWith("#") && it.isNotBlank() }.joinToString(separator = "")
        moves.forEachIndexed { i, it ->
            when (it) {
                '>' -> moveRobot(Vec2i.X_AXIS)
                '<' -> moveRobot(Vec2i.NEG_X_AXIS)
                '^' -> moveRobot(Vec2i.NEG_Y_AXIS)
                'v' -> moveRobot(Vec2i.Y_AXIS)
            }
        }
    }
}

interface Warehouse {
    fun moveRobot(pos: Vec2i)
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

    fun canMoveTo(pos: Vec2i): Boolean {
        return map.getOrNull(pos.y)?.getOrNull(pos.x) == '.'
    }

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

    fun gps(): Int {
        return boxes.sumOf { it.y * 100 + it.x }
    }
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

    fun canMoveTo(pos: Vec2i): Boolean {
        return map.getOrNull(pos.y)?.getOrNull(pos.x) == '.'
    }

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

    fun gps(): Int {
        return boxes.sumOf { it.y * 100 + it.x }
    }
}
