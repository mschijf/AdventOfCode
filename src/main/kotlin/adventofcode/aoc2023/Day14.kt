package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day14(test=false).showResult()
}

/**
 * Note: a faster implementation is below the first solution
 */


class Day14(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Parabolic Reflector Dish", hasInputFile = true) {


//    private val grid = inputAsGrid()
//    private val cubeRocks = grid.filterValues{it == '#'}.keys
//    private val roundedRocks = grid.filterValues{it == 'O'}.keys
//    private val rowCount = inputLines.size
//    private val colCount = inputLines.firstOrNull()?.length ?:0
//
//    override fun resultPartOne(): Any {
//        return roundedRocks.rollNorth().platformLoad()
//    }
//
//    private fun absoluteTop(from: Point): Int {
//        return cubeRocks.filter{it.x == from.x && it.y < from.y}.maxOfOrNull { it.y }?:-1
//    }
//    private fun absoluteBottom(from: Point): Int {
//        return cubeRocks.filter{it.x == from.x && it.y > from.y}.minOfOrNull { it.y }?:rowCount
//    }
//    private fun absoluteLeft(from: Point): Int {
//        return cubeRocks.filter{it.y == from.y && it.x < from.x}.maxOfOrNull { it.x }?:-1
//    }
//    private fun absoluteRight(from: Point): Int {
//        return cubeRocks.filter{it.y == from.y && it.x > from.x}.minOfOrNull { it.x }?:colCount
//    }
//
//    private fun Set<Point>.rollNorth(): Set<Point> {
//        return this.map { from ->
//            val absTop = absoluteTop(from)
//            val countAbove = this.count{it.x == from.x && it.y < from.y && absTop < it.y }
//            val freeSpace = (from.y-absTop-1)-countAbove
//            pos(from.x, from.y - freeSpace)
//        }.toSet()
//    }
//
//
//    private fun Set<Point>.rollSouth(): Set<Point> {
//        return this.map { from ->
//            val absBottom = absoluteBottom(from)
//            val countBelow = this.count { it.x == from.x && it.y > from.y && absBottom > it.y }
//            val freeSpace = (absBottom - from.y - 1) - countBelow
//            pos(from.x, from.y + freeSpace)
//        }.toSet()
//    }
//
//
//    private fun Set<Point>.rollWest(): Set<Point> {
//        return this.map { from ->
//            val absLeft = absoluteLeft(from)
//            val countLeft = this.count { it.y == from.y && it.x < from.x && absLeft < it.x }
//            val freeSpace = (from.x - absLeft - 1) - countLeft
//            pos(from.x - freeSpace, from.y)
//        }.toSet()
//    }
//
//    private fun Set<Point>.rollEast(): Set<Point> {
//        return this.map { from ->
//            val absRight = absoluteRight(from)
//            val countRight = this.count { it.y == from.y && it.x > from.x && absRight > it.x }
//            val freeSpace = (absRight - from.x - 1) - countRight
//            pos(from.x + freeSpace, from.y)
//        }.toSet()
//    }
//
//    private fun Set<Point>.cycle(): Set<Point> {
//        return this.rollNorth().rollWest().rollSouth().rollEast()
//    }
//
//    private fun Set<Point>.platformLoad(): Int {
//        return this.sumOf { rowCount - it.y }
//    }
//
//    /**
//     * Mooier leesbaar, maar wel een stuk trager
//     */
//    private val history : MutableList<Set<Point>> = mutableListOf()
//    private val loadHistory : MutableList<Int> = mutableListOf()
//    override fun resultPartTwo(): Any {
//        var platform = roundedRocks
//        history.add(platform)
//        loadHistory.add(platform.platformLoad())
//        repeat(1_000_000_000) {
//            platform = platform.cycle()
//            if (platform in history) {
//                val index = history.indexOf(platform)
//                val cycleTime = (it+1-index)
//                val remainder = (1_000_000_000 - (index)) % cycleTime
//                val totalRuns = index + remainder
//                println("REPEATER after $it at $index, cycletime: $cycleTime, totalruns = $totalRuns")
//                return (loadHistory[index+remainder])
//            }
//            println(it)
//            history.add(platform)
//            loadHistory.add(platform.platformLoad())
//        }
//        return -1
//    }

    //-----------------------------------------------------------------------------------------------------------------

    //-----------------------------------------------------------------------------------------------------------------

    /**
     * snellere oplossing
     */

    private val platform = inputLines.map { line -> line.map { ch ->  ch}.toMutableList() }

    override fun resultPartOne(): Any {
        rollNorth()
        return platformTotalLoad()
    }

    override fun resultPartTwo(): Any {
        return runTillRepetition()
    }

    private fun runTillRepetition(): Int {
        val history : MutableList<Int> = mutableListOf()
        val loadHistory : MutableList<Int> = mutableListOf()
        history.add(platformValue())
        loadHistory.add(platformTotalLoad())
        repeat(1_000_000_000) {
            cycle()
            if (platformValue() in history) {
                val index = history.indexOf(platformValue())
                val cycleTime = (it+1-index)
                val restje = (1_000_000_000 - (index)) % cycleTime
                val totalRuns = index + restje
//                println("REPEATER after $it at $index, cycletime: $cycleTime, totalruns = $totalRuns")
//                println(loadHistory[index+restje])
                return platformTotalLoad()
            }
            history.add(platformValue())
            loadHistory.add(platformTotalLoad())
//            if (it % 1_000_000 == 0)
//                println("done $it cycles" )
        }
        return -1
    }

    private fun platformValue() : Int {
        var value = 0
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[row][col] == 'O')
                    value += platform.size * row + col
            }
        }
        return value
    }

    private fun cycle() {
        rollNorth()
        rollWest()
        rollSouth()
        rollEast()
    }

    private fun rollNorth() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[row][col] == 'O')
                    moveCubeNorth(row, col)
            }
        }
    }

    private fun rollSouth() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                if (platform[platform.size - row - 1][col] == 'O')
                    moveCubeSouth(platform.size - row - 1, col)
            }
        }
    }

    private fun rollEast() {
        for (col in platform[0].indices) {
            for (row in platform.indices) {
                if (platform[row][platform[0].size - col - 1] == 'O')
                    moveCubeEast(row, platform[0].size - col - 1)
            }
        }
    }

    private fun rollWest() {
        for (col in platform[0].indices) {
            for (row in platform.indices) {
                if (platform[row][col] == 'O')
                    moveCubeWest(row, col)
            }
        }
    }


    private fun moveCubeNorth(row: Int, col: Int) {
        var walk = row-1
        while (walk >= 0 && platform[walk][col] == '.') {
            platform[walk+1][col] = '.'
            platform[walk][col] = 'O'
            walk--
        }
    }

    private fun moveCubeSouth(row: Int, col: Int) {
        var walk = row+1
        while (walk < platform.size && platform[walk][col] == '.') {
            platform[walk-1][col] = '.'
            platform[walk][col] = 'O'
            walk++
        }
    }

    private fun moveCubeWest(row: Int, col: Int) {
        var walk = col-1
        while (walk >= 0 && platform[row][walk] == '.') {
            platform[row][walk+1] = '.'
            platform[row][walk] = 'O'
            walk--
        }
    }

    private fun moveCubeEast(row: Int, col: Int) {
        var walk = col+1
        while (walk < platform[row].size && platform[row][walk] == '.') {
            platform[row][walk-1] = '.'
            platform[row][walk] = 'O'
            walk++
        }
    }

    private fun platformTotalLoad() : Int {
        val tl = platform.mapIndexed { index, chars ->
            chars.count { ch -> ch == 'O' } * (platform.size-index)
        }.sum()
        return tl
    }

    fun printPlatform() {
        for (row in platform.indices) {
            for (col in platform[row].indices) {
                print(platform[row][col])
            }
            println()
        }
        println()
    }

}


