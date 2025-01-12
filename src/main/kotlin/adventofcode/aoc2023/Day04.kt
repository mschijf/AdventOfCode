package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.mylambdas.substringBetween
import kotlin.math.min

fun main() {
    Day04(test=false).showResult()
}

class Day04(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Scratchcards", hasInputFile = true) {

    private val cardList = inputLines.map{Card.of(it)}

    override fun resultPartOne(): Any {
        return cardList.sumOf { it.determineWinning() }
    }

    override fun resultPartTwo(): Any {
        val countCards = Array<Int>(cardList.size){1}
        cardList.forEach { card ->
            repeat (min(card.winningNumbers(), card.id+cardList.size)) { index ->
                countCards[(card.id-1)+index+1] += countCards[(card.id-1)]
            }
        }

        return countCards.sum()
    }
}

data class Card(val id: Int, val winning: List<Int>, val having: List<Int>) {

    //assuming all numbers on one card are different, working with sets is ok
    fun winningNumbers(): Int {
        return winning.toSet().intersect(having.toSet()).count()
    }

    fun determineWinning(): Long {
        val winningNumbers = winningNumbers()
        return if (winningNumbers > 0) 1L shl (winningNumbers-1) else 0
    }


    companion object {
        //Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
        fun of(raw: String) =
            Card(
                id = raw.substringBetween("Card ", ": ").trim().toInt(),
                winning = raw.substringBetween(": ", " | ").trim().split("\\s+".toRegex()).map{it.toInt()},
                having = raw.substringAfter( " | ").trim().split("\\s+".toRegex()).map{it.toInt()}
            )
    }
}
