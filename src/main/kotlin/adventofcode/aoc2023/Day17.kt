package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos
import java.util.*

fun main() {
    Day17(test=false).showResult()
}

class Day17(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Clumsy Crucible", hasInputFile = true) {

    private val heatMap = inputAsGrid().mapValues { it.value.digitToInt() }
    private val maxX = heatMap.keys.maxOf {it.x}
    private val maxY = heatMap.keys.maxOf {it.y}
    private val start = pos(0,0)
    private val end = pos(maxX, maxY)

    override fun resultPartOne(): Any {

        return findMinimalHeatPath(ultra = false)
    }

    override fun resultPartTwo(): Any {
        return findMinimalHeatPath(ultra = true)
    }

    /**
     * priority queue based on shortest path done so far.
     * 'heatSumMap' checks if you have visited a field, ith the same 'history'
     */
    private fun findMinimalHeatPath(ultra: Boolean): Int {
        val heatSumMap = mutableMapOf<PointStatus, Int>()

        val compareByHeatTaken: Comparator<Pair<PointStatus, Int>> = compareBy{ it.second }
        val queue = PriorityQueue(compareByHeatTaken).apply { this.add(Pair(PointStatus(start, Direction.RIGHT, 0), 0)) }

        while (queue.isNotEmpty()) {
            val (currentPointDir, currentValue) = queue.remove()

            if (currentPointDir.point == end) {
                return currentValue
            }

            currentPointDir.nextStepsPlusValues(ultra).forEach { pointStatus ->
                val newValue = currentValue + heatMap[pointStatus.point]!!
                if (heatSumMap.getOrDefault(pointStatus, 999_999_999) > newValue) {
                    heatSumMap[pointStatus] = newValue
                    queue.add(Pair(pointStatus, newValue))
                }
            }
        }
        return -1
    }

    /**
     * Voor deel 1 (gewone Crucible) geldt:
     *    - je mag maximaal 3 stappen 'rechtdoor', daarna moet je een bocht maken
     *
     * Voor deel 2 (ultra crucible)
     *    - je moet minimaal 4 stappen rechtdoor
     *    - je mag maximaal 10 stappen rechtdoor, daarna een bocht
     */
    private fun PointStatus.nextStepsPlusValues(ultra: Boolean): List<PointStatus> {
        val goLeft = this.direction.rotateLeft()
        val goRight = this.direction.rotateRight()
        val stepsDone = this.stepsDoneInDirection

        if (!ultra) {

            return listOfNotNull(
                if (stepsDone < 3) this.moveStep(this.direction) else null,
                this.moveStep(goLeft),
                this.moveStep(goRight),
            ).filter{it.point in heatMap}

        } else {
            return if (stepsDone == 0) { //alleen bij de start in punt 0,0
                listOfNotNull(
                    if (this.point.isMovePossible(goLeft, 4)) this.moveStep(goLeft) else null,
                    if (this.point.isMovePossible(goRight, 4)) this.moveStep(goRight) else null,
                    if (this.point.isMovePossible(this.direction, 4)) this.moveStep(this.direction) else null
                )
            } else if (stepsDone in 1.. 3) {
                listOf(this.moveStep(this.direction))
            } else if (stepsDone in 4 .. 9) {
                listOfNotNull(
                    if (this.point.isMovePossible(goLeft, 4)) this.moveStep(goLeft) else null,
                    if (this.point.isMovePossible(goRight, 4)) this.moveStep(goRight) else null,
                    if (this.point.isMovePossible(this.direction, 1)) this.moveStep(this.direction) else null
                )
            } else { //stepsDone = 10
                listOfNotNull(
                    if (this.point.isMovePossible(goLeft, 4)) this.moveStep(goLeft) else null,
                    if (this.point.isMovePossible(goRight, 4)) this.moveStep(goRight) else null,
                )
            }
        }
    }

    private fun Point.isMovePossible(direction: Direction, steps: Int) : Boolean {
        with(this.moveSteps(direction, steps)) {
            return x in 0..maxX && y in 0 .. maxY
        }
    }
}

data class PointStatus(val point: Point, val direction: Direction, val stepsDoneInDirection: Int) {
    fun moveStep(dir: Direction): PointStatus {
        return PointStatus(point.moveOneStep(dir), dir, if (this.direction == dir) stepsDoneInDirection+1 else 1)
    }
}