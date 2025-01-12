package adventofcode.aoc2023

import adventofcode.PuzzleSolverAbstract
import com.tool.math.lcm
import tool.mylambdas.substringBetween

fun main() {
    Day20(test=false).showResult()
}

class Day20(test: Boolean) : PuzzleSolverAbstract(test, puzzleName="TBD", hasInputFile = true) {

    private fun makeModuleList(): Map<String, Module> {
        val moduleList1 = inputLines.map { Module.of(it) }.associateBy { it.name }
        val unknown = moduleList1.values.flatMap{it.sendTo}.filter{it !in moduleList1}
        val moduleList = moduleList1 + unknown.map{it to UnknownModule(it)}
        moduleList.values.forEach { module ->
            module.initSendToModuleList(module.sendTo.map { moduleList[it]!! })
        }
        moduleList.forEach { (name, module) ->
            module.initReceiveFromModuleList(moduleList.values.filter { it.sendTo.contains(name) })
        }
        return moduleList
    }

    override fun resultPartOne(): Any {
        val moduleList = makeModuleList()

        var ch = 0L
        var cl = 0L
        repeat(1000) {
            val (cnl, cnh) = moduleList.pushButtonOnce()
            ch += cnh
            cl += cnl
        }

        return ch*cl
    }

    private fun Map<String, Module>.pushButtonOnce(): Pair<Long, Long> {
        var countLow = 1L
        var countHigh = 0L
        val queue = ArrayDeque<Message>()
        val all = this["broadcaster"]!!.receive(this["broadcaster"]!!, PulseType.LOW)
        queue.addAll(all)
        while (queue.isNotEmpty()) {
            val message = queue.removeFirst()
            if (message.pulse == PulseType.LOW)
                countLow++
            else
                countHigh++

            val allMessages = message.to.receive(message.from, message.pulse)
            queue.addAll(allMessages)
        }
        return Pair(countLow, countHigh)
    }

    override fun resultPartTwo(): Any {
        val moduleList = makeModuleList()
        return moduleList.pushButtonTill()
    }

    /**
     * ga op zoek naar de voorlopers van module-rx. Dat is er eentje jz. Dit is een conjunction
     * jz stuurt een 0, als "... if it remembers high pulses for all inputs, it sends a low pulse", dus alle inputs van
     * jz moeten een HIGH sturen. (alle inputs zijn: dh, mk, vf en rn)
     *
     * maak nu een 'pushButton' loop en kijk in de loop of je één van de vier inputs tegen komt die van plan is een HIGH te sturem
     * zo ja, sla dan op hoeveel pushButton rondje je daarvoor nodig had.
     *
     * Zodra alle vier een getal hebben (en dus een keer zijn geraakt), weet je wat de totale cycle time is door het product te nemen
     * van deze vier cycleCounts (voor de zekerheid een lcm gedaan)
     */
    private fun Map<String, Module>.pushButtonTill(): Long {
        val broadCaster = this["broadcaster"]!!
        val dummy = UnknownModule("dummy")

        var i = 0L
        val monitored = mutableMapOf("dh" to 0L, "mk" to 0L, "vf" to 0L, "rn" to 0L)
        while (monitored.values.any { it == 0L } ) {
            i++
            val queue = ArrayDeque<Message>()
            val all = broadCaster.receive(dummy, PulseType.LOW)
            queue.addAll(all)
            while (queue.isNotEmpty()) {
                val message = queue.removeFirst()
                if (message.pulse == PulseType.HIGH && message.from.name in monitored) {
                    monitored[message.from.name] = i
                }
                val allMessages = message.to.receive(message.from, message.pulse)
                queue.addAll(allMessages)
            }

        }
        println(monitored.values.reduce { acc, l ->  acc*l})

        return monitored.values.reduce { acc, l ->  lcm(acc, l)}
    }
}

//======================================================================================================================

enum class PulseType { HIGH, LOW}

abstract class Module(val name: String, val sendTo: List<String>) {
    companion object {
        fun of(raw: String): Module {
            return when (raw.first()) {
                '%' -> FlipFlop.of(raw)
                '&' -> Conjunction.of(raw)
                'b' -> Broadcaster.of(raw)
                else -> throw Exception("Unexpected input")
            }
        }
    }

    protected var sendToModuleList: List<Module> = emptyList()
    fun initSendToModuleList(aList : List<Module>) {
        sendToModuleList = aList
    }

    var receiveFromModuleList: List<Module> = emptyList()
    fun initReceiveFromModuleList(aList : List<Module>) {
        receiveFromModuleList = aList
    }

    override fun toString(): String {
        return "$name(${type()}) -> ${sendToModuleList.joinToString(", "){it.name}}"
    }

    abstract fun receive(from: Module, pulse: PulseType): List<Message>
    abstract fun type(): String
}

class Broadcaster(name: String, sendTo: List<String>): Module(name, sendTo) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String): Broadcaster {
            return Broadcaster(
                name = raw.substringBefore(" ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
            )
        }
    }

    override fun receive(from: Module, pulse: PulseType): List<Message> {
        return sendToModuleList.map { Message(this, it, pulse) }
    }

    override fun type(): String {
        return "BC"
    }

}

class FlipFlop(name: String, sendTo: List<String>): Module(name, sendTo) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String): FlipFlop {
            return FlipFlop(
                name = raw.substringBetween("%", " ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
            )
        }
    }

    private var isOn : Boolean = false
    override fun receive(from: Module, pulse: PulseType): List<Message> {
        if (pulse == PulseType.LOW) {
            isOn = !isOn
            return if (isOn) {
                sendToModuleList.map { Message(this, it, PulseType.HIGH) }
            } else {
                sendToModuleList.map { Message(this, it, PulseType.LOW) }
            }
        }
        return emptyList()
    }

    override fun type(): String {
        return "FF"
    }
}

class Conjunction(name: String, sendTo: List<String>): Module(name, sendTo) {
    companion object {
        //broadcaster -> a, b, c
        fun of(raw: String): Conjunction {
            return Conjunction (
                name = raw.substringBetween("&", " ->"),
                sendTo = raw.substringAfter("-> ").split(",").map{it.trim()},
            )
        }
    }

    private val remember = mutableMapOf<Module, PulseType>()
    override fun receive(from: Module, pulse: PulseType): List<Message> {
        remember[from] = pulse
        return if (receiveFromModuleList.all{ module -> remember.getOrDefault(module, PulseType.LOW) == PulseType.HIGH}) {
            sendToModuleList.map { Message(this, it, PulseType.LOW) }
        } else {
            sendToModuleList.map { Message(this, it, PulseType.HIGH) }
        }
    }

    override fun type(): String {
        return "CJ"
    }

}

class UnknownModule(name: String): Module(name, emptyList()) {
    override fun receive(from: Module, pulse: PulseType): List<Message> {
        return emptyList()
    }

    override fun type(): String {
        return "UN"
    }

}


data class Message(val from: Module, val to:Module, val pulse: PulseType) {
    override fun toString(): String {
        return "${from.name} (${from.type()}} -- $pulse --> ${to.name} (${to.type()}}"
    }
}
