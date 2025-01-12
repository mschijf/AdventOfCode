package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.mylambdas.splitByCondition
import tool.mylambdas.substringBetween
import kotlin.collections.get
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day19(test=false).showResult()
}

class Day19(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val workflowList = inputLines.splitByCondition { it.isEmpty() }.first().map { Workflow.of(it) }
    private val partRatingList = inputLines.splitByCondition { it.isEmpty() }.last().map{it.toPartsMap()}

    override fun resultPartOne(): Any {
        return partRatingList.filter { it.isAccepted() }.map {it.values.sum()}.sum()
    }

    override fun resultPartTwo(): Any {
        val initRanges = ("xmas").associate { it.toString() to 1..4000 }
        val result = allCombinations("in", initRanges.toMutableMap())
        return result
    }

    private fun Map<String, Int>.isAccepted(): Boolean {
        var result = "in"
        while (result != "A" && result != "R") {
            val wf = workflowList.first { it.name == result }
            result = wf.getResult(this)
        }
        return result == "A"
    }

    private fun Map<String, IntRange>.product(): Long {
        return this.values.fold(1L){acc, range -> acc * (range.last - range.first + 1) }
    }

    private fun allCombinations(name: String, rangeMap: Map<String, IntRange>): Long {
        if (name == "A") {
            return rangeMap.product()
        }
        if (name == "R") {
            return 0
        }

        val workFlow = workflowList.first { it.name == name }
        var sum = 0L
        val falseRangeMap = rangeMap.toMutableMap()
        workFlow.ruleSet.forEach { rule->
            val trueRangeMap = falseRangeMap.toMutableMap()
            when (rule.compareType) {
                CompareType.SMALLER -> {
                    trueRangeMap[rule.part!!] = trueRangeMap[rule.part]!!.first ..  min(trueRangeMap[rule.part]!!.last, rule.conditionNumber!!-1)
                    falseRangeMap[rule.part] =  max(falseRangeMap[rule.part]!!.first, rule.conditionNumber) .. falseRangeMap[rule.part]!!.last
                    sum += allCombinations(rule.result, trueRangeMap)
                }
                CompareType.BIGGER -> {
                    trueRangeMap[rule.part!!] = max(trueRangeMap[rule.part]!!.first, rule.conditionNumber!! + 1)..trueRangeMap[rule.part]!!.last
                    falseRangeMap[rule.part] = falseRangeMap[rule.part]!!.first..min(falseRangeMap[rule.part]!!.last, rule.conditionNumber)
                    sum += allCombinations(rule.result, trueRangeMap)
                }
                CompareType.NONE -> {
                    sum += allCombinations(rule.result, falseRangeMap)
                }
            }
        }
        return sum
    }


}

//======================================================================================================================

//        {x=787,m=2655,a=1222,s=2876}
fun String.toPartsMap(): Map<String, Int> {
    val list = this.drop(1).dropLast(1).split(",")
    return list.associate{it.substringBefore("=") to it.substringAfter("=").toInt()}
}


data class Workflow(val name: String, val ruleSet: List<Rule> ) {
    companion object {
//        px{a<2006:qkq,m>2090:A,rfg}

        fun of(raw: String): Workflow {
            return Workflow(
                name = raw.substringBefore("{"),
                ruleSet = raw.substringBetween("{","}").split(",").map{Rule.of(it)}
            )
        }
    }

    fun getResult(parts: Map<String, Int>): String {
        ruleSet.forEach {rule ->
            val partValue = parts[rule.part]
            when(rule.compareType) {
                CompareType.NONE -> return rule.result
                CompareType.BIGGER -> if (partValue!! > rule.conditionNumber!!)
                    return rule.result
                CompareType.SMALLER -> if (partValue!! < rule.conditionNumber!!)
                    return rule.result
            }
        }
        throw Exception("none of the rules in ruleset is matching")
    }
}

enum class CompareType {BIGGER, SMALLER, NONE}
data class Rule(val part: String?, val compareType: CompareType, val conditionNumber: Int?, val result: String) {
    companion object {
        //one of : a<2006:qkq,m>2090:A,rfg
        fun of(raw: String): Rule {
            if (raw.contains("<")) {
                return Rule (
                    part = raw.substringBefore("<"),
                    compareType = CompareType.SMALLER,
                    conditionNumber = raw.substringBetween("<", ":").toInt(),
                    result = raw.substringAfter(":")
                )
            } else if (raw.contains(">")) {
                return Rule (
                    part = raw.substringBefore(">"),
                    compareType = CompareType.BIGGER,
                    conditionNumber = raw.substringBetween(">", ":").toInt(),
                    result = raw.substringAfter(":")
                )
            } else {
                return Rule(null, CompareType.NONE, conditionNumber = null, result=raw)
            }
        }
    }

}


