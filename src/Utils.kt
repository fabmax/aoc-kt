@file:Suppress("unused")

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import kotlin.math.abs

fun findPrimeFactors(number: Int, primes: List<Int>): List<Int> {
    return primes.filter { prime -> number % prime == 0 }
}

fun <T> Collection<T>.permutations(): Sequence<List<T>> = sequence {
    // Heap's algorithm (non-recursive variant)
    val elems = toMutableList()
    val cnts = IntArray(elems.size)

    yield(elems.toList())
    var i = 0
    while (i < elems.size) {
        if (cnts[i] < i) {
            val swapIdx = if (i % 2 == 0) 0 else cnts[i]
            elems[swapIdx] = elems[i].also { elems[i] = elems[swapIdx] }
            yield(elems.toList())
            cnts[i]++
            i = 0
        } else {
            cnts[i] = 0
            i++
        }
    }
}

fun <T> List<T>.combinations(places: Int): Sequence<List<T>> = sequence {
    val indices = IntArray(places) { 0 }
    while (indices[0] < size) {
        yield(List(places) { get(indices[it]) })

        indices[indices.lastIndex]++
        for (i in indices.lastIndex downTo 1) {
            if (indices[i] == size) {
                indices[i] = 0
                indices[i-1]++
            }
        }
    }
}

fun coordSequence(width: Int, height: Int) = coordSequence(0 until width, 0 until height)

fun coordSequence(xRange: IntRange, yRange: IntRange) =
    yRange.asSequence().flatMap { y -> xRange.asSequence().map { x -> Vec2i(x, y) } }

inline fun <T> List<T>.splitBy(predicate: (T) -> Boolean): List<List<T>> {
    return (listOf(-1) + indices.filter { predicate(get(it)) } + listOf(size))
        .zipWithNext { from, to -> subList(from + 1, to) }
}

fun List<String>.splitByBlankLines(): List<List<String>> {
    return splitBy { it.isBlank() }
}

fun <T> ArrayDeque<T>.takeAndRemoveWhile(predicate: (T) -> Boolean): List<T> {
    val taken = takeWhile(predicate)
    repeat(taken.size) { removeFirst() }
    return taken
}

val IntRange.size: Int get() = if (isEmpty()) 0 else last - first + 1

fun IntRange.clipLower(min: Int): IntRange {
    return (kotlin.math.max(first, min)..last)
}

fun IntRange.clipUpper(max: Int): IntRange {
    return (first..kotlin.math.min(last, max))
}

fun Vec2i.manhattanDistance(other: Vec2i): Int = abs(x - other.x) + abs(y - other.y)

fun Vec2i.isInLimits(width: Int, height: Int): Boolean {
    return x in 0 until width && y in 0 until height
}

fun Vec2i.neighbors(withDiagonal: Boolean = false): List<Vec2i> {
    return buildList {
        add(Vec2i(x, y-1))
        add(Vec2i(x+1, y))
        add(Vec2i(x, y+1))
        add(Vec2i(x-1, y))
        if (withDiagonal) {
            add(Vec2i(x-1, y-1))
            add(Vec2i(x+1, y-1))
            add(Vec2i(x+1, y+1))
            add(Vec2i(x-1, y+1))
        }
    }
}

fun Vec3i(str: String, delim: Char = ','): Vec3i {
    val (x, y, z) = str.split(delim).filter { it.isNotBlank() }.map { it.trim().toInt() }
    return Vec3i(x, y, z)
}

fun Vec3f(str: String, delim: Char = ','): Vec3f {
    val (x, y, z) = str.split(delim).filter { it.isNotBlank() }.map { it.trim().toFloat() }
    return Vec3f(x, y, z)
}

fun Vec3d(str: String, delim: Char = ','): Vec3d {
    val (x, y, z) = str.split(delim).filter { it.isNotBlank() }.map { it.trim().toDouble() }
    return Vec3d(x, y, z)
}

fun gridSequence(width: Int, height: Int) = sequence {
    for (y in 0 until height) {
        for (x in 0 until width) {
            yield(Vec2i(x, y))
        }
    }
}

fun intersectLines(a1: Vec2d, a2: Vec2d, b1: Vec2d, b2: Vec2d): Vec2d? {
    val denom = (a1.x - a2.x) * (b1.y - b2.y) - (a1.y - a2.y) * (b1.x - b2.x)
    if (denom != 0.0) {
        val a = a1.x * a2.y - a1.y * a2.x
        val b = b1.x * b2.y - b1.y * b2.x
        val x = (a * (b1.x - b2.x) - b * (a1.x - a2.x)) / denom
        val y = (a * (b1.y - b2.y) - b * (a1.y - a2.y)) / denom
        return Vec2d(x, y)
    }
    // lines are parallel
    return null
}

fun findPrimes(upperLimit: Int): List<Int> = (2..upperLimit).filter { it.isPrime }

val Int.isPrime: Boolean get() = (2 .. (this / 2)).none { this % it == 0 }

fun leastCommonMultiple(ints: Collection<Int>): Long {
    val primes = findPrimes(ints.max())
    return ints
        .flatMap { findPrimeFactors(it, primes) }
        .distinct()
        .fold(1L) { prod, value -> prod * value }
}

fun printColored(text: String, fg: Color? = null, bg: Color? = null, bold: Boolean = false, italic: Boolean = false) = print(coloredString(text, fg, bg, bold, italic))

fun coloredString(text: String, fg: Color? = null, bg: Color? = null, bold: Boolean = false, italic: Boolean = false): String {
    val boldCode = if (bold) "1;" else ""
    val italicCode = if (italic) "3;" else ""
    val fgCode = fg?.let { "38;2;${(it.r * 255).toInt()};${(it.g * 255).toInt()};${(it.b * 255).toInt()}" } ?: ""
    val bgCode = bg?.let { "48;2;${(it.r * 255).toInt()};${(it.g * 255).toInt()};${(it.b * 255).toInt()}" } ?: ""
    val sep = if (fgCode.isNotBlank() && bgCode.isNotBlank()) ";" else ""
    return "\u001b[${boldCode}${italicCode}${fgCode}${sep}${bgCode}m$text\u001B[0m"
}
