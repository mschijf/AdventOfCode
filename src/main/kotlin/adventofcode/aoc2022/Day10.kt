package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract


fun main() {
    Day10(test=false).showResult()
}

class Day10(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        var valueXregister = 1
        var sum = 0
        val requestedCycleNumbers = listOf(20, 60, 100, 140, 180, 220)
        inputLines
            .map{Command(it)}
            .map{cmd -> List(cmd.cycleLength-1){0} + cmd.adder  }
            .flatten()
            .forEachIndexed { cycleNumber, cycleAdder ->
                if (cycleNumber+1 in requestedCycleNumbers) {
                    sum += (cycleNumber+1) * valueXregister
                }
                valueXregister += cycleAdder
            }
        return sum.toString()
    }

    override fun resultPartTwo(): String {
        var valueXregister = 1
        inputLines
            .map{Command(it)}
            .map{cmd -> List(cmd.cycleLength-1){0} + cmd.adder  }
            .flatten()
            .forEachIndexed { cycleNumber, cycleAdder ->
                drawPixel(cycleNumber, valueXregister)
                valueXregister += cycleAdder
            }
        println()
        return "END"
    }

    private fun drawPixel(cycleNumber: Int, valueXregister: Int) {
        val pixelPosition = cycleNumber % 40
        if (pixelPosition == 0) {
            println()
        }
        if (pixelPosition in valueXregister - 1 .. valueXregister + 1) {
            print("##")
        } else {
            print("..")
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------

class Command(cmd: String) {
    val cycleLength = if (cmd.startsWith("noop")) 1 else 2
    val adder = if (cmd.startsWith("addx")) cmd.substring("addx".length+1).toInt() else 0
}