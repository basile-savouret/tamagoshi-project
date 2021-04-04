package tamagoshi.tamagoshis

import server.DisplayPatterns
import server.DisplayTama
import server.PlayerCommunication

class BigEater(name: String, com: PlayerCommunication): Tamagoshi(name, com) {

    override fun consumeEnergy(): Boolean {
        return if (energy > 1) {
            energy-= 2
            true
        } else {
            energy-= 2
            false
        }
    }

    override fun reveal(): String? {
        return "GrosMangeur"
    }

}