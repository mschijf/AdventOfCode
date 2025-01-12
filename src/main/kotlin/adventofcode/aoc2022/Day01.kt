package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import tool.mylambdas.splitByCondition


fun main() {
    Day01(test=false).showResult()
}

class Day01(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        return inputLines
            .splitByCondition { it.isBlank() }
            .maxOf { it -> it.sumOf { it.toInt() }}
            .toString()
    }

    override fun resultPartTwo(): String {
        return inputLines
            .splitByCondition { it.isBlank()}
            .map { it -> it.sumOf { it.toInt() }}
            .sortedDescending()
            .take(3)
            .sum()
            .toString()
    }
}


