package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day10Alternative(test=false).showResult()
}

class Day10Alternative(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val requestedCycleNumbers = listOf(20, 60, 100, 140, 180, 220)
        return inputLines
            .asSequence()
            .map{Command(it)}
            .map{cmd -> List(cmd.cycleLength-1){0} + cmd.adder  }
            .flatten()
            .scan (1) { sum, cycleAdder -> sum + cycleAdder }
            .withIndex()
            .filter { iv -> iv.index+1 in requestedCycleNumbers }
            .sumOf { iv -> (iv.index+1) * iv.value }
            .toString()
    }

    override fun resultPartTwo(): String {
        inputLines
            .asSequence()
            .map{Command(it)}
            .map{cmd -> List(cmd.cycleLength-1){0} + cmd.adder  }
            .flatten()
            .scan (1) { sum, cycleAdder -> sum + cycleAdder }
            .forEachIndexed { cycleNumber, valueXregister -> drawPixel(cycleNumber, valueXregister) }
        println()
        return "END"
    }

    private fun drawPixel(cycleNumber: Int, valueXregister: Int) {
        val pixelPosition = cycleNumber % 40
        if (pixelPosition == 0) {
            println()
        }
        if (pixelPosition in valueXregister - 1 .. valueXregister + 1) {
            print("####")
        } else {
            print("    ")
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------