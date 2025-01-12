package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import tool.mylambdas.splitByCondition
import kotlin.math.max

fun main() {
    Day13(test=false).showResult()
}

class Day13(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Point of Incidence", hasInputFile = true) {

    private val patternList = inputLines.splitByCondition { it.isEmpty() }.map{Pattern.of(it)}

    override fun resultPartOne(): Any {
        return patternList.map{ it.findMirrorValue() }.sum()
    }

    override fun resultPartTwo(): Any {
        return patternList.map{it.findMirrorValueAfterSwappingRockForAsh()}.sum()
    }
}

data class Pattern(val rocks: Set<Point>, val ashes: Set<Point>) {
    private val maxX: Int = max(rocks.maxOf{it.x}, ashes.maxOf{it.x} )
    private val maxY: Int = max(rocks.maxOf{it.y}, ashes.maxOf{it.y} )

    companion object {
        fun of (raw: List<String>): Pattern {
            val tmpMap = raw.flatMapIndexed { y, line ->
                line.mapIndexed { x, ch ->  pos(x,y) to ch}
            }.toMap()

            return Pattern(
                rocks = tmpMap.filterValues { it == '#' }.keys,
                ashes = tmpMap.filterValues { it == '.' }.keys
            )
        }
    }

    private fun Point.isReflectedVertical(xLine: Int) : Boolean {

        val mirrored = pos(xLine+(xLine-this.x)-1, this.y)
        return (mirrored in rocks) || mirrored.x < 0 || mirrored.x > maxX
    }
    private fun Point.isReflectedHorizontal(yLine: Int) : Boolean {
        val mirrored = pos(this.x, yLine+(yLine-this.y)-1)
        return (mirrored in rocks) || mirrored.y < 0 || mirrored.y > maxY
    }

    private fun isReflectedVertical(xLine: Int) : Boolean {
        return rocks.all { it.isReflectedVertical(xLine)}
    }
    private fun isReflectedHorizontal(yLine: Int) : Boolean {
        return rocks.all { it.isReflectedHorizontal(yLine)}
    }

    private fun findMirrorVertical(excludeValue: Int): Int {
        return (1..maxX).filter{ it != excludeValue }.firstOrNull() { xLine -> isReflectedVertical(xLine) } ?: 0
    }

    private fun findMirrorHorizontal(excludeValue: Int): Int {
        return (1..maxY).filter{ it != excludeValue }.firstOrNull() { yLine -> isReflectedHorizontal(yLine) } ?: 0
    }

    fun findMirrorValue(excludeValue: Int = -1): Int {
        val x = findMirrorVertical(excludeValue)
        if (x > 0)
            return x

        val y = findMirrorHorizontal(excludeValue/100)
        return 100*y
    }

    fun findMirrorValueAfterSwappingRockForAsh(): Int {
        val org = findMirrorValue()
        val aa = ashes.map { ash ->
            Pattern(rocks + ash, ashes - ash).findMirrorValue(excludeValue = org)
        }.firstOrNull { it != 0 && it != org } ?:0

        if (aa != 0)
            return aa

        return rocks.map {rock ->
            Pattern(rocks - rock, ashes+rock).findMirrorValue(excludeValue = org)
        }.firstOrNull { it != 0 && it != org } ?:0
    }

}

