package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day23(test=false).showResult()
}

//todo: refactor it by using Tool-Direction
//todo: refactor it by having better Board class name (conflicts with the one from day 22)

class Day23(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val board = BoardDay23(inputLines, 10)
        repeat (board.maxRound)  { roundNumber ->
            val proposals = board.getProposedMoves(roundNumber)
            board.moveToProposals(proposals)
        }
        return board.countEmptyGroundTiles().toString()
    }

    override fun resultPartTwo(): String {
        val board = BoardDay23(inputLines, 1000)
        repeat (board.maxRound)  {roundNumber ->
            val proposals = board.getProposedMoves(roundNumber)
            if (proposals.isEmpty()) {
                return (roundNumber+1).toString()
            }
            board.moveToProposals(proposals)
        }
        return "No answer after ${board.maxRound}"
    }
}

//----------------------------------------------------------------------------------------------------------------------

class BoardDay23(inputLines: List<String>, val maxRound: Int) {
    private val extraSpace = maxRound+1
    private val width = inputLines.maxOf { it.length }

    private val board = List(extraSpace) {CharArray(width + 2*extraSpace){ '.' } } +
                inputLines
                    .map { str -> CharArray(width + 2*extraSpace) { i -> if (i >= extraSpace && i < width+extraSpace) str[i-extraSpace] else '.' } } +
                List(extraSpace) {CharArray(width + 2*extraSpace){ '.' } }

    fun countEmptyGroundTiles(): Int {
        val mostLeftElf = board.filter{it.contains('#')}.minOf{row -> row.indexOfFirst {it == '#'}}
        val mostRightElf = board.filter{it.contains('#')}.maxOf{row -> row.indexOfLast {it == '#'}}
        val mostTopElf = board.indexOfFirst { it.contains('#') }
        val mostBottomElf = board.indexOfLast { it.contains('#') }

        var countEmptyGroundTiles = 0
        for (row in mostTopElf .. mostBottomElf) {
            for (col in mostLeftElf .. mostRightElf) {
                if (board[row][col] == '.') {
                    countEmptyGroundTiles++
                }
            }
        }
        return countEmptyGroundTiles
    }

    fun getProposedMoves(roundNumber: Int): Map<PosDay23, MutableList<PosDay23>> {
        val result = HashMap<PosDay23, MutableList<PosDay23>>()
        for (row in board.indices) {
            for (column in board[row].indices) {
                if (board[row][column] == '#') {
                    if (hasNeighbours(row, column)) {
                        val posTo = proposedMove(row, column, roundNumber)
                        val posFrom = PosDay23(row, column)
                        if (posTo != null) {
                            if (!result.contains(posTo))
                                result[posTo] = mutableListOf()
                            result[posTo]!!.add(posFrom)
                        }
                    }
                }
            }
        }
        return result.filter {it.value.size == 1}
    }

    fun moveToProposals(proposals: Map<PosDay23, MutableList<PosDay23>>) {
        for (proposal in proposals) {
            removeElf(proposal.value.first())
        }
        for (proposal in proposals) {
            putElf(proposal.key)
        }
    }

    private fun removeElf(pos: PosDay23) {
        board[pos.row][pos.col] = '.'
    }

    private fun putElf(pos: PosDay23) {
        board[pos.row][pos.col] = '#'
    }

    private fun hasNeighbours(row: Int, col: Int): Boolean {
        for (dir in DirectionDay23.entries) {
            if (board[row+dir.dRow][col+dir.dCol] == '#')
                return true
        }
        return false
    }

    private fun proposedMove(row: Int, col: Int, roundNumber: Int): PosDay23? {
        repeat(4) {
            if ((it + roundNumber) % 4 == 0) {
                if (board[row + DirectionDay23.N.dRow][col + DirectionDay23.N.dCol] == '.' && board[row + DirectionDay23.NE.dRow][col + DirectionDay23.NE.dCol] == '.' && board[row + DirectionDay23.NW.dRow][col + DirectionDay23.NW.dCol] == '.')
                    return PosDay23(row + DirectionDay23.N.dRow, col + DirectionDay23.N.dCol)
            }
            if ((it + roundNumber) % 4 == 1) {
                if (board[row + DirectionDay23.S.dRow][col + DirectionDay23.S.dCol] == '.' && board[row + DirectionDay23.SE.dRow][col + DirectionDay23.SE.dCol] == '.' && board[row + DirectionDay23.SW.dRow][col + DirectionDay23.SW.dCol] == '.')
                    return PosDay23(row + DirectionDay23.S.dRow, col + DirectionDay23.S.dCol)
            }
            if ((it + roundNumber) % 4 == 2) {
                if (board[row + DirectionDay23.W.dRow][col + DirectionDay23.W.dCol] == '.' && board[row + DirectionDay23.NW.dRow][col + DirectionDay23.NW.dCol] == '.' && board[row + DirectionDay23.SW.dRow][col + DirectionDay23.SW.dCol] == '.')
                    return PosDay23(row + DirectionDay23.W.dRow, col + DirectionDay23.W.dCol)
            }
            if ((it + roundNumber) % 4 == 3) {
                if (board[row + DirectionDay23.E.dRow][col + DirectionDay23.E.dCol] == '.' && board[row + DirectionDay23.NE.dRow][col + DirectionDay23.NE.dCol] == '.' && board[row + DirectionDay23.SE.dRow][col + DirectionDay23.SE.dCol] == '.')
                    return PosDay23(row + DirectionDay23.E.dRow, col + DirectionDay23.E.dCol)
            }
        }
        return null
    }

    fun print() {
        board.forEach { println(it) }
    }
}

class PosDay23(val row: Int, val col: Int) {
    override fun hashCode() = 1000* row + col
    override fun equals(other: Any?): Boolean {
        if (other is PosDay23)
            return other.row == row && other.col == col
        return super.equals(other)
    }
}

enum class DirectionDay23(val dRow: Int, val dCol: Int) {
    E(0,1),
    S(1,0),
    W(0,-1),
    N(-1,0),
    NE(-1,1),
    NW(-1,-1),
    SW(1,-1),
    SE(1,1);
}

