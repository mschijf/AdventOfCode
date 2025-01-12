package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract


fun main() {
    Day03(test=false).showResult()
}

class Day03(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        return inputLines
            .map { Pair(it.substring(0, it.length / 2), it.substring(it.length / 2, it.length)) }
            .map { appearsInBoth(it.first, it.second) }
            .sumOf { toValue(it) }
            .toString()
    }

    override fun resultPartTwo(): String {
        return inputLines
            .chunked(3)
            .map { appearsInBoth(appearsInBoth(it[0], it[1]), it[2]) }
            .sumOf { toValue(it) }
            .toString()
    }

    private fun appearsInBoth(first: String, second: String): String {
        return first.filter { second.indexOf(it) >= 0 }
    }

    private fun toValue (s: String) : Int {
        return if (s.lowercase() == s) {
            s[0] - 'a' + 1
        } else {
            s[0] - 'A' + 27
        }
    }

}
