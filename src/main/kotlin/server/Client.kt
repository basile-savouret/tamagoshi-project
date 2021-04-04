package server

import java.net.Socket

class Client(val socket: Socket, val com: PlayerCommunication, initialName: String) {
    var name: String

    init {
        name = initialName
    }
}