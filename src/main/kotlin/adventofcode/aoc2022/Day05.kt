package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import java.util.*

fun main() {
    Day05(test=false).showResult()
}

class Day05(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val stackList = StackList(inputLines.filter{!it.startsWith("move") && it.isNotBlank()})
        inputLines
            .filter{it.startsWith("move")}
            .map { Action(it) }
            .forEach { stackList.executePartOne(it) }
        return stackList.toppers()
    }

    override fun resultPartTwo(): String {
        val stackList = StackList(inputLines.filter{!it.startsWith("move") && it.isNotBlank()})
        inputLines
            .filter{it.startsWith("move")}
            .map { Action(it) }
            .forEach { stackList.executePartTwo(it) }
        return stackList.toppers()
    }
}

//----------------------------------------------------------------------------------------------------------------------

class StackList(stackLines: List<String>) {
    private val stackList : List<Stack<String>> = List((stackLines.last().length+2) / 4) {Stack()}
    init {
        stackLines
            .dropLast(1)
            .reversed()
            .forEach {
                processStackLine(it)
            }
    }

    private fun processStackLine(stackLine: String) {
        for ((stackNumber, i) in (1 until stackLine.length step 4).withIndex()) {
            if (i < stackLine.length && stackLine[i] != ' ') {
                stackList[stackNumber].push(stackLine[i].toString())
            }
        }
    }

    fun executePartOne(action: Action) {
        repeat (action.moveCount) {
            stackList[action.to].push(stackList[action.from].pop())
        }
    }

    fun executePartTwo(action: Action) {
        val tmpStack = Stack<String>()
        repeat (action.moveCount) {
            tmpStack.push(stackList[action.from].pop())
        }
        repeat (action.moveCount) {
            stackList[action.to].push(tmpStack.pop())
        }
    }

    fun toppers() = stackList.joinToString("") { it.peek() }
}

//----------------------------------------------------------------------------------------------------------------------

class Action(private val actionString: String) {
    val moveCount = getNumberBetween("move", "from")
    val from = getNumberBetween("from", "to") - 1
    val to = getNumberAfter("to") - 1

    private fun getNumberBetween(left: String, right: String) : Int {
        val li = actionString.indexOf(left) + left.length
        val re = actionString.indexOf(right)
        return actionString.substring(li, re).trim().toInt()
    }

    private fun getNumberAfter(left: String) : Int {
        val li = actionString.indexOf(left) + left.length
        return actionString.substring(li).trim().toInt()
    }
}
