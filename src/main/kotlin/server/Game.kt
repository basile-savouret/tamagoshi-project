package server

import tamagoshi.jeu.DifficultyLevel
import tamagoshi.jeu.MultiplayerTamaGame
import tamagoshi.jeu.Tamagame
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.*
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

class Game(port: Int) {
    private val port: Int
    private val server: ServerSocket
    private var clientList: MutableList<Client>
    private var maxPlayer: Int
    private var currentGame: Tamagame?
    private val secondPsemaphore: Semaphore
    private val gameSemaphore: Semaphore
    private val bundle: ResourceBundle

    init {
        this.port = port
        server = ServerSocket(port)
        clientList = emptyList<Client>().toMutableList()
        maxPlayer = 1
        currentGame = null
        secondPsemaphore = Semaphore(0)
        gameSemaphore = Semaphore(0)
        bundle = ResourceBundle.getBundle("ressource.GameStrings_fr_FR", Locale.getDefault());
    }

    fun start() {
        while (true) {
            val socket = server.accept()
            val writer = PrintWriter(socket.getOutputStream(), true)
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val com = PlayerCommunication(writer, reader)
            val client = Client(socket, com, "player${clientList.size + 1}")
            if (clientList.size < maxPlayer) {
                clientList.add(element = client)
                val clientIndex = clientList.indexOf(client)
                println("a new client is connected in the place n°${clientIndex}")
                thread(start = true, name = "handler of the client n°${clientIndex}") {
                    clientHandler(client, clientIndex)
                }
            } else {
                PrintWriter(
                    client.socket.getOutputStream(),
                    true
                ).println(DisplayPatterns(information = bundle.getString("gameIsFull")).toJsonString())
                client.socket.close()
                println("the server refused a client because it is full")
            }
        }
    }

    private fun clientHandler(client: Client, id: Int) {
        try {
            client.com.print(DisplayPatterns(question = bundle.getString("enterYourName")))
            client.name = client.com.getNonNullResponse()
            while (true) {
                if (id == 0) {
                    firstPlayerHandler(client)
                }
                if (id == 1) {
                    secondPlayerHandler()
                }
                val restart = client.com.getVerifiedResponse(
                    DisplayPatterns(question = bundle.getString("askReplay"), keepInformation = true),
                    listOf(bundle.getString("replay"), bundle.getString("quit"))
                )
                if (restart == "quitter") break
            }
        } catch (e: IOException) {
            println("the client n°$id will be rejected because of the error: $e")
            e.printStackTrace()
        } finally {
            clientList.remove(client)
            client.socket.close()
            println("deconnexion of the client n°$id")
        }
    }

    private fun firstPlayerHandler(client: Client) {
        val difficulties = listOf(bundle.getString("easy"), bundle.getString("medium"),  bundle.getString("hard"))
        val difficulty = DifficultyLevel.valueOf(
            client.com.getVerifiedResponse(
                DisplayPatterns(
                    question = bundle.getString("chooseDifficulty")
                ),
                difficulties
            ).toUpperCase()
        )
        val playModes = listOf("1", "2")
        val playMode = client.com.getVerifiedResponse(DisplayPatterns(question = bundle.getString("askMultiplier")), playModes)
        if (playMode == "1") {
            currentGame = Tamagame(difficulty, client)
            currentGame?.start()
        } else {
            maxPlayer = 2
            client.com.print(DisplayPatterns(information = bundle.getString("waitingSecond")))
            secondPsemaphore.acquire()
            client.com.print(DisplayPatterns(information ="${clientList.get(1).name} ${bundle.getString("enteredYourGame")}"))
            currentGame = MultiplayerTamaGame(difficulty, client, clientList.get(1))
            currentGame?.start()
            gameSemaphore.release()
        }
    }

    private fun secondPlayerHandler() {
        secondPsemaphore.release()
        gameSemaphore.acquire()
    }

    fun closeServer() {
        clientList.forEach {
            PrintWriter(it.socket.getOutputStream(), true).println("the server is closed")
            it.socket.close()
        }
        clientList = emptyList<Client>().toMutableList()
        server.close()
    }
}

fun main() {
    val game = Game(4000)
    game.start()
}
