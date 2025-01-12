package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Point
import tool.mylambdas.collectioncombination.mapCombinedItems
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day11(test=true).showResult()
}

class Day11(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Cosmic Expansion", hasInputFile = true) {

    private val galaxyMap = inputAsGrid().filter { it.value != '.' }.keys
    private val emptyVerticalLines = galaxyMap.emptyVerticalLines()
    private val emptyHorizontalLines = galaxyMap.emptyHorizontalLines()

    override fun resultPartOne(): Any {
        return galaxyMap.toList()
            .mapCombinedItems { point, point2 ->
                point.galaxyDistanceTo(point2, expansionFactor = 2)}
            .sum()
    }

    override fun resultPartTwo(): Any {
        val factor = if (test) 100 else 1_000_000
        return galaxyMap.toList()
            .mapCombinedItems { point, point2 ->
                point.galaxyDistanceTo(point2, expansionFactor = factor)}
            .sum()
    }

    private fun Point.galaxyDistanceTo(other: Point, expansionFactor: Int) : Long {
        val minX = min(this.x, other.x)
        val maxX = max(this.x, other.x)
        val minY = min(this.y, other.y)
        val maxY = max(this.y, other.y)

        val emptyVerticalLinesBetween = emptyVerticalLines.count{ it in minX..maxX}
        val emptyHorizontalLinesBetween = emptyHorizontalLines.count{ it in minY..maxY}
        return this.distanceTo(other) + (expansionFactor-1).toLong() * (emptyHorizontalLinesBetween + emptyVerticalLinesBetween)
    }

    private fun Set<Point>.emptyVerticalLines(): List<Int> {
        val minX = this.minOf { it.x }
        val maxX = this.maxOf { it.x }
        return (minX..maxX).filter{this.isEmptyVerticalLine(it)}
    }

    private fun Set<Point>.emptyHorizontalLines(): List<Int> {
        val minY = this.minOf { it.y }
        val maxY = this.maxOf { it.y }
        return (minY..maxY).filter{this.isEmptyHorizontalLine(it)}
    }

    private fun Set<Point>.isEmptyVerticalLine(x: Int): Boolean {
        return this.none{it.x == x}
    }

    private fun Set<Point>.isEmptyHorizontalLine(y: Int): Boolean {
        return this.none{it.y == y}
    }
}


