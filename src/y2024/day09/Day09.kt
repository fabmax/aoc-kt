package y2024.day09

import AocPuzzle

fun main() = Day09.runAll()

data class FileInfo(val blockI: Int, val size: Int, val fileName: Int)

object Day09 : AocPuzzle<Long, Long>() {

    override fun solve1(input: List<String>): Long {
        val chunkSizes = input[0].toCharArray().map { it.toString().toInt() }
        val memory = IntArray(chunkSizes.sum()) { -1 }

        var blockI = 0
        chunkSizes.forEachIndexed { chunkI, size ->
            if (chunkI % 2 == 0) {
                repeat(size) { memory[blockI + it] = chunkI / 2 }
            }
            blockI += size
        }

        var freePtr = memory.firstFree(0)
        var lastFilePtr = memory.lastFile(memory.lastIndex)
        while (lastFilePtr > freePtr) {
            memory[freePtr] = memory[lastFilePtr]
            memory[lastFilePtr--] = -1
            freePtr = memory.firstFree(freePtr)
        }

        return memory.checksum()
    }

    override fun solve2(input: List<String>): Long {
        val chunkSizes = input[0].toCharArray().map { it.toString().toInt() }
        val memory = IntArray(chunkSizes.sum()) { -1 }
        val fileInfos = mutableListOf<FileInfo>()

        var blockI = 0
        chunkSizes.forEachIndexed { chunkI, size ->
            if (chunkI % 2 == 0) {
                fileInfos += FileInfo(blockI, size, 0)
                repeat(size) { memory[blockI + it] = chunkI / 2 }
            }
            blockI += size
        }

        fileInfos.reversed().forEach { fileInfo ->
            val freePtr = memory.firstFree(0, fileInfo.size)
            if (freePtr > 0 && freePtr < fileInfo.blockI) {
                repeat(fileInfo.size) { j ->
                    memory[freePtr + j] = memory[fileInfo.blockI + j]
                    memory[fileInfo.blockI + j] = -1
                }
            }
        }

        return memory.checksum()
    }

    fun IntArray.firstFree(from: Int, freeSize: Int = 1): Int {
        for (i in from .. (size - freeSize)) {
            if ((0 until freeSize).all { this[i + it] == -1 }) {
                return i
            }
        }
        return -1
    }

    fun IntArray.lastFile(from: Int): Int {
        for (i in from downTo 0) {
            if (this[i] != -1) {
                return i
            }
        }
        return -1
    }

    fun IntArray.checksum(): Long {
        var checksum = 0L
        for (i in indices) {
            val f = this[i]
            if (f > 0) {
                checksum += f * i
            }
        }
        return checksum
    }
}
