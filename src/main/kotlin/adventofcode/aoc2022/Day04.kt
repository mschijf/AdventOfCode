package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day04(test=false).showResult()
}

class Day04(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        return inputLines
            .map { SectionPair(it) }
            .count { it.hasFullyOverlap() }
            .toString()
    }

    override fun resultPartTwo(): String {
        return inputLines
            .map { SectionPair(it) }
            .count { it.hasOverlap() }
            .toString()
    }
}

class SectionPair(input: String) { //input: "2-3, 4-7", --> first: Section(2-3), second: Section(4-7)
    private val splitInput = input.split(",")
    private val first = Section(splitInput[0])
    private val second = Section(splitInput[1])

    fun hasFullyOverlap() = first.fullyOverlappedBy(second) || second.fullyOverlappedBy(first)
    fun hasOverlap() = first.hasOverlapWith(second)
}

class Section(input:String) { //input: "2-3" --> begin:2, end:3
    private val splitInput = input.split("-")
    private val begin: Int = splitInput[0].toInt()
    private val end: Int = splitInput[1].toInt()

    fun fullyOverlappedBy(other: Section) = this.begin >= other.begin && this.end <= other.end
    fun hasOverlapWith(other: Section) = this.begin <= other.end && this.end >= other.begin
}
