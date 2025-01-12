package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import com.tool.math.lcm
import tool.mylambdas.substringBetween

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Haunted Wasteland", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val instructionMap = InstructionMap(inputLines)
        return instructionMap.loopUntil("AAA") {step -> step == "ZZZ"}
    }

    override fun resultPartTwo(): Any {
        val instructionMap = InstructionMap(inputLines)
        val start = instructionMap.desertMap.keys.filter { it.endsWith("A") }
        val countsPerPath = start.map{ instructionMap.loopUntil(it){ step -> step.endsWith("Z") } }
        return countsPerPath.fold(1L) { acc, i ->  lcm(acc, i.toLong()) }
    }
}

class InstructionMap(raw: List<String>) {
    val instructions: List<Char> = raw.first().toList()
    val desertMap: Map<String, Pair<String, String>> = raw.drop(2).associate { line ->
            line.substringBefore(" ").trim() to
                    Pair(
                        line.substringBetween("= (", ", ").trim(),
                        line.substringBetween(", ", ")").trim()
                    )
        }

    fun loopUntil(start: String, stopCondition: (String) -> Boolean): Int {
        var count=0
        var next = start
        while (!stopCondition(next)) {
            val instruction = instructions[count % instructions.size]
            next = if (instruction == 'L') {
                desertMap[next]!!.first
            } else {
                desertMap[next]!!.second
            }
            count++
        }
        return count
    }
}

