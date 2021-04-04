package tamagoshi.jeu

import server.Client
import server.DisplayPatterns
import server.DisplayTama
import server.PlayerCommunication
import tamagoshi.tamagoshis.Tamagoshi
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

class MultiplayerTamaGame(difficulty: DifficultyLevel, val firstP: Client, val secondP: Client): Tamagame(difficulty, firstP) {
    private var secondPTamaList: MutableList<Tamagoshi> = emptyList<Tamagoshi>().toMutableList()
    private var secondPTamaDeadList: MutableList<Tamagoshi> = emptyList<Tamagoshi>().toMutableList()
    private val bundle: ResourceBundle

    init {
        val nameList = listOf("Jean", "Tristan", "Tom", "Roger", "Luc", "Michel", "Romain", "Mathieu", "Patrick").toMutableList()
        tamasInit(nameList, secondPTamaList, secondP.com)
        bundle = ResourceBundle.getBundle("ressource.MultiplayerTamaGameStrings_fr_FR", Locale.getDefault())
    }

    override fun start() {
        cursingTurn()
        for (i in 1..10) {
            displayedOnBoth(DisplayPatterns(turn = "${bundle.getString("turn")}$i"))
            turn()
            if (tamaList.isEmpty() || secondPTamaList.isEmpty()) break
        }
        val firstPResult = getAndDisplayResult(firstP.com, tamaList, tamaDeadList)
        val secondPResult = getAndDisplayResult(secondP.com, secondPTamaList, secondPTamaDeadList)
        val information = if (firstPResult > secondPResult) {
            "${firstP.name} ${bundle.getString("win")} ${firstPResult}%"
        } else if (firstPResult < secondPResult) {
            "${secondP.name} ${bundle.getString("win")} ${secondPResult}% ${secondPResult}%"
        } else {
            "${bundle.getString("eguality")} ${firstPResult}%"
        }
        displayedOnBoth(DisplayPatterns(information = information))
    }

    private fun turn(){
        val s1 = Semaphore(0)
        val s2 = Semaphore(0)
        val s3 = Semaphore(0)
        val s4 = Semaphore(0)
        thread(start = true, name = "the turn of the first player") {
            playTurn(firstP.com, tamaList, secondPTamaList)
            s1.release()
            if (!s2.tryAcquire()) {
                firstP.com.print(DisplayPatterns(information = "${bundle.getString("waitingFor")} ${secondP.name}", waitingState = true))
                s2.acquire()
            }
            tamaList = getAliveTamas(firstP.com, tamaList,tamaDeadList)
            s3.release()
        }
        thread(start = true, name = "the turn of the second player") {
            playTurn(secondP.com, secondPTamaList, tamaList)
            s2.release()
            if (!s1.tryAcquire()) {
                secondP.com.print(DisplayPatterns(information = "${bundle.getString("waitingFor")} ${firstP.name}", waitingState = true))
                s1.acquire()
            }
            secondPTamaList = getAliveTamas(secondP.com, secondPTamaList,secondPTamaDeadList)
            s4.release()
        }
        s3.acquire()
        s4.acquire()
    }

    private fun cursingTurn() {
        val s1 = Semaphore(0)
        val s2 = Semaphore(0)
        val s3 = Semaphore(0)
        val s4 = Semaphore(0)
        thread(start = true, name = "the firt player is choosing the crused one") {
            val display = DisplayPatterns(turn = bundle.getString("cursedTurn"), information = bundle.getString("chooseCursedOne"))
            display.tamaList = secondPTamaList.map {DisplayTama(name = it.name)}
            display.rewriteTamaList = true
            secondPTamaList[chooseTama(firstP.com, secondPTamaList, display)].cursed = true
            s1.release()
            if (!s2.tryAcquire()) {
                firstP.com.print(DisplayPatterns(information = "${bundle.getString("waitingFor")} ${secondP.name}", waitingState = true))
                s2.acquire()
            }
            s3.release()
        }
        thread(start = true, name = "the second player is choosing the crused one") {
            val display = DisplayPatterns(turn = bundle.getString("cursedTurn"), information = bundle.getString("chooseCursedOne"))
            display.tamaList = tamaList.map {DisplayTama(name = it.name)}
            display.rewriteTamaList = true
            tamaList[chooseTama(secondP.com, tamaList, display)].cursed = true
            s2.release()
            if (!s1.tryAcquire()) {
                secondP.com.print(DisplayPatterns(information = "${bundle.getString("waitingFor")} ${firstP.name}", waitingState = true))
                s1.acquire()
            }
            s4.release()
        }
        s3.acquire()
        s4.acquire()
    }

    private fun annoyTama(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>) {
        val list = tamaList.map { DisplayTama(name = it.name) }
        val display = DisplayPatterns(information = bundle.getString("chooseAnnoyedOne"), tamaList = list, rewriteTamaList = true)
        tamaList[chooseTama(com, tamaList, display)].annoy()
    }

    private fun playTurn(com: PlayerCommunication, tamaList: MutableList<Tamagoshi>, oponentTamaList: MutableList<Tamagoshi>){
        val list = tamaList.map { DisplayTama(name = it.name, state = it.speak()) }
        com.print(DisplayPatterns(tamaList = list, rewriteTamaList = true))
        feadTama(com, tamaList)
        playWithTama(com, tamaList)
        annoyTama(com, oponentTamaList)
    }

    private fun displayedOnBoth(display: DisplayPatterns) {
        firstP.com.print(display)
        secondP.com.print(display)
    }
}