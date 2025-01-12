package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day24(test=false).showResult()
}

//todo: refactor it by using Tool-Direction and Pos/Point class

class Day24(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val valley = Valley(inputLines)
        val minutesPassed = walkTo(valley, valley.startPos, valley.endPos)
        return minutesPassed.toString()
    }

    override fun resultPartTwo(): String {
        val valley = Valley(inputLines)
        val minutesPassed1 = walkTo(valley, valley.startPos, valley.endPos)
        val minutesPassed2 = walkTo(valley, valley.endPos, valley.startPos)
        val minutesPassed3 = walkTo(valley, valley.startPos, valley.endPos)
        return (minutesPassed1 + minutesPassed2 + minutesPassed3).toString()
    }

    private fun walkTo(valley: Valley, fromPos: PosDay24, toPos: PosDay24):Int {
        var minutesPassed = 0
        var candidatePerMinuteSet = setOf(fromPos)

        while (!candidatePerMinuteSet.contains(toPos)) {
            minutesPassed++
            valley.doBlizzardMove()
            candidatePerMinuteSet = candidatePerMinuteSet.map{elfPos -> valley.generateMoves(elfPos)}.flatten().toSet()
        }
        return minutesPassed
    }
}

//----------------------------------------------------------------------------------------------------------------------

class Valley(inputLines: List<String>) {
    private val valley = inputLines.map{ str -> List(str.length) { i -> PositionInfo(str[i]) } }
    private val maxRow = valley.size
    private val maxCol = valley[0].size

    val startPos = PosDay24(0, valley.first().indexOfFirst { it.isGround } )
    val endPos = PosDay24(valley.lastIndex, valley.last().indexOfFirst { it.isGround } )

    private fun isFreeField(pos: PosDay24) = (pos.row in 0 until maxRow) && (pos.col in 0 until maxCol) && valley[pos.row][pos.col].blizzardList.isEmpty() && valley[pos.row][pos.col].isGround

    fun generateMoves(elf: PosDay24): List<PosDay24> {
        return DirectionDay24.entries
            .map { direction -> elf.moveTo(direction) }
            .plusElement(elf)
            .filter {newPos -> isFreeField(newPos)}
    }

    fun doBlizzardMove() {
        for (row in valley.indices) {
            for (col in valley[row].indices) {
                valley[row][col].blizzardList.forEach { blizzardDirection ->
                    val nextBlizzardPos = nextBlizzardPos(row, col, blizzardDirection)
                    valley[nextBlizzardPos.row][nextBlizzardPos.col].addBlizzard(blizzardDirection)
                }
            }
        }
        for (row in valley.indices) {
            for (col in valley[row].indices) {
                valley[row][col].alignBlizzards()
            }
        }
    }

    private fun nextBlizzardPos(row: Int, col: Int, dir: DirectionDay24): PosDay23 {
        val newRow = row + dir.dRow
        val newCol = col + dir.dCol
        if (valley[newRow][newCol].isWall) {
            val oppRow = ((row + 2*dir.dRow + maxRow) % maxRow) + dir.dRow
            val oppCol = ((col + 2*dir.dCol + maxCol) % maxCol) + dir.dCol
            return PosDay23(oppRow, oppCol)
        } else {
            return PosDay23(newRow, newCol)
        }
    }

    fun print(candidatePerMinuteSet: Set<PosDay23>) {
        for (row in valley.indices) {
            for (col in valley[row].indices) {
                if (PosDay23(row, col) in candidatePerMinuteSet) {
                    print("E")
                } else {
                    valley[row][col].print()
                }
            }
            println()
        }
    }
}

class PositionInfo(inputChar: Char) {
    val isWall = inputChar == '#'
    val isGround = inputChar != '#'

    val blizzardList = listOfNotNull(if (inputChar in "<>^v") toDirection(inputChar) else null).toMutableList()
    private val afterMoveBlizzardList = mutableListOf<DirectionDay24>()

    fun addBlizzard(blizzard: DirectionDay24) {
        afterMoveBlizzardList.add(blizzard)
    }

    fun alignBlizzards() {
        blizzardList.clear()
        blizzardList.addAll(afterMoveBlizzardList)
        afterMoveBlizzardList.clear()
    }

    private fun toDirection(blizzardChar: Char): DirectionDay24 {
        return when (blizzardChar) {
            '>' -> DirectionDay24.RIGHT
            '<' -> DirectionDay24.LEFT
            '^' -> DirectionDay24.UP
            'v' -> DirectionDay24.DOWN
            else -> throw Exception("Unexpected Blizzard Char")
        }
    }

    fun print() {
        if (isWall) {
            print('#')
        } else if (blizzardList.isEmpty()) {
            print('.')
        } else if (blizzardList.size == 1) {
            print(blizzardList.first())
        } else {
            print(blizzardList.size)
        }
    }
}


class PosDay24(val row: Int, val col: Int) {
    override fun hashCode() = 1000* row + col
    override fun equals(other: Any?) = if (other is PosDay24) other.row == row && other.col == col else super.equals(other)
    fun moveTo(dir: DirectionDay24) = PosDay24(row+dir.dRow, col+dir.dCol)
}

enum class DirectionDay24(val dRow: Int, val dCol: Int, private val directionChar: Char) {
    RIGHT(0,1, '>'),
    DOWN(1,0, 'v'),
    LEFT(0,-1, '<'),
    UP(-1,0, '^');

    override fun toString() = directionChar.toString()
}
