package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.twodimensional.*
import tool.mylambdas.substringBetween
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day18(test=false).showResult()
}

/**
 * Twee 'slimme' stappen nodig voor deel 2. Deel 1 is evt brute force op te lossen.
 **
 * Slimme stap 1:
 * Ik trek een lijn om de gegraven gaten heen. als je dit zo doet, dan hebben straks alle rechthoeken (zie slimme stap 2)
 * hun eigen gaten en is er geen overlap van gaten. Dit telt makkelijker bij elkaar op.
 * Dit lijntje trekken is een lastige, maar wordt in 'digIt' gedaan. In principe wordt er per lijn gekeken of je 'buitenom'
 *    (polygon deel is nog 'concaaf')moet of 'binnendoor' (polygondeel is 'convex')
 * Ik maak hier wel de aanname dat de volgorde van gaten 'clockwise' gedaan worden en dat we weer bij het begin aankomen.
 *
 * Slimme stap 2:
 * Je gaat een polygon maken met alleen maar rechte hoeken. De polygoin wordt gerpresenteedr door 'borderLines' een lst van lijnen.
 * is in rechthoeken op te delen. Dit doet de method: makeRectangles. Dit bevat echter ook rectangles die buiten het polygon vallen.
 * Alle rechthoeken bij elkaar vormen samen één groot rechthoek, waar depolygon precies inpast.
 * Per rechthoek kan je onderzoeken of het tot de polygon behoort,
 *     door het aantal verticale lijnen (van de oorspronkelijke polygon) rechts van een veld in de rechthoek te tellen
 *     als het aantal zijdes oneven is, dan hoort het bij de polygon
 * Vervolgens de oppervlakte per polygon berekenen en die bij elkaar op te tellen.
 *
 */

class Day18(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="Lavaduct Lagoon", hasInputFile = true) {

    override fun resultPartOne(): Any {
        val digPlan = inputLines.map {DigAction.ofPart1(it)}
        val borderLines = digPlan.digIt()
        return borderLines.calculateArea()
    }

    override fun resultPartTwo(): Any {
        val digPlanPart2 = inputLines.map {DigAction.ofPart2(it)}
        val borderLines = digPlanPart2.digIt()
        return borderLines.calculateArea()
    }

    private fun List<Line>.calculateArea(): Long {
        val borderLinesVertical = this.filter {it.isVertical}.toSet()
        val horLines = this.divideIntoHorizontalLines()
        val verLines = this.divideIntoVerticalLines()
        val rectangles = makeRectangles(horLines, verLines)

        val rectanglesToCount = rectangles
            .filter{ rc -> rc.countLinesToTheRight(borderLinesVertical) % 2 == 1 }

        val totalArea = rectanglesToCount.sumOf { rc -> rc.area()}
        return totalArea
    }


    private fun List<DigAction>.digIt(): List<Line> {
        val result = mutableListOf<Line>()
        var currentPos = pos(0,0)
        var lastCorner = expectedCorner(this.last().direction, this[0].direction)
        this.forEachIndexed {index, action ->

            val nextCorner = if (index == this.size-1) {
                expectedCorner(this[index].direction, this[0].direction)
            } else {
                expectedCorner(this[index].direction, this[index + 1].direction)
            }

            val extra = when (lastCorner) {
                CornerType.D90 -> when (nextCorner) {
                    CornerType.D90 -> 1
                    CornerType.D270 -> 0
                }

                CornerType.D270 -> when (nextCorner) {
                    CornerType.D90 -> 0
                    CornerType.D270 -> -1
                }
            }

            val nextPos = currentPos.moveSteps(action.direction, action.steps + extra)
            result.add(Line(currentPos, nextPos))

            currentPos = nextPos
            lastCorner = nextCorner
        }
        return result
    }

    enum class CornerType{
        D90, D270
    }
    private fun expectedCorner(dir1: Direction, dir2:Direction): CornerType {
        return when (dir1) {
            Direction.RIGHT -> when(dir2) {
                Direction.UP -> CornerType.D270
                Direction.DOWN -> CornerType.D90
                else -> throw Exception("WEEIRD")
            }
            Direction.LEFT -> when(dir2) {
                Direction.UP -> CornerType.D90
                Direction.DOWN -> CornerType.D270
                else -> throw Exception("WEEIRD")
            }
            Direction.UP -> when(dir2) {
                Direction.LEFT -> CornerType.D270
                Direction.RIGHT -> CornerType.D90
                else -> throw Exception("WEEIRD")
            }
            Direction.DOWN -> when(dir2) {
                Direction.LEFT -> CornerType.D90
                Direction.RIGHT -> CornerType.D270
                else -> throw Exception("WEEIRD")
            }
        }
    }

    private fun List<Line>.divideIntoHorizontalLines(): List<Line> {
        return this
            .filter { it.isHorizontal }
            .flatMap { listOf(it.from.x, it.to.x) }
            .distinct()
            .sortedBy { it }
            .zipWithNext { a, b -> Line(pos(a,0),pos(b,0) ) }
    }

    private fun List<Line>.divideIntoVerticalLines(): List<Line> {
        return this
            .filter { it.isVertical }
            .flatMap { listOf(it.from.y, it.to.y) }
            .distinct()
            .sortedBy { it }
            .zipWithNext { a, b -> Line(pos(0, a),pos(0, b) ) }
    }

    private fun makeRectangles(horizontalLines: List<Line>, verticalLines: List<Line>) : List<Rectangle> {
        return horizontalLines.flatMap { hor ->
            verticalLines.map { ver ->
                val maxX = max(hor.from.x, hor.to.x)
                val minX = min(hor.from.x, hor.to.x)
                val maxY = max(ver.from.y, ver.to.y)
                val minY = min(ver.from.y, ver.to.y)
                Rectangle(
                    pos(minX, minY),
                    pos(maxX, maxY)
                )
            }
        }
    }

    private fun Rectangle.countLinesToTheRight(verticaLineSet: Set<Line>): Int {
        val xValue = this.topLeft().x + 0.5
        val yValue = this.topLeft().y + this.topLeft().distanceTo(this.bottomLeft())*1.0 / 2.0
        return verticaLineSet
            .count{vl ->
                vl.from.x > xValue  &&
                        yValue > min(vl.from.y.toDouble(), vl.to.y.toDouble()) &&
                        yValue < max(vl.from.y.toDouble(), vl.to.y.toDouble())
            }
    }
}

data class DigAction(val direction: Direction, val steps: Int, val color: String) {
    companion object {
        //U 2 (#7a21e3)
        fun ofPart1(raw: String) : DigAction {
            return DigAction(
                direction = when (raw.substringBefore(" ")) {
                    "U" -> Direction.UP
                    "D" -> Direction.DOWN
                    "L" -> Direction.LEFT
                    "R" -> Direction.RIGHT
                    else -> throw Exception("UnknownDirection") },
                steps = raw.drop(2).substringBefore(" ").toInt(),
                color = raw.substringBetween("(", ")")
            )
        }

        fun ofPart2(raw: String) : DigAction {
            val hexString = "0x" + raw.substringBetween("(#", ")")
            val steps = Integer.decode(hexString.take(7))
            val direction = when(hexString.last())  {
                '0' -> Direction.RIGHT
                '1' -> Direction.DOWN
                '2' -> Direction.LEFT
                '3' -> Direction.UP
                else -> throw Exception("UnknownDirection")
            }
            return DigAction(
                direction = direction,
                steps = steps,
                color = hexString
            )
        }
    }
}

data class Line(val from: Point, val to: Point) {
    val isHorizontal = from.y == to.y
    val isVertical = from.x == to.x
    fun length() = from.distanceTo(to)
}
