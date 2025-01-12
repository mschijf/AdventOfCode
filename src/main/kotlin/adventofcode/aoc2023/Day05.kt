package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.mylambdas.splitByCondition
import tool.mylambdas.substringBetween
import kotlin.math.min

fun main() {
    Day05(test=false).showResult()
}

// Bruteforce does work (see partTwoBruteForce)...
//   Result part 1: 388071289 (after 0.001 sec)
//   Result part 2: 84206669 (after 525.693 sec)
//

/*
Deel twee is niet de mooiste oplossing, maar wel een manier om het efficient te doen:
- bepaal van de laatste transformer ('humidity-to-location') wat de laagste destinationRange is. In principe zit daar
  de laagste location in.
- neem van die destinationRange, de bijbehorende sourceRange
- ga op basis van die sourceRange kijken welke destinationRanges er in 'temperature-to-humidity' zijn te vinden
  (dit wordt een lijst van ranges)
- neem daar weer de bijbehorende lijst van sourceRanges van
- gebruik die sourceRanges lijst weer als destination in 'light-to-temperature'
- etc. tot je aan het begin bent en een lijst van seed-ranges hebt.

-    met behulp daarvan gaan we voor ieder element van die ranges die ook voorkomt in de start-seed ranges het
     proces van deel 1 doen en bepalen zo de kleinste
 */

// (de laagste destination kan ook een 0..x range zijn, met x de laagste startpunt van alle destination ranges

class Day05(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="If You Give A Seed A Fertilizer", hasInputFile = true) {

    private val seedList = inputLines
        .first()
        .substringAfter("seeds: ")
        .split("\\s+".toRegex()).map{it.toLong()}

    private val seedRanges = seedList
        .chunked(2)
        .map{it[0]..it[0]+it[1]-1}

    private val transformerMap = inputLines
        .drop(2)
        .splitByCondition { it.isEmpty() }
        .map{ Transformer.of(it) }
        .associateBy { it.source }

    override fun resultPartOne(): Any {
        return seedList
            .map{ seedToLocation(it) }
            .min()
    }


    override fun resultPartTwo(): Any {
        val finalTransformer = transformerMap["humidity"]!!.fillEmptyRangesInDestination() //to add 0..x ranges in beginning

        finalTransformer.transformNumberList.sortedBy { it.destinationRange.first }.forEach {
            var sourceRangeList = listOf(it.sourceRange)
            sourceRangeList = sourceRangeList.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["temperature"]!!.transformNumberList )}
            sourceRangeList = sourceRangeList.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["light"]!!.transformNumberList )}
            sourceRangeList = sourceRangeList.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["water"]!!.transformNumberList )}
            sourceRangeList = sourceRangeList.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["fertilizer"]!!.transformNumberList )}
            sourceRangeList = sourceRangeList.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["soil"]!!.transformNumberList )}
            sourceRangeList = sourceRangeList.flatMap{ aRange -> destinationRangesToSource(aRange, transformerMap["seed"]!!.transformNumberList )}

//            println(sourceRangeList.intersect(seedRanges))
            var smallest = Long.MAX_VALUE
            sourceRangeList.intersect(seedRanges).forEach { seedRange ->
                var i = seedRange.first
                while (i <= seedRange.last) {
                    val xx = seedToLocation(i)
                    if (xx < smallest) {
//                        println("$i --> $xx")
                        smallest = xx
                    }
                    //smallest = min(smallest, seedToLocation(i))
                    i++
                }
            }
            if (smallest < Long.MAX_VALUE)
                return smallest
        }
        return -1L
    }

// 2604983879
// 2620224650

    private fun seedToLocation(seedNumber: Long): Long {
        var current = "seed"
        var currentNumber = seedNumber

        while (transformerMap.contains(current)) {
            currentNumber = transformerMap[current]!!.transform(currentNumber)
            current = transformerMap[current]!!.destination
        }
        return currentNumber
    }

    private fun destinationRangesToSource(inRange: LongRange, compareRanges: List<TransformNumber>): List<LongRange> {
        val result = mutableListOf<LongRange>()
        var start = inRange.first
        var end = inRange.last
        compareRanges.forEach {compareRange ->
            if (start <= end && start in compareRange.destinationRange && end in compareRange.destinationRange) {
                result.add(compareRange.sourceRange.first + (start-compareRange.destinationRange.first)..compareRange.sourceRange.first + (end-compareRange.destinationRange.first))
                return result
            }
            if (start <= end && start in compareRange.destinationRange) {
                result.add(compareRange.sourceRange.first + (start-compareRange.destinationRange.first)..compareRange.sourceRange.last)
                start = compareRange.destinationRange.last+1
            }
            if (start <= end && end in compareRange.destinationRange) {
                result.add(compareRange.sourceRange.first ..compareRange.sourceRange.first + (end-compareRange.destinationRange.first))
                end = compareRange.destinationRange.first-1
            }
        }
        if (start <= end)
            result.add(start..end)
        return result
    }

    private fun Transformer.fillEmptyRangesInDestination(): Transformer {
        val destinationResult = mutableListOf<LongRange>()
        val sourceResult = mutableListOf<LongRange>()
        var start = 0L
        this.transformNumberList.sortedBy { t -> t.destinationRange.first }.forEach { t ->
            if (start < t.destinationRange.first) {
                destinationResult += (start .. (t.destinationRange.first-1))
                sourceResult += (start .. (t.destinationRange.first-1))
            } else {
                destinationResult += t.destinationRange
                sourceResult += t.sourceRange
            }
            start = t.destinationRange.last+1
        }
        return Transformer(
            source=this.source,
            destination=this.destination,
            transformNumberList=List(destinationResult.size) { index -> TransformNumber(sourceResult[index], destinationResult[index] ) }
        )
    }

    //
    // brute-force works, surprisingly enough. It took about 9-10 minutes,
    // I run it, while developing a better alternative, but it found the result (way) before
    // my alternative solution was developed ;-)
    //
    private fun partTwoBruteforce(): Any {
        var smallest = Long.MAX_VALUE

        seedRanges.forEach { seedRange ->
            var i = seedRange.first
            while (i <= seedRange.last) {
                smallest = min(smallest, seedToLocation(i))
                i++
            }
        }
        return smallest
    }

}



data class Transformer(val source: String, val destination: String, val transformNumberList: List<TransformNumber>) {
    fun transform(from: Long) : Long {
        val transformNumber = transformNumberList.firstOrNull { from in it.sourceRange } ?: TransformNumber(from..from, from ..from)
        return transformNumber.destinationRange.first + (from - transformNumber.sourceRange.first)
    }

    companion object {
        fun of(raw: List<String>): Transformer {
            return Transformer(
                source = raw.first().substringBefore("-"),
                destination = raw.first().substringBetween("to-", " map:"),
                transformNumberList = raw.drop(1).map { TransformNumber.of(it) }
            )
        }
    }
}

data class TransformNumber (val sourceRange: LongRange, val destinationRange: LongRange) {
    companion object {
        fun of(raw: String): TransformNumber {
            val list = raw.split("\\s+".toRegex()).map {it.toLong() }
            val sourceStart = list[1]
            val destinationStart = list[0]
            val length = list[2]
            return TransformNumber(
                sourceRange = sourceStart..sourceStart+length-1,
                destinationRange = destinationStart .. destinationStart+length-1
            )
        }
    }
}

fun List<LongRange>.intersect(other: List<LongRange>): List<LongRange> {
    val result = mutableListOf<LongRange>()
    this.forEach { thisRange ->
        other.forEach {otherRange ->
            if (thisRange.first in otherRange) {
                if (thisRange.last in otherRange) {
                    result.add(thisRange.first..thisRange.last)
                } else {
                    result.add(thisRange.first..otherRange.last)
                }
            } else if (thisRange.last in otherRange) {
                result.add(otherRange.first..thisRange.last)
            } else {
                //do nothing
            }
        }
    }
    return result
}