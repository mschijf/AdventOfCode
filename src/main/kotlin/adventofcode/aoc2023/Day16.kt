package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day16(test=false).showResult()
}

class Day16(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="The Floor Will Be Lava", hasInputFile = true) {

    private val grid = inputAsGrid()

    override fun resultPartOne(): Any {
        return beamIt(BeamRayPoint(pos(-1,0), Direction.RIGHT))
    }

    override fun resultPartTwo(): Any {
        val maxX = grid.keys.maxOf{it.x}
        val maxY = grid.keys.maxOf{it.y}
        val maxEnergyList = listOf(
            (0..maxX).maxOf { beamIt(BeamRayPoint(pos(it, -1), Direction.DOWN)) },
            (0..maxX).maxOf { beamIt(BeamRayPoint(pos(it, maxY+1), Direction.UP)) },
            (0..maxY).maxOf { beamIt(BeamRayPoint(pos(-1, it), Direction.RIGHT)) },
            (0..maxY).maxOf { beamIt(BeamRayPoint(pos(maxX+1, it), Direction.LEFT)) }
        )
        return maxEnergyList.max()
    }

    private fun beamIt(startConfiguration: BeamRayPoint): Int {
        var beamList: List<BeamRayPoint> = listOf(startConfiguration)

        val beamRayPointHistory = mutableSetOf<BeamRayPoint>()

        var oldSize = -1
        while (oldSize != beamRayPointHistory.size) {
            oldSize = beamRayPointHistory.size
            beamList = beamList
                .moveAllOneStep()
                .filterNot { it in beamRayPointHistory }
                .also {
                    beamRayPointHistory.addAll(it)
                }
        }

        return beamRayPointHistory.distinctBy { it.position }.size
    }


    private fun List<BeamRayPoint>.moveAllOneStep(): List<BeamRayPoint> {
        return this
            .flatMap{it.move()}
            .filter{it.position in grid}
    }

    private fun BeamRayPoint.move(): List<BeamRayPoint> {
        val newPos = this.position.moveOneStep(this.direction)
        return when (grid[newPos]) {
            '\\' -> when (this.direction) {
                Direction.RIGHT -> listOf(BeamRayPoint(newPos, Direction.DOWN))
                Direction.LEFT -> listOf(BeamRayPoint(newPos, Direction.UP))
                Direction.UP -> listOf(BeamRayPoint(newPos, Direction.LEFT))
                Direction.DOWN -> listOf(BeamRayPoint(newPos, Direction.RIGHT))
            }

            '/' -> when (this.direction) {
                Direction.RIGHT -> listOf(BeamRayPoint(newPos, Direction.UP))
                Direction.LEFT -> listOf(BeamRayPoint(newPos, Direction.DOWN))
                Direction.UP -> listOf(BeamRayPoint(newPos, Direction.RIGHT))
                Direction.DOWN -> listOf(BeamRayPoint(newPos, Direction.LEFT))
            }

            '-' -> when (this.direction) {
                Direction.RIGHT,
                Direction.LEFT -> listOf(BeamRayPoint(newPos, this.direction))
                Direction.UP,
                Direction.DOWN -> listOf(BeamRayPoint(newPos, Direction.LEFT), BeamRayPoint(newPos, Direction.RIGHT))
            }

            '|' -> when (this.direction) {
                Direction.RIGHT,
                Direction.LEFT -> listOf(BeamRayPoint(newPos, Direction.UP), BeamRayPoint(newPos, Direction.DOWN))
                Direction.UP,
                Direction.DOWN -> listOf(BeamRayPoint(newPos, this.direction))
            }

            '.' -> listOf(BeamRayPoint(newPos, this.direction))

            else -> emptyList()
        }
    }

}

data class BeamRayPoint(val position: Point, val direction: Direction)