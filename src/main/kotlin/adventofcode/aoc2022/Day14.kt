package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import java.lang.Integer.max

fun main() {
    Day14(test=false).showResult()
}

class Day14(test: Boolean) : PuzzleSolverAbstract(test) {
    private val listOfLines = inputLines
        .map{strPath ->
            strPath
                .split(" -> ")
                .map { pos(it.split(",")[0].toInt(), it.split(",")[1].toInt()) }
                .windowed(2)
                .map{Line(it[0], it[1])}
        }
        .flatten()

    override fun resultPartOne(): String {
        val grid = fillGrid()

        var posXY = dropSand(grid)
        var count = 0
        while(posXY.y > 0) {
            grid[posXY.x][posXY.y] = 'o'
            posXY = dropSand(grid)
            count++
        }
//        printGrid(grid, 490, 510, 0, 12)
        return count.toString()
    }

    override fun resultPartTwo(): String {

        val maxY = listOfLines.maxOfOrNull { line -> max(line.startPoint.y, line.endPoint.y) }?:0

        val grid = fillGrid(bottomY = maxY+2)

        var count = 0
        do {
            val posXY = dropSand(grid)
            grid[posXY.x][posXY.y] = 'o'
            count++
        } while(!(posXY.x == 500 && posXY.y == 0))

//        printGrid(grid, 490, 510, 0, maxY+3)
        return count.toString()
    }

    private fun dropSand(grid: Array<CharArray>): Point {
        var sandPoint = pos(500, 0)
        while (sandPoint.y < grid[sandPoint.x].size-1) {
            if (grid[sandPoint.x][sandPoint.y + 1] == '.') {
                sandPoint = pos(sandPoint.x, sandPoint.y + 1)
            } else if (grid[sandPoint.x - 1][sandPoint.y + 1] == '.') {
                sandPoint = pos(sandPoint.x - 1, sandPoint.y + 1)
            } else if (grid[sandPoint.x + 1][sandPoint.y + 1] == '.') {
                sandPoint = pos(sandPoint.x + 1, sandPoint.y + 1)
            } else {
                return sandPoint
            }
        }
        return pos(-1,-1)
    }

    private fun fillGrid(bottomY: Int? = null) : Array<CharArray> {
        val grid = Array(1000) { CharArray(500) {'.'} }
        listOfLines.forEach {line ->
            if (line.startPoint.x == line.endPoint.x) {
                for (y in line.startPoint.y .. line.endPoint.y) {
                    grid[line.startPoint.x][y] = 'R'
                }
            } else if (line.startPoint.y == line.endPoint.y) {
                for (x in line.startPoint.x .. line.endPoint.x ) {
                    grid[x][line.startPoint.y] = 'R'
                }
            } else {
                println("UNEXPECTED INPUT!!")
            }
        }
        if (bottomY != null) {
            for (x in 0 until grid.size) {
                grid[x][bottomY] = 'R'
            }
        }
        return grid
    }

    private fun printGrid(grid: Array<CharArray>, minX: Int, maxX: Int, minY: Int, maxY: Int) {
        for (y in minY .. maxY) {
            for (x in minX .. maxX) {
                print(grid[x][y])
            }
            println()
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------

class Line(first: Point, last: Point) {
    val startPoint: Point
    val endPoint: Point
    init {
        if (first.x < last.x || first.y < last.y) {
            startPoint = first
            endPoint = last
        } else {
            startPoint = last
            endPoint = first
        }
    }
}