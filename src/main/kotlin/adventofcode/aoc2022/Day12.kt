package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import java.util.*

fun main() {
    Day12(test=false).showResult()
}

class Day12(test: Boolean) : PuzzleSolverAbstract(test) {

    private val grid = inputLines.map{it.toList()}

    override fun resultPartOne(): String {
        val stepCount = doSolve(findStartPos())
        return stepCount.toString()
    }

    override fun resultPartTwo(): String {
        return getStartPosList()
            .map{startPos -> doSolve(startPos)}
            .filter { stepCount -> stepCount > 0 }
            .min()
            .toString()
    }

    private fun doSolve(startGridPos: Point): Int {
        val visitedAfterStepsTaken = List(grid.size) { row -> MutableList(grid[row].size) {0} }
        val stepQueue : Queue<Point> = LinkedList()
        stepQueue.add(startGridPos)
        while (stepQueue.isNotEmpty()) {
            val current = stepQueue.remove()
            val stepsDone = visitedAfterStepsTaken[current.y][current.x]
            if (grid[current.y][current.x] == 'E') {
                return stepsDone
            } else {
                current
                    .neighBoursInGrid(grid)
                    .filter {neighbour -> isLegalStep(current, neighbour) && visitedAfterStepsTaken[neighbour.y][neighbour.x] <= 0}
                    .forEach { neighbour ->
                        stepQueue.add(neighbour)
                        visitedAfterStepsTaken[neighbour.y][neighbour.x] = stepsDone + 1
                    }
            }
        }
        return -1
    }

    private fun findStartPos(): Point {
        for (row in grid.indices)
            for (col in grid[row].indices)
                if (grid[row][col] == 'S')
                    return pos(col, row)
        return pos(0,0)
    }

    private fun getStartPosList(): List<Point> {
        return grid
            .mapIndexed{ rowIndex, row -> row.mapIndexed { colIndex, col -> Pair(pos(colIndex, rowIndex), col)}}
            .flatten()
            .filter {it.second == 'a' || it.second == 'S'}
            .map {it.first}
    }

    private fun isLegalStep(fromGridPos: Point, toGridPos:Point): Boolean {
        return (letterValue(grid[toGridPos.y][toGridPos.x]) - letterValue(grid[fromGridPos.y][fromGridPos.x]) <= 1)
    }

    private fun letterValue(letter: Char) = (if (letter == 'S') 'a' else if (letter == 'E') 'z' else letter) - 'a'

    private fun Point.neighBoursInGrid(grid: List<List<Any>>) = this.neighbors().filter { it.isOnGrid(grid) }
    private fun Point.isOnGrid(grid: List<List<Any>>) = (y >= 0 && y < grid.size && x >= 0 && x < grid[y].size)

}

//----------------------------------------------------------------------------------------------------------------------

//class GridPos(val row: Int, val col: Int) {
//
//    fun neighBoursInGrid(grid: List<List<Any>>): List<GridPos> {
//        val result = mutableListOf<GridPos>()
//        if (up().isOnGrid(grid)) result.add(up())
//        if (down().isOnGrid(grid)) result.add(down())
//        if (left().isOnGrid(grid)) result.add(left())
//        if (right().isOnGrid(grid)) result.add(right())
//        return result
//    }
//
//    private fun isOnGrid(grid: List<List<Any>>) = (row >= 0 && row < grid.size && col >= 0 && col < grid[row].size)
//
//    private fun up() = GridPos(row-1, col)
//    private fun down() = GridPos (row+1, col)
//    private fun left() = GridPos (row, col-1)
//    private fun right() = GridPos (row, col+1)
//}

