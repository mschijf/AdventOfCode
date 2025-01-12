package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day02(test=false).showResult()
}

class Day02(test: Boolean) : PuzzleSolverAbstract(test) {

    private val letter1ToTool = mapOf('A' to Tool.ROCK, 'B' to Tool.PAPER, 'C' to Tool.SCISSORS)
    private val letter2ToTool = mapOf('X' to Tool.ROCK, 'Y' to Tool.PAPER, 'Z' to Tool.SCISSORS)
    private val letter2ToResult = mapOf('X' to Result.LOSS, 'Y' to Result.DRAW, 'Z' to Result.WIN)

    private fun outCome(pair: Pair<Tool, Tool>): Result {
        return pair.second.played(pair.first)
    }

    private fun findTool (pair: Pair<Tool, Result>): Tool {
        return Tool.entries.first{it.played(pair.first) == pair.second}
    }

    override fun resultPartOne(): String {
        return inputLines
            .map { Pair(it[0], it[2]) }
            .map { Pair(letter1ToTool[it.first]!!, letter2ToTool[it.second]!!) }
            .sumOf { outCome(it).value + it.second.value }
            .toString()
    }

    override fun resultPartTwo(): String {
        return inputLines
            .map { Pair(it[0], it[2]) }
            .map { Pair(letter1ToTool[it.first]!!, letter2ToResult[it.second]!!) }
            .map { Pair(it.first, findTool(it)) }
            .sumOf { outCome(it).value + it.second.value }
            .toString()
    }
}

enum class Tool(val value: Int, private val playValue: Int) {
    ROCK(1, 0),
    PAPER(2, 1),
    SCISSORS(3, 2);

    fun played(other: Tool): Result {
        return if (this == other)
            Result.DRAW
        else if ((this.playValue+2) % 3 == other.playValue)
            Result.WIN
        else
            Result.LOSS
    }
}

enum class Result(val value: Int) {
    LOSS(0),
    WIN(6),
    DRAW(3)
}
