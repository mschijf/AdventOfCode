package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day09(test=false).showResult()
}

class Day09(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Mirage Maintenance", hasInputFile = true) {

    private val historyLines = inputLines.map{line -> line.split("\\s+".toRegex()).map{ word -> word.toInt() }}

    override fun resultPartOne(): Any {
        return historyLines.sumOf{ line -> line.predictNextValue() }
    }

    override fun resultPartTwo(): Any {
        return historyLines.sumOf{ line -> line.predictFirstValue() }
    }

    //
    // creates a list of lists.
    // Each list has the differences of two adjacent fields of the previous list
    //

    private fun List<Int>.sequenceOfdifferences() : List<List<Int>> {
        val sequenceOfDifferences = mutableListOf<List<Int>>()
        var next = this
        while (next.any{it != 0}) {
            sequenceOfDifferences.add(next)
            next = next.zipWithNext { a, b -> b-a }
        }
        return sequenceOfDifferences
    }

    private fun List<Int>.predictNextValue(): Int {
        return this
            .sequenceOfdifferences()
            .map {it.last()}
            .foldRight(0){lastValue, acc -> acc + lastValue}
    }

    private fun List<Int>.predictFirstValue(): Int {
        return this
            .sequenceOfdifferences()
            .map {it.first()}
            .foldRight(0){firstValue, acc -> firstValue - acc}
    }

}


