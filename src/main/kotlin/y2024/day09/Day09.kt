package y2024.day09

import AocPuzzle

fun main() = Day09.runAll()

object Day09 : AocPuzzle<Long, Long>() {
    override fun solve1(input: List<String>): Long {
        val numbers = input[0].toCharArray().map { it.toString().toInt() }
        val blocks = IntArray(numbers.sum()) { -1 }

        var blockI = 0
        var fileI = 0
        for (i in numbers.indices) {
            val isFile = i % 2 == 0
            if (isFile) {
                for (j in 0 until numbers[i]) {
                    blocks[blockI + j] = fileI
                }
                fileI++
            }
            blockI += numbers[i]
        }

        var freePtr = blocks.nextFree(0)
        var lastFilePtr = blocks.prevFile(blocks.lastIndex)

        while (lastFilePtr > freePtr) {
            blocks[freePtr] = blocks[lastFilePtr]
            blocks[lastFilePtr] = -1
            freePtr = blocks.nextFree(0)
            lastFilePtr = blocks.prevFile(blocks.lastIndex)
        }

        var checksum = 0L
        for (i in blocks.indices) {
            val f = blocks[i]
            if (f > 0) {
                checksum += f * i
            }
        }
        return checksum
    }

    fun IntArray.nextFree(from: Int, freeSize: Int = 1): Int {
        for (i in from .. (size - freeSize)) {
            if ((0 until freeSize).all { this[i + it] == -1 }) {
                return i
            }
        }
        return -1
    }

    fun IntArray.prevFile(from: Int): Int {
        for (i in from downTo 0) {
            if (this[i] != -1) {
                return i
            }
        }
        return -1
    }

    override fun solve2(input: List<String>): Long {
        val numbers = input[0].toCharArray().map { it.toString().toInt() }
        val blocks = IntArray(numbers.sum()) { -1 }
        val fileIndices = mutableListOf<FileInfo>()

        var blockI = 0
        var fileI = 0
        for (i in numbers.indices) {
            val isFile = i % 2 == 0
            if (isFile) {
                fileIndices += FileInfo(blockI, numbers[i], fileI)
                for (j in 0 until numbers[i]) {
                    blocks[blockI + j] = fileI
                }
                fileI++
            }
            blockI += numbers[i]
        }

        for (i in fileIndices.indices.reversed()) {
            val info = fileIndices[i]
            val freePtr = blocks.nextFree(0, info.size)
            if (freePtr > 0 && freePtr < info.blockI) {
                for (j in 0 until info.size) {
                    blocks[freePtr + j] = info.fileI
                    blocks[info.blockI + j] = -1
                }
            }
        }

        var checksum = 0L
        for (i in blocks.indices) {
            val f = blocks[i]
            if (f > 0) {
                checksum += f * i
            }
        }
        return checksum
    }
}

data class FileInfo(val blockI: Int, val size: Int, val fileI: Int)
