package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.threedimensional.Point3D
import tool.coordinate.twodimensional.Rectangle
import tool.coordinate.twodimensional.pos

fun main() {
    Day22(test=false).showResult()
}

//45214 --> wrong

//95059 --> goede antwoord

/**
 * lastige opgave, had vast makkelijker gekund. Code is waarschijnlijk niet al te makkelijk leesbaar.
 * Moet nog veel gerefactored worden. Kost ook zo'n 5 seconden om te runnen.
 *
 * korte uitleg: brick Class (een balk)
 * heightMap: per hoogte houden we bij wat de rechthoek is van de bovenkant van brick (waarop anderen kunnen steunen)
 * supportMap is een map waarin per brick een lijst is opgenomen met de bricks die het ondersteunt)
 * supporteByMap is precies andersom: per brick een lijst van bricks waarop het leunt
 *
 * hopelijk is de rest van de code duidelijk, want ik heb ven geen zin om eer comments toe te voegen
 */

class Day22(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val brickList = inputLines.mapIndexed{index, s ->  Brick.of(index, s)}
    private val heightMap: Map<Int, MutableList<Brick>>// = mapOf<Int, MutableList<Brick>>()
    private val supportMap: Map<Brick, MutableList<Brick>>// = brickList.associateWith{mutableListOf<Brick>()}
    private val supportedByMap: Map<Brick, MutableList<Brick>>// = brickList.associateWith{mutableListOf<Brick>()}

    init {
        val result = brickList.fallDown()
        heightMap = result.first
        supportMap = result.second

        supportedByMap = brickList.map { it to mutableListOf<Brick>() }.toMap().toMutableMap()
        supportMap.forEach { (keyBrick, supportsList) ->
            supportsList.forEach { supportedBrick ->
                supportedByMap[supportedBrick]!!.add(keyBrick)
            }
        }
    }



    override fun resultPartOne(): Any {
        return safeDeletes().size
    }

    override fun resultPartTwo(): Any {
        return brickList.sumOf{countRemovals(it)}
    }

    private fun countRemovals(brick: Brick): Int {
        var succ = determineSuccessors(brick)
        var newSucc = succ.filter{rc -> (supportedByMap[rc]!!-succ).isEmpty()}.toSet() + brick
        while (succ.size != newSucc.size) {
            succ = newSucc
            newSucc = succ.filter{rc -> (supportedByMap[rc]!!-succ).isEmpty()}.toSet() + brick
        }

        var c = 0
        (succ-brick).forEach {rc ->
            if ((supportedByMap[rc]!! - succ).isEmpty()) {
                c++
            }
        }
        return c
    }



    private fun determineSuccessors(brick: Brick): Set<Brick> {

        val result = mutableSetOf<Brick>()
        supportMap[brick]!!.forEach {
            result.addAll(determineSuccessors(it))
        }
        return result + brick

    }



    private fun List<Brick>.fallDown(): Pair<Map<Int, MutableList<Brick>>, Map<Brick, MutableList<Brick>>>  {
        val heightMap = mutableMapOf<Int, MutableList<Brick>>()
        val supportMap = this.associateWith{mutableListOf<Brick>()}

        this.sortedBy { it.p2.z }.forEach {aBrick ->
            var nothingHappened = true
            for (z in aBrick.p2.z downTo 1) {
                if (heightMap.contains(z)) {
                    val rectList = heightMap[z]!!
                    val overlappedWith = aBrick.rectangle.overlaps(rectList.map{it})
                    if (overlappedWith.isNotEmpty()) {
                        if (!heightMap.contains(z+aBrick.height)) {
                            heightMap[z+aBrick.height] = mutableListOf()
                        }
                        heightMap[z+aBrick.height]!!.add(aBrick)
                        nothingHappened = false

                        overlappedWith.forEach { overlapper -> supportMap[overlapper]!!.add(aBrick) }
                        break
                    }
                }
            }
            if (nothingHappened) {
                if (!heightMap.contains(aBrick.height)) {
                    heightMap[aBrick.height] = mutableListOf()
                }
                heightMap[aBrick.height]!!.add(aBrick)
            }
        }
        return Pair(heightMap, supportMap)
    }

    private fun safeDeletes(): Set<Brick> {
        val result = mutableListOf<Brick>()
        heightMap.values.forEach {brickList ->
            brickList.forEach {brickOnHeight ->
                if (brickOnHeight.canBeDeletedSafely(brickList)) {
                    result.add(brickOnHeight)
                }
            }
        }
        return result.toSet()
    }

    private fun Brick.canBeDeletedSafely(levelBrickList: List<Brick>): Boolean {
        if (supportMap[this]!!.isEmpty()) {
            return true
        } else {
            val union = mutableSetOf<Brick>()
            (levelBrickList-this).forEach {
                union += supportMap[it]!!.toSet()
            }
            if (union.containsAll(supportMap[this]!!)) {
                return true
            }
        }
        return false
    }



}

fun Rectangle.overlaps(rectList: List<Brick>): List<Brick> {
    val result = mutableListOf<Brick>()
    rectList.forEach { other->
        if (this.overlaps(other.rectangle))
            result.add(other)
    }
    return result
}

data class Brick(val name: String, val p1: Point3D, val p2: Point3D) {
    val rectangle = Rectangle(pos(p1.x, p1.y), pos(p2.x, p2.y) )
    val height = p1.z - p2.z + 1

    override fun toString(): String {
        return name
    }
    companion object {
        fun of(index: Int, raw: String): Brick {
            val points = listOf(Point3D.of(raw.split("~")[0]), Point3D.of(raw.split("~")[1]))
            if (points[0].z >= points[1].z) {
                return Brick(
                    name = ('A' + index).toString(),
                    p1 = points[0],
                    p2 = points[1]
                )
            } else {
                return Brick(
                    name = ('A' + index).toString(),
                    p1 = points[1],
                    p2 = points[0]
                )
            }
        }
    }
}



