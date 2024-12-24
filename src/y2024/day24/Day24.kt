package y2024.day24

import AocPuzzle
import extractNumbers
import y2023.day20.Output
import kotlin.math.abs

fun main() = Day24.runAll()

object Day24 : AocPuzzle<Long, String>() {
    override fun solve1(input: List<String>): Long {
        val circuit = Circuit(input)
        circuit.settle()
        return circuit.output
    }

    override fun solve2(input: List<String>): String {
        val circuit = Circuit(input)
        val expected = if (isTestRun()) circuit.inputX and circuit.inputY else circuit.inputX + circuit.inputY

        circuit.settle()

        val actual = circuit.output
        println("expected: 0%s (%x)".format(((1L shl 47) + expected).toString(2).chunked(8).joinToString(" ").removePrefix("1"), expected))
        println("actual:   0%s (%x)".format(((1L shl 47) + actual).toString(2).chunked(8).joinToString(" ").removePrefix("1"), actual))

        //expected: 0011 0000 0001 0100 1101 0111 0001 1010 0000 0110 1110 0110 (3014d71a06e6)
        //actual:   0010 1111 1111 0100 1101 0110 1010 0010 0000 0110 1110 0110 (2ff4d6a206e6)

        for (i in circuit.outWires.indices) {
            val mask = 1L shl i
            val bitAct = (actual and mask) != 0L
            val bitExp = (expected and mask) != 0L
            if (bitAct != bitExp) {
                println("Wrong bit $i: expected: $bitExp, actual: $bitAct")
            }
        }
        return ""
    }
}

class Wire(var value: Boolean) {
    override fun toString(): String = if (value) "1" else "0"
}

class Circuit(description: List<String>) {
    val wires: Map<String, Wire> = buildMap {
        val wireNames = "[a-z]+[0-9]*".toRegex()
        description
            .flatMap { line -> wireNames.findAll(line).map { line to it.value } }
            .forEach { (line, wireName) ->
                val value = if (':' in line) line.substringAfter(": ") == "1" else false
                putIfAbsent(wireName, Wire(value))
            }
    }

    val gates: Map<String, Gate> = buildMap {
        description.filter { "->" in it }.forEach { gateDesc ->
            val name = gateDesc.substringAfter("-> ")
            val output = wires[name]!!
            val inputs = gateDesc.substringBefore(" ->").split(' ')
            val gate = when (inputs[1]) {
                "AND" -> And(name, output, inputs[0], inputs[2])
                "OR" -> Or(name, output, inputs[0], inputs[2])
                "XOR" -> Xor(name, output, inputs[0], inputs[2])
                else -> error(inputs[1])
            }
            put(name, gate)
        }
    }

    val inWiresX = wires.keys.filter { it.startsWith("x") }.sorted().map { wires[it]!! }
    val inWiresY = wires.keys.filter { it.startsWith("y") }.sorted().map { wires[it]!! }
    val outWires = wires.keys.filter { it.startsWith("z") }.sorted().map { wires[it]!! }

    val inputX = computeNumber(inWiresX)
    val inputY = computeNumber(inWiresY)
    val output: Long get() = computeNumber(outWires)

    fun settle() {
        do {
            val settled = gates.values.fold(true) { acc, gate -> gate.settle() && acc }
        } while (!settled)
    }

    private fun computeNumber(wires: List<Wire>): Long =
        wires.foldRight(0L) { wire, acc -> (acc shl 1) + if (wire.value) 1 else 0 }

    abstract inner class Gate(val name: String, val output: Wire, var inNameA: String, var inNameB: String) {
        val inA: Wire get() = wires[inNameA]!!
        val inB: Wire get() = wires[inNameB]!!

        abstract fun update()

        fun settle(): Boolean {
            val before = output.value
            update()
            return before == output.value
        }
    }

    inner class And(name: String, output: Wire, inA: String, inB: String) : Gate(name, output, inA, inB) {
        override fun update() {
            output.value = inA.value && inB.value
        }
    }

    inner class Or(name: String, output: Wire, inA: String, inB: String) : Gate(name, output, inA, inB) {
        override fun update() {
            output.value = inA.value || inB.value
        }
    }

    inner class Xor(name: String, output: Wire, inA: String, inB: String) : Gate(name, output, inA, inB) {
        override fun update() {
            output.value = inA.value != inB.value
        }
    }
}
