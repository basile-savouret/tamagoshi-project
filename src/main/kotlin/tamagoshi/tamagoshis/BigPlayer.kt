package tamagoshi.tamagoshis

import server.PlayerCommunication

class BigPlayer(name: String, com: PlayerCommunication): Tamagoshi(name, com) {

    override fun consumeFun(): Boolean {
        return if (hasFun > 1) {
            hasFun-= 2
            true
        } else {
            hasFun-= 2
            false
        }
    }

    override fun reveal(): String? {
        return "GrosJoueur"
    }

}