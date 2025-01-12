package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import tool.math.getCombinationList
import java.util.*

fun main() {
    Day16(test=false).showResult()
}

class Day16(test: Boolean) : PuzzleSolverAbstract(test) {
    private val valveList = inputLines.map{Valve(it)}
    private val startValve = valveList.find { valve -> valve.name == "AA" }!!
    private val valveListEssential = valveList.filter{it.rate > 0}
    private val distanceMap = makeDistanceMap()

    override fun resultPartOne(): String {
        val optimal = solver(startValve, valveListEssential.toSet(), 0, 30)
        return optimal.toString()
    }

    override fun resultPartTwo(): String {
        var maxResult = 0
        for (setSize in 0 .. valveListEssential.size / 2) {
            val allCombinationsList = getCombinationList(valveListEssential, setSize)
            print("set-size $setSize - total combinations: ${allCombinationsList.size}. Progress (per 100)")
            allCombinationsList.forEachIndexed {index, combination ->
                if (index % 100 == 0) {
                    print(".")
                }
                val valveSetMan = combination.map { it }.toSet()
                val valveSetElephant = valveListEssential.minus(valveSetMan).toSet()

                val optimalMan = solver(startValve, valveSetMan, 0, 26)
                val optimalElephant = solver(startValve, valveSetElephant, 0, 26)

                val totalResult = optimalMan + optimalElephant
                if (totalResult > maxResult) {
                    maxResult = totalResult
                }
            }
            println()
        }
        return maxResult.toString()
    }

    private fun solver(previousValve: Valve, toBeVisited: Set<Valve>, minutesPassed: Int, maxSeconds: Int): Int {
        if (toBeVisited.isEmpty() || minutesPassed >= maxSeconds) {
            return 0
        }

        var maxValue = 0
        for (nextValve in toBeVisited) {
            // om hier te komen heeft tijd gekost, namelijk distance in minutes
            // om de valve te openen, kost ook tijd: 1
            val distanceInMinutes = distanceMap[previousValve]!![nextValve]!!
            val totalMinutesPassed = minutesPassed + distanceInMinutes + 1
            if (totalMinutesPassed < maxSeconds) {
                val pressureReleasedHere = (maxSeconds - totalMinutesPassed) * nextValve.rate
                val totalPressureAfterHere = solver(
                    nextValve,
                    toBeVisited - nextValve,
                    minutesPassed + distanceInMinutes + 1,
                    maxSeconds
                )
                if (totalPressureAfterHere + pressureReleasedHere > maxValue) {
                    maxValue = totalPressureAfterHere + pressureReleasedHere
                }
            }
        }
        return maxValue
    }

    private fun makeDistanceMap(): Map<Valve, Map<Valve, Int>> {
        val distanceMap = mutableMapOf<Valve, MutableMap<Valve, Int>>()
        valveListEssential.plus(startValve).forEach {fromValve ->
            distanceMap[fromValve] = mutableMapOf()
            valveListEssential.forEach {toValve ->
                distanceMap[fromValve]!![toValve] = getShortestPathSteps(fromValve, toValve)
            }
        }
        return distanceMap
    }

    private fun getShortestPathSteps(fromValve: Valve, toValve: Valve): Int {
        val queue : Queue<Valve> = LinkedList()
        val dMap = mutableMapOf<String, Int>()
        queue.add(fromValve)
        dMap[fromValve.name] = 0
        while (queue.isNotEmpty()) {
            val current = queue.remove()
            if (current == toValve)
                return dMap[current.name]!!

            current.neighbourValveNameList.forEach {nbValveName ->
                val nbValve = valveList.find { valve -> valve.name == nbValveName }!!
                if (!dMap.contains(nbValve.name) ) {
                    dMap[nbValve.name] = dMap[current.name]!! + 1
                    queue.add(nbValve)
                }
            }
        }
        return 99999999
    }

    private fun print(dMap: Map<Valve, Map<Valve, Int>>) {
        dMap.keys.forEach {fromValve->
            print("${fromValve.name} -->  ")
            dMap[fromValve]!!.forEach { toValve ->
                print(" ${toValve.key.name} ${dMap[fromValve]!![toValve.key]}")
            }
            println()
        }
    }

}


//----------------------------------------------------------------------------------------------------------------------

class Valve(inputStr: String) { //inputStr = "Valve AA has flow rate=0; tunnels lead to valves DD, II, BB"
    val name: String = inputStr
        .substringAfter("Valve ")
        .substringBefore("has flow rate")
        .trim()
    val rate: Int = inputStr
        .substringAfter("has flow rate=")
        .substringBefore(";")
        .trim().toInt()

    val neighbourValveNameList : List<String> = inputStr.replace("valves", "valve")
        .substringAfter(" to valve ")
        .split(", ")

    fun print() {
        println("VALVE $name: rate: $rate , neighbours: $neighbourValveNameList")
    }
}
