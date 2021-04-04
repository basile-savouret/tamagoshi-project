package tamagoshi.jeu

import server.Client
import server.DisplayPatterns
import server.DisplayTama
import server.PlayerCommunication
import tamagoshi.tamagoshis.BigEater
import tamagoshi.tamagoshis.BigPlayer
import tamagoshi.tamagoshis.Tamagoshi
import java.util.*
import kotlin.math.round
import kotlin.random.Random

open class Tamagame(private var difficulty: DifficultyLevel, private val player: Client) {
    protected var tamaList: MutableList<Tamagoshi> = emptyList<Tamagoshi>().toMutableList()
    protected var tamaDeadList: MutableList<Tamagoshi> = emptyList<Tamagoshi>().toMutableList()
    private val bundle: ResourceBundle

    init {
        val nameList = listOf("Pierre", "Paul", "Jack", "Mike", "Ted", "Antoine", "Titouan", "Alexandre", "Henry").toMutableList()
        tamasInit(nameList, tamaList, player.com)
        bundle = ResourceBundle.getBundle("ressource.TamaGameStrings_fr_FR", Locale.getDefault())
    }

    protected fun tamasInit(nameList: MutableList<String>, tamaList: MutableList<Tamagoshi>, com: PlayerCommunication) {
        val tamaNumber: Int =
                if (difficulty == DifficultyLevel.FACILE) 3
                else if (difficulty == DifficultyLevel.MOYEN) 5
                else 7

        for (i in 1..tamaNumber) {
            val name = nameList.random()
            nameList.remove(name)
            tamaList.add(element = randomTama(name, com))
        }
    }

    protected fun randomTama(name: String, com: PlayerCommunication): Tamagoshi {
        val rand = Random.nextFloat() - 0.5
        if (rand <= -0.25) return BigEater(name, com)
        if (rand <= 0) return BigPlayer(name, com)
        return Tamagoshi(name, com)
    }

    open fun start() {
        for (i in 1..10) {
            val display = DisplayPatterns(turn = "${bundle.getString("turn")}$i")
            display.tamaList = tamaList.map { DisplayTama(name = it.name, state = it.speak()) }
            player.com.print(display)
            feadTama(player.com, tamaList)
            playWithTama(player.com, tamaList)
            tamaList = getAliveTamas(player.com, tamaList, tamaDeadList)
            if (tamaList.isEmpty()) break
        }
        player.com.print(DisplayPatterns(information = bundle.getString("endGame")))
        getAndDisplayResult(player.com, tamaList, tamaDeadList)
    }

    protected fun getAliveTamas(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>, tamaDeadList: MutableList<Tamagoshi>): MutableList<Tamagoshi> {
        val list = emptyList<DisplayTama>().toMutableList()
        tamaDeadList.addAll(elements = tamaList.filter {
            it.grow()
            if (it.isDead()) {
                list.add(DisplayTama(name = it.name, isDead = true))
                true
            } else {
                false
            }
        })
        com.print(DisplayPatterns(tamaList = list))
        return tamaList.filter { !it.isDead() }.toMutableList()
    }


    protected fun feadTama(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>) {
        val display = DisplayPatterns(question = bundle.getString("askFeed"))
        tamaList[chooseTama(com, tamaList, display)].eat()
    }

    protected fun playWithTama(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>) {
        val display = DisplayPatterns(question = bundle.getString("askPlay"))
        tamaList[chooseTama(com, tamaList, display)].play()
    }

    protected fun chooseTama(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>, display: DisplayPatterns): Int {
        val tamaSelected = com.getVerifiedResponse(display, tamaList.mapIndexed { index, it -> "$index" }, false).toInt()
        return tamaSelected
    }

    protected fun getAndDisplayResult(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>, tamaDeadList: MutableList<Tamagoshi>): Double {
        var text = ""
        tamaList.forEach {
            val reaveal = it.reveal()
            text += "${
                if (reaveal != null) {
                    "${it.name} ${bundle.getString("whoWasA")} $reaveal"
                } else it.name
            } ${bundle.getString("survived")} \n"
        }
        tamaDeadList.forEach {
            val reaveal = it.reveal()
            text += "${
                if (reaveal != null) {
                    "${it.name} ${bundle.getString("whoWasA")} $reaveal"
                } else it.name
            } ${bundle.getString("didntSurvive")} \n"
        }
        val result = round(getScore(10.0) * 10)/10
        text += "${bundle.getString("score")} ${result}%"
        val display = DisplayPatterns(endText = text)
        com.print(display)
        return result
    }

    protected fun getScore(actualMaxAge: Double): Double {
        val tamaDeadAge = tamaDeadList.fold(0) { acc, it -> acc + it.age }
        val tamaAliveAge = tamaList.size * actualMaxAge
        val totalMaxAge = (tamaList.size + tamaDeadList.size) * actualMaxAge
        return ((tamaDeadAge + tamaAliveAge) / totalMaxAge) * 100
    }

}

enum class DifficultyLevel {
    FACILE,
    MOYEN,
    DIFFCILE;
}