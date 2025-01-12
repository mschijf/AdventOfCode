package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day07(test=false).showResult()
}

class Day07(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Camel Cards", hasInputFile = true) {


    override fun resultPartOne(): Any {
        val handList= inputLines.map{ Hand.of(it, false) }
        return handList
            .sortedWith(compareBy { it.handValue })
            .mapIndexed { index, hand -> (index+1) * hand.bid }
            .sum()
    }

    override fun resultPartTwo(): Any {
        val handList= inputLines.map{ Hand.of(it, true) }
        return handList
            .sortedWith(compareBy { it.handValue })
            .mapIndexed { index, hand -> (index+1) * hand.bid }
            .sum()
    }


}

data class Hand(val cards: List<Char>, val handValue: Int, val bid: Int) {
    companion object {
        fun of (raw: String, useJoker: Boolean): Hand {
            val cardList = raw.substringBefore(" ").toList()
            return Hand(
                cards=cardList,
                handValue=if (useJoker) cardList.handValueByReplacingJokers() else cardList.handValueWithoutUsingJokers(),
                bid=raw.substringAfter(" ").toInt(),
            )
        }

        //A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
        private fun cardValue(card: Char, useJoker: Boolean): Int {
            return when(card) {
                'A' -> 14
                'K' -> 13
                'Q' -> 12
                'J' -> if (useJoker) 0 else 11
                'T' -> 10
                else -> card - '0'
            }
        }

        private fun List<Char>.cardListValue(useJoker: Boolean): Int {
            return this.fold(0) { acc, card -> acc*15 + cardValue(card, useJoker) }
        }

        private fun totalHandValue(orderedList: List<Char>, orgCardList: List<Char>, useJoker: Boolean) : Int {
            val cardListValue = orgCardList.cardListValue(useJoker)
            return when  {
                orderedList.fiveOfAKind() -> 6_000_000 + cardListValue
                orderedList.fourOfAKind() -> 5_000_000 + cardListValue
                orderedList.fullHouse() -> 4_000_000 + cardListValue
                orderedList.threeOfAKind() -> 3_000_000 + cardListValue
                orderedList.twoPair() -> 2_000_000 + cardListValue
                orderedList.onePair() -> 1_000_000 + cardListValue
                orderedList.highCard() -> 0 + cardListValue
                else -> throw Exception ("not possible")
            }
        }

        private fun List<Char>.handValueWithoutUsingJokers(): Int {
            val orderedList = this.sortedWith(compareBy { cardValue(it, useJoker = false) })
            return totalHandValue(orderedList, this, useJoker=false)
        }

        private fun handValueUsingJokers(orgCardList:List<Char>, jokerCardList: List<Char>): Int {
            val orderedList = jokerCardList.sortedWith(compareBy { cardValue(it, useJoker=true) })
            return totalHandValue(orderedList, orgCardList, useJoker = true)
        }


        private fun List<Char>.fiveOfAKind(): Boolean {
            return (this.distinct().count() == 1) && (
                    this[0] == this[1] && this[0] == this[2] && this[0] == this[3] && this[0] == this[4]
                    )
        }

        private fun List<Char>.fourOfAKind(): Boolean {
            return (this.distinct().count() == 2) && (
                    (this[0] == this[1] && this[0] == this[2] && this[0] == this[3]) ||
                            (this[1] == this[2] && this[1] == this[3] && this[1] == this[4])
                    )
        }

        private fun List<Char>.fullHouse(): Boolean {
            return (this.distinct().count() == 2) && (
                    (this[0] == this[1] && this[2] == this[3] && this[2] == this[4]) ||
                            (this[0] == this[1] && this[0] == this[2] && this[3] == this[4])
                    )
        }

        private fun List<Char>.threeOfAKind(): Boolean {
            return (this.distinct().count() == 3) && (
                    (this[0] == this[1] && this[0] == this[2]) ||
                            (this[1] == this[2] && this[1] == this[3]) ||
                            (this[2] == this[3] && this[2] == this[4])
                    )
        }

        private fun List<Char>.twoPair(): Boolean {
            return (this.distinct().count() == 3) && (
                    (this[0] == this[1] && this[2] == this[3]) ||
                            (this[0] == this[1] && this[3] == this[4]) ||
                            (this[1] == this[2] && this[3] == this[4])
                    )
        }

        private fun List<Char>.onePair(): Boolean {
            return (this.distinct().count() == 4) && (
                    (this[0] == this[1]) ||
                            (this[1] == this[2]) ||
                            (this[2] == this[3])  ||
                            (this[3] == this[4])
                    )
        }

        private fun List<Char>.highCard(): Boolean {
            return (this.distinct().count() == 5)
        }

        private val otherCards = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2')
        private fun List<Char>.handValueByReplacingJokers(currentHand: String="", index: Int=0): Int {
            if (index > 4) {
                return handValueUsingJokers(this, currentHand.toList())
            }
            return if (this[index] == 'J') {
                otherCards.maxOf{
                    handValueByReplacingJokers(currentHand+it, index+1)
                }
            } else {
                handValueByReplacingJokers(currentHand+this[index], index+1)
            }
        }

    }

}


