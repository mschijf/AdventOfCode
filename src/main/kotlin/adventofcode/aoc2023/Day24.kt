package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.threedimensional.Point3DLong
import tool.mylambdas.collectioncombination.filterCombinedItems

fun main() {
    Day24(test=false).showResult()
}

class Day24(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private val movingPointList = inputLines.map{MovingPoint.of(it)}

    val area = if (test) 7.0..27.0 else 200000000000000.0..400000000000000.0

    override fun resultPartOne(): Any {
        val xx = movingPointList.filterCombinedItems { movingPoint1, movingPoint2 ->
            val intersectionPoint = movingPoint1.intersectionWithOtherPointAfterTime(movingPoint2, 0.0)
            intersectionPoint != null && intersectionPoint.location.x in area&& intersectionPoint.location.y in area
        }
        return xx.size
    }

    override fun resultPartTwo(): Any {

        val range = -500L..500L
        while (true) {
            val hail = movingPointList.shuffled().take(4)
            range.forEach { deltaX ->
                range.forEach { deltaY ->
                    val hail0 = hail[0].afterVelocityChange(deltaX, deltaY, 0)
                    val intercepts = hail.drop(1).mapNotNull {
                        it.afterVelocityChange(deltaX, deltaY, 0).intersectionWithOtherPointAfterTime(hail0, 0.0)
                    }
                    if (intercepts.size == 3 &&
                        intercepts.all { it.location.x == intercepts.first().location.x } &&
                        intercepts.all { it.location.y == intercepts.first().location.y }
                    ) {
                        range.forEach { deltaZ ->
                            val z1 = hail[1].positionZAfterTime(intercepts[0].time, deltaZ)
                            val z2 = hail[2].positionZAfterTime(intercepts[1].time, deltaZ)
                            val z3 = hail[3].positionZAfterTime(intercepts[2].time, deltaZ)
                            if (z1 == z2 && z2 == z3) {
                                return (intercepts.first().location.x + intercepts.first().location.y + z1).toLong()
                            }
                        }
                    }
                }
            }
        }
    }
}

data class MovingPoint(val location: Point3DLong, val speed: Speed) {

    companion object {
        //20, 19, 15 @  1, -5, -3
        fun of(raw: String): MovingPoint {
            val p1 = Point3DLong.of(raw.split(" @ ")[0])
            val p2 = Point3DLong.of(raw.split(" @ ")[1])
            return MovingPoint(
                location = p1,
                speed = Speed(p2.x, p2.y, p2.z)
            )
        }
    }

    private fun toHailLine(): HailLine {
        val a = speed.y.toDouble()/speed.x.toDouble()
        val b = location.y - a * location.x
        return HailLine(a, b)
    }

    //y = ax + b, waarbij a de 'slope' is (= richtingscoëfficient)
    fun intersectionWithOtherPointAfterTime(other: MovingPoint, afterTime: Double): IntersectionPointWithTime? {
        val thisLine = toHailLine()
        val otherLine = other.toHailLine()
        val intersection = thisLine.intersectionOrNull(otherLine)
        if (intersection != null) {
            val tThis = (intersection.x - location.x) / speed.x
            val tOther = (intersection.x - other.location.x) / other.speed.x
            return if (tThis > afterTime && tOther > afterTime)
                IntersectionPointWithTime(intersection, tThis)
            else
                null
        }
        return null
    }

    fun afterVelocityChange(vx: Long, vy: Long, vz: Long): MovingPoint {
        return MovingPoint(location, Speed(speed.x + vx, speed.y + vy, speed.z + vz))
    }

    fun positionZAfterTime(time: Double, deltaVZ: Long): Double {
        return (location.z + time * (speed.z + deltaVZ))
    }

}

data class Speed(val x: Long, val y: Long, val z: Long)

data class HailLine(val a: Double, val b: Double) {

    fun intersectionOrNull(other: HailLine): Point2DDouble? {
        if (a.isInfinite() || other.a.isInfinite() || a == other.a) {
            return null
        }

        val x = (other.b - this.b)/(this.a - other.a)
        val y = (this.a * x) + this.b
        return Point2DDouble(x, y)
    }
}

data class IntersectionPointWithTime(val location: Point2DDouble, val time: Double)
data class Point2DDouble(val x: Double, val y: Double)

// Thanks to: https://todd.ginsberg.com/post/advent-of-code/2023/day24/ :
//
// I won’t pretend that I came up with this solution. This part of this puzzle was the hardest one for me all year.
// I had a lot of help from Google and Reddit. And that’s fine.
//
// Basically what we’re going to do is pretend that the rock we are throwing is still and the hailstones are all moving
// at some new velocity relative to the now still “thrown” rock. In theory, they should all intersect with some given
// point, which we can calculate with the code we wrote for part 1.
//
// The strategy here is to run through every value for x and y (within some small range) to use as a velocity delta,
// and find four random hailstones that collide given those deltas. This gives us the x and y points that the rock
// must be thrown by. And from that, we can iterate through the same range to apply a delta to z, and find the point
// where all of those same hailstones meet up.
//
// Since I’m picking random hailstones to start with, this may not actually work (for the small range chosen), so we
// run this over and over until it does work.
//
// As a result, sometimes it runs fast, sometimes it takes 30 seconds or more...
//
// Nevertheless, the result should be: 711031616315001