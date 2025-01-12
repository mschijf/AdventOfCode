package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import java.lang.Math.floorMod

fun main() {
    Day20(test=false).showResult()
}

class Day20(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val list = inputLines.mapIndexed { index, it -> Pair(it.toLong(), index) }.toMutableList()

        mixList(list)
        return sumOfThreeValues(list).toString()
    }

    override fun resultPartTwo(): String {
        val decryptionKey = 811589153L
        val list = inputLines.mapIndexed { index, it -> Pair(it.toLong()*decryptionKey, index) }.toMutableList()

        repeat(10) {
           mixList(list)
        }
        return sumOfThreeValues(list).toString()
    }


    private fun mixList(list: MutableList<Pair<Long, Int>>) {
        val cycleLength = list.size
        for (index in list.indices) {
            val orgIndex = list.indexOfFirst { it.second == index}
            val orgValue = list[orgIndex].first
            val newPair = Pair(orgValue, index)

            list.removeAt(orgIndex)

            val newIndex = orgIndex + orgValue
            if (newIndex < 0) {
                list.add(floorMod(newIndex, cycleLength - 1), newPair)
            } else if (newIndex > cycleLength) {
                list.add(floorMod(newIndex, cycleLength - 1), newPair)
            } else if (newIndex == 0L) {
                list.add(newPair)
            } else {
                list.add(newIndex.toInt(), newPair)
            }
        }
    }

    private fun sumOfThreeValues(list: MutableList<Pair<Long, Int>>): Long {
        val cycleLength = list.size
        val index0 = list.indexOfFirst { it.first == 0L }
        val value1000 = list[(index0 + 1000) % cycleLength].first
        val value2000 = list[(index0 + 2000) % cycleLength].first
        val value3000 = list[(index0 + 3000) % cycleLength].first
        return (value1000 + value2000 + value3000)
    }


}

//----------------------------------------------------------------------------------------------------------------------
