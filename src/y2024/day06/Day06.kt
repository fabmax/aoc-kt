package y2024.day06

import AocPuzzle
import de.fabmax.kool.math.Vec2i
import gridSequence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() = Day06.runAll()

object Day06 : AocPuzzle<Int, Int>() {
    override fun solve1(input: List<String>): Int {
        val map = GuardMap(input)
        do {
            val moveResult = map.move()
        } while (moveResult == MoveResult.CONTINUE)
        return map.passedFields.size
    }

    override fun solve2(input: List<String>): Int {
        val map = GuardMap(input)
        val startPos = map.guardPos
        val startDir = map.guardDir
        do {
            val moveResult = map.move()
        } while (moveResult == MoveResult.CONTINUE)

        return runBlocking(Dispatchers.Default) {
            gridSequence(map.width, map.height)
                .filter { obstaclePos -> obstaclePos != startPos && obstaclePos in map.passedFields }
                .map { obstaclePos ->
                    async {
                        val obstacleMap = GuardMap(map.map, startPos, startDir)
                        obstacleMap.addObstacle(obstaclePos)
                        do {
                            val moveResult = obstacleMap.move()
                            if (moveResult == MoveResult.LOOP) {
                                return@async 1
                            }
                        } while (moveResult == MoveResult.CONTINUE)
                        0
                    }
                }
                .toList().awaitAll().sum()
        }
    }
}

private fun GuardMap(input: List<String>): GuardMap {
    val guardRegex = """[\^v<>]+""".toRegex()

    val map = input.map { it.replace(guardRegex, ".") }
    val guardPos = Vec2i(
        guardRegex.find(input.first { guardRegex.containsMatchIn(it) })!!.range.first,
        input.indexOfFirst { guardRegex.containsMatchIn(it) }
    )
    val guardDir: Vec2i = when (input[guardPos.y][guardPos.x]) {
        '^' -> Vec2i.NEG_Y_AXIS
        'v' -> Vec2i.Y_AXIS
        '>' -> Vec2i.X_AXIS
        '<' -> Vec2i.NEG_X_AXIS
        else -> error("Invalid guard marker")
    }

    return GuardMap(map, guardPos, guardDir)
}

private class GuardMap(
    var map: List<String>,
    var guardPos: Vec2i,
    var guardDir: Vec2i,
) {
    val width = map[0].length
    val height = map.size
    val passedFields = mutableMapOf(guardPos to mutableSetOf(guardDir))

    fun fieldAt(pos: Vec2i): Char? = map.getOrNull(pos.y)?.getOrNull(pos.x)

    fun addObstacle(pos: Vec2i) {
        val newMap = map.toMutableList()
        val line = newMap[pos.y].toCharArray()
        line[pos.x] = 'O'
        newMap[pos.y] = line.concatToString()
        map = newMap
    }

    fun move(): MoveResult {
        val next = fieldAt(guardPos + guardDir) ?: return MoveResult.LEFT_MAP
        if (next == '.') {
            guardPos += guardDir
        } else {
            guardDir = guardDir.turnRight()
        }
        if (fieldAt(guardPos) != null) {
            val directionsAtField = passedFields.getOrPut(guardPos) { mutableSetOf() }
            return if (directionsAtField.add(guardDir)) MoveResult.CONTINUE else MoveResult.LOOP
        }
        return MoveResult.LEFT_MAP
    }

    private fun Vec2i.turnRight(): Vec2i {
        return when (this) {
            Vec2i.X_AXIS -> Vec2i.Y_AXIS
            Vec2i.Y_AXIS -> Vec2i.NEG_X_AXIS
            Vec2i.NEG_X_AXIS -> Vec2i.NEG_Y_AXIS
            Vec2i.NEG_Y_AXIS -> Vec2i.X_AXIS
            else -> error("Invalid direction")
        }
    }
}

private enum class MoveResult {
    CONTINUE,
    LEFT_MAP,
    LOOP
}
