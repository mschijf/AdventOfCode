package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import kotlin.math.max

fun main() {
    Day25(test=false).showResult()
}

class Day25(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val connections = inputLines
        .associate { it.substringBefore(": ") to it.substringAfter(": ").split(" ").toSet() }
    private val singleConnections = connections.flatMap{ (leftPart, rightSet) -> rightSet.map{ rightPart -> leftPart to rightPart}}
    private val allConnections = connections
        .flatMap{ (leftPart, rightSet) ->
            rightSet.flatMap{ rightPart ->
                setOf(leftPart to rightPart, rightPart to leftPart) }}
        .groupBy { it.first }
        .mapValues { it.value.map { it.second } }
    private val components = singleConnections.flatMap { setOf(it.first, it.second) }.toSet()

    /**
     * first find for each node, the minimal distance to all other nodes
     * determine the nodes with fewest distances to all other nodes (turned out to be a list of six nodes)
     * determine the connections between those nodes (turned out to be exactly three connections)
     * remove the connections from all connections and check the number of disconnected groups. (turned out to be two)
     * Done.
     */

    override fun resultPartOne(): Any {
        val distanceList = components.map{it to distanceToAll(it)}
        val minDistance = distanceList.minOf { it.second }
        val bestConnected = distanceList.filter { it.second == minDistance }.map { it.first }
        println(distanceList.sortedBy { it.second })
        println(bestConnected)

        val mostPromisingSet = mutableSetOf<Pair<String, String>>()
        for (i in 0 until bestConnected.size-1) {
            for (j in i+1 until bestConnected.size) {
                val connectionLR = Pair(bestConnected[i], bestConnected[j])
                val connectionRL = Pair(bestConnected[i], bestConnected[j])
                if (connectionLR in singleConnections || connectionRL in singleConnections) {
                    mostPromisingSet += connectionRL
                    mostPromisingSet += connectionLR
                    println(connectionLR)
                }
            }
        }

        val result = disconnectedGroupCount(singleConnections - mostPromisingSet)
        return result[0].size * result[1].size
    }

    override fun resultPartTwo(): Any {
        return "no-part2 on December 25"
    }

    private fun disconnectedGroupCount(connections: List<Pair<String, String>>): List<Set<String>> {
        val result = mutableListOf<MutableSet<String>>()

        connections.forEach { (leftPart, rightPart) ->
            val matchingGroups = result.filter { aGroup -> leftPart in aGroup || rightPart in aGroup }
            when (matchingGroups.size) {
                0 -> result.add(mutableSetOf(leftPart, rightPart))
                1 -> {
                    matchingGroups.first().add(leftPart)
                    matchingGroups.first().add(rightPart)
                }

                2 -> {
                    result.remove(matchingGroups.first())
                    result.remove(matchingGroups.last())
                    result.add((matchingGroups.first() + matchingGroups.last()).toMutableSet())
                }

                else -> throw Exception("Unexpected")
            }
        }
        return result
    }

    private fun distanceToAll(from: String): Int {
        var max = 0
        val visited = mutableSetOf<String>(from)
        val queue = ArrayDeque<Pair<String, Int>>()
        queue.add(Pair(from, 0))
        while (queue.isNotEmpty()) {
            val (current, stepsDone) = queue.removeFirst()
            max = max(stepsDone, max)
            allConnections[current]!!.filter{it !in visited}.forEach { neighbor->
                visited += neighbor
                queue.add(Pair(neighbor, stepsDone+1))
            }
        }
        return max
    }
}


