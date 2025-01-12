package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day12(test=false).showResult()
}

class Day12(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Hot Springs", hasInputFile = true) {


    override fun resultPartOne(): Any {
        val conditionRecordLines = inputLines.map { it.split("\\s+".toRegex()).first()}
        val conditionRecordGroups = inputLines.map { it.split("\\s+".toRegex()).last().split(",").map{it.toInt()}}
        return conditionRecordLines.mapIndexed { index, line ->  calculate(line, conditionRecordGroup = conditionRecordGroups[index])}.sum()
    }

    override fun resultPartTwo(): Any {
        val conditionRecordLines = inputLines.map { it.split("\\s+".toRegex()).first().copyFive("?")}
        val conditionRecordGroups = inputLines.map { it.split("\\s+".toRegex()).last().copyFive(",").split(",").map{it.toInt()}}
        return conditionRecordLines.mapIndexed { index, line ->  calculate(line, conditionRecordGroup = conditionRecordGroups[index])}.sum()
//        return execute(conditionRecordLines, conditionRecordGroups)
    }


    private fun String.copyFive(delimiter: String): String {
        return "$this$delimiter$this$delimiter$this$delimiter$this$delimiter$this"
    }

    //    private fun execute(conditionRecordLines: List<String>, conditionRecordGroups: List<List<Int>>): Long {
//        var sum = 0L
//        conditionRecordLines.forEachIndexed { index, s ->
//            val count = calculate(s, conditionRecordGroups[index])
//            println("$index -> $count")
//            sum += count
//        }
//        return sum
//    }
//
    private fun calculate(inputString: String, conditionRecordGroup: List<Int>,
                          cache:MutableMap<Pair<String, List<Int>>, Long> = mutableMapOf() ): Long {

        if (inputString.isEmpty()) {
            return if (conditionRecordGroup.isEmpty()) 1 else 0
        }

        if (conditionRecordGroup.isEmpty()) {
            return if (inputString.contains("#")) 0 else 1
        }

        val cacheKey = Pair(inputString, conditionRecordGroup)
        if (cache.contains(cacheKey)) {
            return cache[cacheKey]!!
        }


        val currentChar = inputString.first()
        val hekjesGroupSize = conditionRecordGroup.first()

        val startingWithDotOrUnknown = if (currentChar == '.' || currentChar == '?') {
            calculate(inputString.drop(1), conditionRecordGroup, cache)
        } else {
            0
        }

        val startingWithHekjeOrUnknown = if (currentChar == '#' || currentChar == '?') {
            if (inputString.impossibleToMake(hekjesGroupSize)) {
                0
            } else {
                calculate(inputString.drop(hekjesGroupSize + 1), conditionRecordGroup.drop(1), cache)
            }
        } else {
            0
        }

        val total = startingWithDotOrUnknown + startingWithHekjeOrUnknown
        cache[cacheKey] = total
        return total
    }

    private fun String.impossibleToMake(hekjesGroupSize: Int) : Boolean {
        return (hekjesGroupSize > this.length) ||
                (this.take(hekjesGroupSize).any { it == '.' }) ||
                (hekjesGroupSize < this.length  && this[hekjesGroupSize] == '#')
    }
}