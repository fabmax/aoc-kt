package y2024.day24

import AocPuzzle
import de.fabmax.kool.util.MdColor
import extractNumbers
import printColored
import y2023.day20.Output
import kotlin.math.abs
import kotlin.random.Random

fun main() = Day24.runAll()

object Day24 : AocPuzzle<Long, String>() {
    override fun solve1(input: List<String>): Long {
        val circuit = Circuit(input)
        circuit.settle()
        return circuit.output
    }

    override fun solve2(input: List<String>): String {
        val circuit = Circuit(input)

        // handcrafted swaps for my input based on checks below
        // to make this work for a different input, clear the list of swaps
        // and work through the errors detected by the checking code below
        val swaps = listOf(
            "z12" to "qdg",
            "z19" to "vvf",
            "fgn" to "dck",
            "z37" to "nvh"
        )
        swaps.forEach { circuit.applySwap(it) }

        if (swaps.size < 4) {
            // check the following pattern:
            //  bit 0:
            //   x00 XOR y00 -> z00
            //   x00 AND y00 -> finalCarry
            //  all other bits:
            //   x01 XOR y01 -> firstResult
            //   x01 AND y01 -> firstCarry
            //   firstResult XOR previousFinalCarry -> z01
            //   firstResult AND previousFinalCarry -> secondCarry
            //   firstCarry OR secondCarry -> finalCarry

            val bit0 = circuit.findGatesByInput("x00", "y00")
            bit0.checkGate("x00", "XOR", "y00", "z00")
            var prevFinalCarry = bit0.checkGate("x00", "AND", "y00").name

            for (i in 1 .. circuit.inWiresX.lastIndex) {
                println("\nbit $i:")

                val xi = "x%02d".format(i)
                val yi = "y%02d".format(i)
                val zi = "z%02d".format(i)
                val bitI = circuit.findGatesByInput(xi, yi)

                val firstRes = bitI.checkGate(xi, "XOR", yi).name
                val firstCar = bitI.checkGate(xi, "AND", yi).name
                val outJ = circuit.findGatesByInput(firstRes, prevFinalCarry)
                outJ.checkGate(firstRes, "XOR", prevFinalCarry, zi)
                val secondCar = outJ.checkGate(firstRes, "AND", prevFinalCarry).name
                prevFinalCarry = circuit.findGatesByInput(firstCar, secondCar).checkGate(firstCar, "OR ", secondCar).name
            }
        }

        return swaps.flatMap { listOf(it.first, it.second) }.sorted().joinToString(",")
    }

    private fun List<Circuit.Gate>.checkGate(a: String, type: String, b: String, out: String? = null): Circuit.Gate {
        var optionA = "$a $type $b"
        var optionB = "$b $type $a"

        if (out != null) {
            optionA = "$optionA -> $out"
            optionB = "$optionB -> $out"
        }

        val match = find { it.toString().startsWith(optionA) || it.toString().startsWith(optionB) }
        if (match != null) {
            printColored("  $match\n", MdColor.LIGHT_GREEN)
            return match
        } else {
            printColored("missing: $optionA (or $optionB)\n", MdColor.RED)
            forEach { printColored("  $it\n") }
            error("missing: $optionA")
        }
    }

    private fun Circuit.applySwap(swap: Pair<String, String>) {
        val a = gates.remove(swap.first)!!
        val b = gates.remove(swap.second)!!
        a.name = b.name.also { b.name = a.name }
        gates[a.name] = a
        gates[b.name] = b
    }
}

class Wire(val name: String, var value: Boolean) {
    override fun toString(): String = if (value) "1" else "0"
}

class Circuit(description: List<String>) {
    val wires: Map<String, Wire> = buildMap {
        val wireNames = "[a-z]+[0-9]*".toRegex()
        description
            .flatMap { line -> wireNames.findAll(line).map { line to it.value } }
            .forEach { (line, wireName) ->
                val value = if (':' in line) line.substringAfter(": ") == "1" else false
                putIfAbsent(wireName, Wire(wireName, value))
            }
    }

    val gates: MutableMap<String, Gate> = buildMap {
        description.filter { "->" in it }.forEach { gateDesc ->
            val name = gateDesc.substringAfter("-> ")
            val output = wires[name]!!
            val inputs = gateDesc.substringBefore(" ->").split(' ')
            val gate = when (inputs[1]) {
                "AND" -> And(name, wires[inputs[0]]!!, wires[inputs[2]]!!)
                "OR" -> Or(name, wires[inputs[0]]!!, wires[inputs[2]]!!)
                "XOR" -> Xor(name, wires[inputs[0]]!!, wires[inputs[2]]!!)
                else -> error(inputs[1])
            }
            put(name, gate)
        }
    }.toMutableMap()

    val inWiresX = wires.keys.filter { it.startsWith("x") }.sorted().map { wires[it]!! }
    val inWiresY = wires.keys.filter { it.startsWith("y") }.sorted().map { wires[it]!! }
    val outWires = wires.keys.filter { it.startsWith("z") }.sorted().map { wires[it]!! }

    val inputX: Long get() = computeNumber(inWiresX)
    val inputY: Long get() = computeNumber(inWiresY)
    val output: Long get() = computeNumber(outWires)

    private fun computeNumber(wires: List<Wire>): Long =
        wires.foldRight(0L) { wire, acc -> (acc shl 1) + if (wire.value) 1 else 0 }

    fun findGatesByInput(vararg inputs: String): List<Gate> {
        val ins = setOf(*inputs)
        return gates.values.filter { it.inA.name in ins || it.inB.name in ins }
    }

    fun settle(): Boolean {
        var i = 0
        do {
            val settled = gates.values.fold(true) { acc, gate -> gate.settle() && acc }
        } while (!settled && ++i < 20)
        return i < 20
    }

    abstract inner class Gate(var name: String, val inA: Wire, val inB: Wire, val type: String) {
        abstract fun update(): Boolean

        fun settle(): Boolean {
            val output = wires[name]!!
            val before = output.value
            output.value = update()
            return before == output.value
        }

        override fun toString(): String {
            val a = minOf(inA.name, inB.name)
            val b = maxOf(inA.name, inB.name)
            return "$a $type $b -> $name"
        }
    }

    inner class And(name: String, inA: Wire, inB: Wire) : Gate(name, inA, inB, "AND") {
        override fun update() = inA.value && inB.value
    }

    inner class Or(name: String, inA: Wire, inB: Wire) : Gate(name, inA, inB, "OR ") {
        override fun update() = inA.value || inB.value
    }

    inner class Xor(name: String, inA: Wire, inB: Wire) : Gate(name, inA, inB, "XOR") {
        override fun update() = inA.value != inB.value
    }
}
