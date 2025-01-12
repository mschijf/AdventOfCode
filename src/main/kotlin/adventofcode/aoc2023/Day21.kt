package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.Point
import tool.coordinate.twodimensional.pos

fun main() {
    Day21(test=false).showResult()
}

class Day21(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val wholeGrid = inputAsGrid()
    private val gardenPlots = wholeGrid.filterValues { it == '.' || it == 'S' }.keys
    private val start = wholeGrid.filterValues { it == 'S' }.keys.first()
    private val gridSize = wholeGrid.keys.maxOf { it.y } + 1 //note that input is a rectangle

    override fun resultPartOne(): Any {
        val stepsToDo = if (test) 6 else 64
        return doSteps(stepsToDo)
        //alternative:
        //  return minimalPathToAllFields().filterValues { it <= stepsToDo && (it % 2) == (stepsToDo % 2)}.count()
    }

    override fun resultPartTwo(): Any {
        if (test) {
            return "we're not gonna run this one for test"
        }
        return solve2()
    }

    private fun doSteps(maxSteps: Int): Int {
        var visited = setOf<Point>(start)

        repeat(maxSteps) {
            visited = visited.flatMap { pos -> pos.neighbors().filter { it in gardenPlots } }.toSet()
        }
        return visited.size
    }

    private fun doStepsPart2(maxSteps: Int): Int {
        var visited = setOf<Point>(start)

        repeat(maxSteps) {
            visited = visited.flatMap {
                pos -> pos.neighbors().filter {nb->
                    nb.mappedPoint(gridSize) in gardenPlots
                }
            }.toSet()
        }
        return visited.size
    }


    private fun minimalPathToAllFields(): Map<Point, Int> {
        val result = mutableMapOf<Point, Int>()
        val queue = ArrayDeque<Pair<Point, Int>>().apply{add(Pair(start, 0))}
        while (queue.isNotEmpty()) {
            val (current, stepsDone) = queue.removeFirst()
            current.neighbors().filter { it in gardenPlots }.filter {it !in result}.forEach {nb->
                result[nb] = stepsDone+1
                queue.add(Pair(nb, stepsDone+1))
            }
        }
        return result
    }

    private fun solve2():Any {
        val stepsToDo = 26_501_365 //26501365
        val numStepCycles = (stepsToDo - (gridSize / 2)) / gridSize

        // we need to do a call of doStepsPart2(26_501_365), but that takes of course way too long
        // we can find out a sequence in growth if we do some steps (see explanation below all code):
        //

        val stepIncrements = (0..3).map { incr -> (gridSize / 2) + (gridSize * incr) }
        val reachableTilesCountList = stepIncrements.map { doStepsPart2(it) }
//        val reachableTileCounts = listOf(3691, 32975, 91439, 179083)

        // if we check the differences (use day 9 solution for that),
        val diffs = reachableTilesCountList.sequenceOfDifferences()
        println(diffs.joinToString("\n"))
        // we can find out that the differences (of the differences of the differences...) become constant:
        // we can find out that, this already is after four rounds for our input:
        //
        //        [3691, 32975, 91439, 179083]
        //        [29284, 58464, 87644]
        //        [29180, 29180]

        // now we can use that to extrapolate this a number of times, by sub sequentially calculating the last number of the diff-lists:

        val lastNumbers = diffs.map{it.last().toLong()}.toMutableList()
        repeat(numStepCycles-(diffs[0].size-1)) {
            for (i in lastNumbers.size - 2 downTo 0) {
                lastNumbers[i] += lastNumbers[i+1]
            }
        }
        println("Correct answer:597102953699891")
        return lastNumbers[0]
    }

    private fun List<Int>.sequenceOfDifferences() : List<List<Int>> {
        val sequenceOfDifferences = mutableListOf<List<Int>>()
        var next = this
        while (next.any{it != 0}) {
            sequenceOfDifferences.add(next)
            next = next.zipWithNext { a, b -> b-a }
        }
        return sequenceOfDifferences
    }


    private fun Point.mappedPoint(gridSize: Int) = pos(Math.floorMod(this.x, gridSize), Math.floorMod(this.y, gridSize))
}



// clever thing: 26_501_365 is not just a number, it is 202300 * 131 + 65
// Thanks to: https://www.ericburden.work/blog/2023/12/21/advent-of-code-day-21/
//
// ALso a nice read: https://github.com/villuna/aoc23/wiki/A-Geometric-solution-to-advent-of-code-2023,-day-21
//

//        /*
//         And this is where it starts to get a bit...wonky. We can observe from
//         the input that the number of steps the elf wants to take is a multiple
//         of the grid size + (grid size // 2). The grid is 131 tiles square, so
//         we can see that 26501365 = n * 131 + 65, with n = 202,300. We also
//         note that, in the real input, there is a clear path from the start to
//         each edge and each edge is clear all the way around. Additionally, the
//         input forms a _diamond_ shape with a clear lane all the way around the
//         manhattan distance of (grid size / 2) from the start. So, this means
//         our zoomed out grid looks like:
//
//                2....2
//                ..11..
//                ..11..
//                2....2
//
//        where the `1`'s represent the inner section of obstacles and the `2`'s
//        represent the outer section of obstacles, which tiles to:
//
//                2....22....22....22....22....2
//                ..11....11....11....11....11..
//                ..11....11....11....11....11..
//                2....22....22....22....22....2
//                2....22....22....22....22....2
//                ..11....11....11....11....11..
//                ..11....11....11....11....11..
//                2....22....22....22....22....2
//                2....22....22....22....22....2
//                ..11....11....11....11....11..
//                ..11....11....11....11....11..
//                2....22....22....22....22....2
//                2....22....22....22....22....2
//                ..11....11....11....11....11..
//                ..11....11....11....11....11..
//                2....22....22....22....22....2
//                2....22....22....22....22....2
//                ..11....11....11....11....11..
//                ..11....11....11....11....11..
//                2....22....22....22....22....2
//
//        So, without assuming that the `1` set of rocks and the `2` set of rocks
//        are identical, we have alternating blocks of obstacles that form a
//        repeating ring-like pattern around the center. We can then proceed on
//        the assumption that these rings form the basis of a pattern in our
//        output, like:
//
//            - At (grid size / 2) steps, the elf's walking range encompasses the
//              `1` set of obstacles in the center.
//            - At (grid size / 2) + (grid size) steps, the elf's range now
//              encompasses the entire first ring of obstacles, including 5 groups
//              of `1`s and 4 groups of `2`s.
//            - At (grid size / 2) + (grid size * 2), the elf's range now
//              encompasses the first two rings of obstacles, including 13 groups
//              of `1`'s and 12 groups of `2`'s.
//
//        Because the number of obstacle groups included is scaling in some
//        predictable fashion with an increase in number of steps by (grid size),
//        we are able to manually find the first few results for the function
//        steps -> reachable tiles and extrapolate the answer from there.
//        */
//
//        // Taking the first four results of steps -> reachable tiles for
//        // step sizes at (grid size // 2), (grid size // 2) + (grid size),
//        // (grid size // 2) + (grid size * 2), (grid size // 2) + (grid size * 3)...
//        val stepIncrements = (0..3).map { incr ->
//            (map.grid.size / 2) + (map.grid.size * incr)
//        }
//        val reachableTileCounts = stepIncrements.map { map.reachableTiles(it).toLong() }
//            .toMutableList()
//
//        /*
//        For my input, I get [3755, 33494, 92811, 181706] for these first four
//        counts of reachable tiles. The increase isn't linear, however, if we
//        repeatedly take the differences between the values in sequence...
//
//            [3755, 33494, 92811, 181706]
//              [29739, 59317, 88895]
//                  [29578, 29578]
//                        [0]
//
//        It's Day 9! Or, rather, it's a polynomial sequence. There's definitely
//        math that can be done here to derive a formula for predicting the next
//        values based on this, but for my own sanity, I'm going to use the same
//        method from Day 9.
//         */
//
