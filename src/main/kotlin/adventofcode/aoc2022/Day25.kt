package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day25(test=false).showResult()
}

class Day25(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        return toSnafuNumber(inputLines.sumOf { toDecimal(it) })
    }

    private fun toDecimal(snafuString: String): Long {
        var result = 0L
        for (ch in snafuString) {
            result = 5*result + toDigit(ch)
        }
        return result
    }

    private fun toSnafuNumber(decimal: Long) : String {
        var result = ""
        var rest = decimal
        while (rest != 0L) {
            val last = rest % 5
            val shift = if (last >= 3) last-5 else last
            result = toSnafuChar(shift.toInt()) + result
            rest = (rest - shift) / 5
        }
        return result
    }

    //2-=2-0=-0-=0200=--21
    private fun toDigit(ch: Char) = if (ch == '-') -1 else if (ch == '=') -2 else ch - '0'
    private fun toSnafuChar(digit: Int) = if (digit == -1) '-' else if (digit == -2) '=' else '0' + digit


}

//----------------------------------------------------------------------------------------------------------------------


