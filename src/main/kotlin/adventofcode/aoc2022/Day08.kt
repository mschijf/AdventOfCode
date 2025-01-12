package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day08(test=false).showResult()
}

class Day08(test: Boolean) : PuzzleSolverAbstract(test) {
    private val grid = inputLines.map { it -> it.toList().map { it - '0' } }

    override fun resultPartOne(): String {
        return grid
            .mapIndexed { rowIndex, row -> List(row.size) { colIndex -> isVisibleTree(rowIndex, colIndex) } }
            .sumOf { it.count { visibleTree -> visibleTree } }
            .toString()
    }

    override fun resultPartTwo(): String {
        return grid
            .mapIndexed { rowIndex, row -> List(row.size) { colIndex -> scenicValue(rowIndex, colIndex) } }
            .maxOf { it.max() }
            .toString()
    }

    private fun isVisibleTree(row: Int, col: Int) =
        isVisibleTree(row, col, 0, 1) ||
                isVisibleTree(row, col, 0, -1) ||
                isVisibleTree(row, col, 1, 0) ||
                isVisibleTree(row, col, -1, 0)

    private fun scenicValue(row: Int, col: Int) =
        countVisible(row, col, 0, 1) *
                countVisible(row, col, 0, -1) *
                countVisible(row, col, 1, 0) *
                countVisible(row, col, -1, 0)

    private fun isVisibleTree(row: Int, col: Int, rowDir: Int, colDir: Int): Boolean {
        var newRow = row + rowDir
        var newCol = col + colDir
        while (newRow >= 0 && newCol >= 0 && newRow < grid.size && newCol < grid[newRow].size) {
            if (grid[newRow][newCol] >= grid[row][col])
                return false
            newRow += rowDir
            newCol += colDir
        }
        return true
    }

    private fun countVisible(row: Int, col: Int, rowDir: Int, colDir: Int): Int {
        var result = 0
        var newRow = row + rowDir
        var newCol = col + colDir
        while (newRow >= 0 && newCol >= 0 && newRow < grid.size && newCol < grid[newRow].size) {
            result++
            if (grid[newRow][newCol] >= grid[row][col])
                return result
            newRow += rowDir
            newCol += colDir
        }
        return result
    }
}

//----------------------------------------------------------------------------------------------------------------------

