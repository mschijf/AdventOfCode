package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract
import tool.coordinate.threedimensional.Point3D

fun main() {
    Day18(test=false).showResult()
}

class Day18(test: Boolean) : PuzzleSolverAbstract(test) {
    private val cubeList = inputLines
        .map {it.split(",")}
        .map{Point3D(it[0].toInt(), it[1].toInt(), it[2].toInt())}

    private val directions = listOf(
        Point3D(-1,0,0), Point3D(1,0,0),
        Point3D(0,-1,0), Point3D(0,1,0),
        Point3D(0,0,-1), Point3D(0,0,1) )

    private val minX = cubeList.minOf { Point3D -> Point3D.x }
    private val maxX = cubeList.maxOf { Point3D -> Point3D.x }
    private val minY = cubeList.minOf { Point3D -> Point3D.y }
    private val maxY = cubeList.maxOf { Point3D -> Point3D.y }
    private val minZ = cubeList.minOf { Point3D -> Point3D.z }
    private val maxZ = cubeList.maxOf { Point3D -> Point3D.z }

    private var enclosedSet = mutableSetOf <Point3D>()
    private var notEnclosedSet = mutableSetOf <Point3D>()

    override fun resultPartOne(): String {
        var cubeConnectedSides = 0
        for (i in 0 until cubeList.size - 1) {
            for (j in i+1 until cubeList.size) {
                if (cubeList[i].distanceTo(cubeList[j]) == 1) {
                    cubeConnectedSides+=2
                }
            }
        }
        val totalCubeSides = cubeList.size * 6 - cubeConnectedSides
        return totalCubeSides.toString()
    }

    override fun resultPartTwo(): String {
        val borders = determineOuterBorders()
        var countCubes = 0
        var cubeConnectedSides = 0
        for (Point3D in cubeList) {
            countCubes++
            if (countCubes % 100 == 0) {
                println("Examined $countCubes of ${cubeList.size}")
            }
            for (dir in directions) {
                if (enclosed(Point3D.plus(dir), mutableSetOf(), 0, borders) ) {
                    cubeConnectedSides++
                }
            }
        }
        val totalCubeSides = cubeList.size * 6 - cubeConnectedSides
        return totalCubeSides.toString()
    }

    private fun determineOuterBorders(): Set<Point3D> {
        val result = mutableSetOf<Point3D>()
        for (x in minX .. maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
                for (z in maxZ downTo minZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
            }
            for (y in maxY downTo minY) {
                for (z in minZ..maxZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
                for (z in maxZ downTo minZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
            }
        }

        for (x in maxX downTo  minX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
                for (z in maxZ downTo minZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
            }
            for (y in maxY downTo minY) {
                for (z in minZ..maxZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
                for (z in maxZ downTo minZ) {
                    val Point3D = Point3D(x, y, z)
                    if (Point3D in cubeList) {
                        break
                    }
                    result.add(Point3D)
                }
            }
        }

        return result
    }

    private fun enclosed(Point3D: Point3D, alreadyVisited: MutableSet<Point3D>, level: Int, borders: Set<Point3D>): Boolean {
        if (Point3D.x < minX || Point3D.x > maxX ||Point3D.y < minY || Point3D.y > maxY || Point3D.z < minZ || Point3D.z > maxZ)
            return false
        if (Point3D in borders)
            return false


        if (Point3D in enclosedSet)
            return true
        if (Point3D in notEnclosedSet)
            return false


        if (Point3D in cubeList)
            return true


        for (dir in directions) {
            val next = Point3D.plus(dir)

            if (!alreadyVisited.contains(next)) {
                alreadyVisited.add(next)
                if (!enclosed(next, alreadyVisited, level+1, borders)) {
                    notEnclosedSet.add(next)
                    return false
                }
            }
        }
        enclosedSet.add(Point3D)
        return true
    }
}

//----------------------------------------------------------------------------------------------------------------------
//
//class Point3D(val x: Int, val y: Int, val z: Int) {
//    fun connectedTo(other: Point3D): Boolean {
//        return  (x == other.x && y == other.y && (z-other.z).absoluteValue == 1) ||
//                (x == other.x && z == other.z && (y-other.y).absoluteValue == 1) ||
//                (y == other.y && z == other.z && (x-other.x).absoluteValue == 1)
//    }
//
//    fun plus(dir: Point3D): Point3D {
//        return (Point3D(x+dir.x, y+dir.y, z+dir.z))
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (other !is Point3D)
//            return super.equals(other)
//        return x==other.x && y == other.y && z == other.z
//    }
//
//    override fun toString() = "($x,$y,$z)"
//    override fun hashCode(): Int = 10000*x + 100*y + z
//
//}

