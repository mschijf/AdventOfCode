package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.xyCoordinate
import java.text.DecimalFormat

fun main() {
    Day17(test=false).showResult()
}

const val chamberWidth = 7
const val leftEdgeRoom = 2
const val bottomEdgeRoom = 3

class Day17(test: Boolean) : PuzzleSolverAbstract(test) {
    private val jetsStream = JetStream(inputLines[0])
    private val rockShapeList = RockShapeList()

    override fun resultPartOne(): String {
        jetsStream.reset()
        rockShapeList.reset()
        val rockPieces = mutableSetOf<Point>()

        repeat(2022) {
            val rock = Rock(rockShape=rockShapeList.nextShape(), highestTop=(rockPieces.maxOfOrNull { it.y } ?: -1) + 1)
            rockRolling(rock, rockPieces)
            rockPieces.addAll(rock.coordinateList)
        }

        return (rockPieces.maxOf {  it.y } + 1).toString()
    }

    override fun resultPartTwo(): String {
        jetsStream.reset()
        rockShapeList.reset()
        val rockPieces = mutableSetOf<Point>()
        val stateList = mutableListOf<State>()

        stateList.add(State(rockPieces, rockShapeList, jetsStream))
        repeat(5000) {
            val rock = Rock(rockShape=rockShapeList.nextShape(), highestTop=(rockPieces.maxOfOrNull { it.y } ?: -1) + 1)
            rockRolling(rock, rockPieces)
            rockPieces.addAll(rock.coordinateList)

            val newState = State(rockPieces, rockShapeList, jetsStream)
            val index = stateList.indexOfFirst { it == newState }
//            println("${it+1} $newState")
            if (index >= 0) {
                val cycleLength = (stateList.size - index)
                val heightPerCycle = newState.height - stateList[index].height
                val countRepeatingCycles = (1_000_000_000_000 - index) / cycleLength
                val initialRounds = 1_000_000_000_000 - countRepeatingCycles * cycleLength
                val startHeight = stateList[initialRounds.toInt()].height

                println("THERE IT IS!  $index --> ${stateList.size}")
                println("   Cycle Length (in rounds)   : $cycleLength")
                println("   Repeating Cycles           : " + DecimalFormat("#,###.##").format(countRepeatingCycles))
                println("   Height per Cycle           : $heightPerCycle")
                println("   Initial Rounds             : $initialRounds")
                println("   Height after Initial Rounds: $startHeight")

                return (countRepeatingCycles * heightPerCycle + startHeight).toString()

            }
            stateList.add(newState)
        }
        return "NO ANSWER FOUND"
    }

    private fun rockRolling(rock: Rock, rockPieces: Set<Point>) {
        rock.pushByJet(jetsStream.nextDirection(), rockPieces)
        while (rock.canFallDown(rockPieces)) {
            rock.fallDownOneUnit()
            rock.pushByJet(jetsStream.nextDirection(), rockPieces)
        }
    }
}

//----------------------------------------------------------------------------------------------------------------------

class State(rockPieces: Set<Point>, rockShapeList: RockShapeList, jetStream: JetStream) {
    private val topPattern = mutableListOf<Int>()
    init {
        val firstColumnHeight = rockPieces.filter { it.x == 0 }.maxOfOrNull { it.y } ?: 0
        for (col in 1 until  chamberWidth) {
            topPattern.add((rockPieces.filter { it.x == col }.maxOfOrNull { it.y } ?: 0) - firstColumnHeight)
        }
    }


    private val rockShapeIndex =  rockShapeList.currentIndex
    private val jetStreamIndex = jetStream.currentIndex
    val height = (rockPieces.maxOfOrNull { it.y } ?: -1) + 1

    override fun equals(other: Any?): Boolean {
        if (other !is State)
            return super.equals(other)
        return (rockShapeIndex == other.rockShapeIndex) && (jetStreamIndex == other.jetStreamIndex) && (topPattern == other.topPattern)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun toString(): String {
        return "$topPattern Rock:$rockShapeIndex Jet:$jetStreamIndex"
    }
}

//----------------------------------------------------------------------------------------------------------------------

class Rock(rockShape: RockShape, highestTop: Int) {
    var coordinateList = rockShape.coordinateList
        .map { c -> xyCoordinate(c.x + leftEdgeRoom, c.y+highestTop+bottomEdgeRoom+rockShape.shapeHeight-1) }

    fun canFallDown(rockPieces: Set<Point>): Boolean {
        return if (coordinateList.minOf { it.y } <= 0) {
            false
        } else {
            coordinateList
                .map { c -> c.down() }
                .intersect(rockPieces)
                .isEmpty()
        }
    }

    private fun canBePushedLeft(rockPieces: Set<Point>): Boolean {
        return if (coordinateList.minOf { it.x } <= 0) {
            false
        } else {
            coordinateList
                .map { c -> c.left() }
                .intersect(rockPieces)
                .isEmpty()
        }
    }

    private fun canBePushedRight(rockPieces: Set<Point>): Boolean {
        return if (coordinateList.maxOf { it.x } >= chamberWidth-1) {
            false
        } else {
            return coordinateList
                .map { c -> c.right() }
                .intersect(rockPieces)
                .isEmpty()
        }
    }

    fun pushByJet(dir: Direction, rockPieces: Set<Point>) {
        if (dir == Direction.LEFT) {
            if (canBePushedLeft(rockPieces)) {
                coordinateList = coordinateList.map { c -> c.left() }
            }
        } else {
            if (canBePushedRight(rockPieces)) {
                coordinateList = coordinateList.map { c -> c.right() }
            }
        }
    }

    fun fallDownOneUnit() {
        coordinateList = coordinateList
                .map { c -> c.down() }
    }
}

//----------------------------------------------------------------------------------------------------------------------

class JetStream(inputStr: String) {
    var currentIndex = 0
        private set

    private val dirList = inputStr
        .map {if (it == '<') Direction.LEFT else Direction.RIGHT}

    fun nextDirection(): Direction {
        val oldIndex = currentIndex
        currentIndex = (currentIndex + 1) % dirList.size
        return dirList[oldIndex]
    }

    fun reset() {
        currentIndex = 0
    }
}

class RockShapeList {
    private val shapeList = listOf(
        RockShape(listOf(xyCoordinate(0,0), xyCoordinate(1,0), xyCoordinate(2,0), xyCoordinate(3,0))),
        RockShape(listOf(xyCoordinate(1,0), xyCoordinate(0,-1), xyCoordinate(1,-1), xyCoordinate(2,-1), xyCoordinate(1,-2))),
        RockShape(listOf(xyCoordinate(2,0), xyCoordinate(2,-1), xyCoordinate(0,-2), xyCoordinate(1,-2), xyCoordinate(2,-2))),
        RockShape(listOf(xyCoordinate(0,0), xyCoordinate(0,-1), xyCoordinate(0,-2), xyCoordinate(0,-3))),
        RockShape(listOf(xyCoordinate(0,0), xyCoordinate(1,0), xyCoordinate(0,-1), xyCoordinate(1,-1))),
    )

    var currentIndex = 0
        private set

    fun nextShape(): RockShape {
        val oldIndex = currentIndex
        currentIndex = (currentIndex + 1) % shapeList.size
        return shapeList[oldIndex]
    }

    fun reset() {
        currentIndex = 0
    }
}

data class RockShape(val coordinateList : List<Point>) {
    val shapeHeight = coordinateList.distinctBy { it.y }.count()
}
