package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Direction
import tool.coordinate.twodimensional.Point

fun main() {
    Day23(test=false).showResult()
}

class Day23(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="A Long Walk", hasInputFile = true) {

    private val totalMap = inputAsGrid()
    private val trail = totalMap.filterValues { it in ".<>^v" }.keys
    private val start = trail.first{it.y==0}
    private val end = trail.maxBy{it.y}
    private val graph = createIntermediatesGraph()
    private val graphNeighbors = graph.keys.groupBy { it.first }

    override fun resultPartOne(): Any {
        return findLongest(start)
    }

    override fun resultPartTwo(): Any {
        return findLongestViaIntermediates(start)
    }

    private fun findLongest(current: Point, onPath: Set<Point> = emptySet()): Int {
        if (current == end) {
            return 0
        }

        var max = Int.MIN_VALUE
        current.nextMoves().filter{it !in onPath}.forEach { nextStep->
            val lengthPath = 1 + findLongest(nextStep, onPath+current)
            if (lengthPath > max) {
                max = lengthPath
            }
        }
        return max
    }

    private fun findLongestViaIntermediates(current: Point, onPath: Set<Point> = emptySet()): Int {
        if (current == end)
            return 0

        var max = Int.MIN_VALUE
        val nextSteps = graphNeighbors[current]!!.filter { it.second !in onPath }
        nextSteps.forEach { step->
            val lengthPath = graph[step]!! + findLongestViaIntermediates(step.second, onPath+current)
            if (lengthPath > max) {
                max = lengthPath
            }
        }
        return max
    }

    private fun Point.nextMoves(): List<Point> {
        return when (totalMap[this]!!) {
            '>' -> listOf(this.moveOneStep(Direction.RIGHT) )
            '<' -> listOf(this.moveOneStep(Direction.LEFT) )
            '^' -> listOf(this.moveOneStep(Direction.UP))
            'v' -> listOf(this.moveOneStep(Direction.DOWN))
            '.' -> this.neighbors()
            else -> throw Exception("Unknown Trail Thing")
        }.filter { it in trail }
    }

    private fun Point.nextMovesPart2(): List<Point> {
        return this.neighbors().filter { it in trail }
    }

    private fun createIntermediatesGraph(): Map<Pair<Point, Point>, Int>
    {
        val result = mutableMapOf<Pair<Point, Point>, Int>()

        val intermediates = trail.filter { it.nextMovesPart2().size > 2 } + start + end
        intermediates.forEach { im1->
            intermediates.forEach { im2 ->
                if (im1 != im2) {
                    val path = findPathBetweenIntermediates(im1, im2)
                    if (path > 0) {
                        result[im1 to im2] = path
                        result[im2 to im1] = path
                    }
                }
            }
        }
        return result
    }

    private fun findPathBetweenIntermediates(from: Point, to: Point): Int {
        val queue = ArrayDeque<Pair<Point, Int>>()
        from.nextMovesPart2().forEach { nm->queue.add(Pair(nm, 1)) }

        val visited = mutableSetOf<Point>(from)

        while (queue.isNotEmpty()) {
            val (current, stepsDone) = queue.removeFirst()
            if (current == to)
                return stepsDone
            val nextMoves = current.nextMovesPart2().filter {it !in visited }
            if (nextMoves.size == 1) {
                visited += current
                queue.add(Pair(nextMoves.first(), stepsDone + 1))
            }
        }
        return -1
    }
}


