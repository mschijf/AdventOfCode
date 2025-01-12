package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day13(test=false).showResult()
}

class Day13(test: Boolean) : PuzzleSolverAbstract(test) {

    override fun resultPartOne(): String {
        val list = inputLines
            .filter {it.isNotBlank() }
            .chunked(2)
            .map {TreeNode(it[0], it[1])}

//        list.forEach {
//            it.print();
//            val isOk = it.inRightOrder()
//            println("Order: $isOk")
//        }

        return list
            .withIndex()
            .filter{it.value.inRightOrder()}
            .sumOf { (it.index+1) }
            .toString()
    }

    override fun resultPartTwo(): String {
        val list = inputLines
            .filter {it.isNotBlank() }
            .chunked(2)
            .map {TreeNode(it[0], it[1])}
            .map {listOf(it.left, it.right)}
            .flatten()
            .toMutableList()

        val tn = TreeNode("[[2]]","[[6]]")
        list.add(tn.left)
        list.add(tn.right)

        bubbleSort(list)
//        list.forEach { it.print(); println() }
        return list
            .withIndex()
            .filter {it.value.compare(tn.left) == 0 || it.value.compare(tn.right) == 0}
            .map {(it.index+1)}
            .reduce{acc, i ->  acc * i }
            .toString()
    }

    private fun bubbleSort(list: MutableList<ListElement>) {
        for (i in 0 until list.size-1) {
            for (j in i until list.size) {
                val cmp = list[i].compare(list[j])
                if (cmp == 1) {
                    val tmp = list[i]
                    list[i] = list[j]
                    list[j] = tmp
                 }
            }
        }
    }

}

//----------------------------------------------------------------------------------------------------------------------

interface Element {
    fun print()
    fun compare(other: Element): Int
}

class NumberElement(val number: Int): Element {
    override fun print() {
        print ("${toString()} ")
    }

    override fun compare(other: Element): Int {
        if (other is NumberElement) {
            return if (this.number < other.number) -1 else if (this.number == other.number) 0 else 1
        }
        return ListElement(listOf(this)).compare(other)
    }

    override fun toString(): String {
        return number.toString()
    }
}

class ListElement(private val aList: List<Element>): Element {
    override fun print() {
        print("[")
        aList.forEach {
            it.print()
        }
        print("]")
    }

    override fun compare(other: Element): Int {
        val compareOther = if (other is ListElement) other else ListElement(listOf(other))
        aList.forEachIndexed { index, elt ->
            if (index >= compareOther.aList.size) { //Right side ran out of items, so inputs are not in the right order
                return 1
            }
            val cmp = elt.compare(compareOther.aList[index])
            if (cmp == -1) {
                return -1
            } else if (cmp == 1) {
                return 1
            }
        }
        return if (this.aList.size < compareOther.aList.size) { //Left side ran out of items, so inputs are in the right order
            -1
        } else {
            0
        }
    }
}


class TreeNode(s0: String, s1: String) {
    val left : ListElement = ListElement(makeList(s0.substringAfter("[").substringBeforeLast("]")))
    val right: ListElement = ListElement(makeList(s1.substringAfter("[").substringBeforeLast("]")))

    fun inRightOrder(): Boolean {
        return left.compare(right) <= 0
    }

    private fun makeList(s: String): List<Element> {
        val result = mutableListOf<Element>()
        var index = 0
        var numberStr = ""
        while (index < s.length) {
            val letter = s[index]
            if (letter == ',') {
                if (numberStr.isNotBlank()) {
                    result.add(NumberElement(numberStr.toInt()))
                    numberStr = ""
                }
                index++
            } else if (letter == '[') {
                val endSubListIndex = findClosingBracket(s, index)
                result.add(ListElement(makeList(s.substring(index+1, endSubListIndex))))
                index = endSubListIndex+1
            } else {
                numberStr += letter
                index++
            }
        }
        if (numberStr.isNotBlank()) {
            result.add(NumberElement(numberStr.toInt()))
        }
        return result
    }

    private fun findClosingBracket(s: String, fromIndex: Int): Int {
        var level = 0
        var index = fromIndex
        while (index < s.length) {
            val letter = s[index]
            if (letter == '[') {
                level++
            } else if ( letter == ']') {
                level--
                if (level == 0) {
                    return index
                }
            }
            index++
        }
        return -1
    }

    fun print() {
        left.print()
        println()
        right.print()
        println()
    }
}
