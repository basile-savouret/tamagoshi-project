package tamagoshi.tamagoshis

import server.DisplayPatterns
import server.DisplayTama
import server.PlayerCommunication
import java.util.*
import kotlin.random.Random

open class Tamagoshi(val name: String, protected val com: PlayerCommunication) {
    var age: Int
    private val maxEnergy: Int
    protected var energy: Int
    protected var hasFun: Int
    private val maxFun: Int
    var cursed: Boolean
    protected val bundle: ResourceBundle


    init {
        age = 0
        maxEnergy = Random.nextInt(5, 10)
        energy = Random.nextInt(3, 8)
        maxFun = Random.nextInt(7, 11)
        hasFun = Random.nextInt(4, 9)
        cursed = false
        bundle = ResourceBundle.getBundle("ressource.TamagoshiStateStrings_fr_FR", Locale.getDefault())
    }

    private fun getState(): Pair<TamagoshiState, TamagoshiState?> {
        var firstState: TamagoshiState? = null
        if (energy <= 4) {
            firstState = TamagoshiState.HUNGRY
        }

        if (hasFun <= 4) {
            if (firstState != null) return Pair(firstState, TamagoshiState.BORED)
            else firstState = TamagoshiState.BORED
        }

        if (firstState != null) return Pair(firstState, null)
        return Pair(TamagoshiState.HAPPY, null)
    }

    fun speak() :String {
        val state = getState()
        return "${state.first.toString(bundle)} ${if (state.second != null) "et " + state.second!!.toString(bundle) else ""}"
    }

    fun eat() {
        val tama = if (energy < maxEnergy) {
            energy += if (cursed) Random.nextInt(2,5) else Random.nextInt(1,4)
            DisplayTama(name = this.name, reaction = TamagoshiState.SATISFIED.toString(bundle))

        } else {
            DisplayTama(name = this.name, reaction = TamagoshiState.NOTHUNGRY.toString(bundle))
        }
        com.print(DisplayPatterns(tamaList = listOf(tama)))
    }

    protected open fun consumeEnergy(): Boolean {
        return if (energy > 0) {
            energy--
            true
        } else {
            energy--
            false
        }
    }

    fun grow(): Boolean {
        if (!consumeEnergy()) return false
        if (!consumeFun()) return false
        this.age++
        return true
    }

    protected open fun consumeFun(): Boolean {
        return if (hasFun > 0) {
            hasFun--
            true
        } else {
            hasFun--
            false
        }
    }

    fun play() {
        val tama = if (hasFun < maxFun) {
            hasFun += Random.nextInt(2,5)
            DisplayTama(name = this.name, reaction = TamagoshiState.PLAYFUL.toString(bundle))
        } else {
            DisplayTama(name = this.name, reaction = TamagoshiState.ANNOYED.toString(bundle))
        }
        com.print(DisplayPatterns(tamaList = listOf(tama)))
    }

    fun annoy() {
        hasFun--
    }

    fun isDead(): Boolean {
        if (hasFun <= 0 || energy <= 0) {
            return true
        }
        return false
    }

    open fun reveal(): String? {
        return null
    }

    override fun toString(): String {
        return "name: ${name} \n age: ${age} \n maxEnergy: ${maxEnergy} \n energy: ${energy}"
    }
}

enum class TamagoshiState(val display: String) {
    HAPPY("happyState"),
    HUNGRY("hungryState"),
    SATISFIED("satisfiedState"),
    BORED("boredState"),
    PLAYFUL("playFulState"),
    ANNOYED("annoyedState"),
    NOTHUNGRY("notHungryState");


    fun toString(bundle: ResourceBundle): String {
        return bundle.getString(display)
    }
}