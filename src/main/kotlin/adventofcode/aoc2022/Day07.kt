package adventofcode.aoc2022

import adventofcode.PuzzleSolverAbstract

fun main() {
    Day07(test=false).showResult()
}

class Day07(test: Boolean) : PuzzleSolverAbstract(test) {
    private var root = Dir("/", null)
    private var current = root

    init {
        inputLines
            .forEach {
                if (it.startsWith("$ ")) {
                    executeCmd(it.substring("$ ".length).trim())
                } else if (it.startsWith("dir ")) {
                    current.addDir(it.substring("dir ".length).trim())
                } else { //file
                    val fileInfoSplitted = it.trim().split(" ")
                    current.addFile(fileInfoSplitted[1], fileInfoSplitted[0].toInt())
                }
            }
    }

    private fun executeCmd(cmd: String) {
        if (cmd.startsWith("cd ")) {
            current = when(val direction = cmd.substring("cd ".length).trim()) {
                ".." -> current.oneDirUp()
                "/" -> root
                else -> current.oneDirDown(direction)
            }
        }
    }

    override fun resultPartOne(): String {
        return root.getAllDirsAsList()
            .filter { it.getSize() <= 100000 }
            .sumOf { it.getSize() }
            .toString()
    }

    override fun resultPartTwo(): String {
        val freeSpace = 70000000 - root.getSize()
        val spaceNeeded = 30000000 - freeSpace
        return root.getAllDirsAsList()
            .filter { it.getSize() >= spaceNeeded }
            .minByOrNull { it.getSize() }!!
            .getSize()
            .toString()
    }
}

//----------------------------------------------------------------------------------------------------------------------

class Dir(
    private val name: String,
    private val parent: Dir?) {
    private val fileList = mutableMapOf<String, Int>()
    private val dirList = mutableMapOf<String, Dir>()

    private var size: Int? = null
    fun getSize(): Int {
        if (size == null) {
            size = fileList.values.sum() + dirList.values.sumOf { it.getSize() }
        }
        return size!!
    }

    fun addFile(fileName: String, fileSize: Int) {
        fileList[fileName] = fileSize
    }

    fun addDir(dirName: String) {
        dirList[dirName] = Dir(dirName, this)
    }

    fun oneDirUp() = parent!!

    fun oneDirDown(dirName: String) = dirList[dirName]!!

    fun printTree(indent: Int = 0) {
        print(" ".repeat(indent))
        println(name)
        dirList.values.forEach { it.printTree(indent + 3) }
        fileList.keys.forEach {
            print(" ".repeat(indent+3))
            println("$it (${fileList[it]})")
        }
    }

    fun getAllDirsAsList() : List<Dir> {
        val result = mutableListOf<Dir>()
        result.add(this)
        dirList.values.forEach { result.addAll(it.getAllDirsAsList())}
        return result
    }
}
