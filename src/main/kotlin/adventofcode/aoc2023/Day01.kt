package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day01(test = false).showResult()
}

class Day01(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Trebuchet?!", hasInputFile = true) {

    override fun resultPartOne(): Any {
        return inputLines
            .map{ it.filter { ch -> ch.isDigit() } }
            .map { 10*it.first().digitToInt() + it.last().digitToInt() }
            .sum()
    }

    override fun resultPartTwo(): Any {
        return inputLines(testFile="example2")
            .map{ it
                .replaceWordByNumber()
                .filter { ch -> ch.isDigit() }
            }
            .map { 10*it.first().digitToInt() + it.last().digitToInt() }
            .sum()
    }

    private fun String.replaceWordByNumber(): String {
        val new = StringBuilder()
        for (i in this.indices) {
            when  {
                this.substring(i).startsWith("one") -> new.append(1)
                this.substring(i).startsWith("two") -> new.append(2)
                this.substring(i).startsWith("three") -> new.append(3)
                this.substring(i).startsWith("four") -> new.append(4)
                this.substring(i).startsWith("five") -> new.append(5)
                this.substring(i).startsWith("six") -> new.append(6)
                this.substring(i).startsWith("seven") -> new.append(7)
                this.substring(i).startsWith("eight") -> new.append(8)
                this.substring(i).startsWith("nine") -> new.append(9)
                else -> new.append(this[i])
            }
        }
        return new.toString()
    }
}




