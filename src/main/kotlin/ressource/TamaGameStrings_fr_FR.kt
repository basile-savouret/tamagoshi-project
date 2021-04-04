package ressource

import java.util.*

class TamaGameStrings_fr_FR : ListResourceBundle() {

    private val contents = arrayOf(
        arrayOf("turn", "tour n°"),
        arrayOf("endGame", "Fin de partie !!"),
        arrayOf("askFeed", "nourrir quel tamagoshi?"),
        arrayOf("askPlay", "jouer avec quel tamagoshi?"),
        arrayOf("whoWasA", "qui était un"),
        arrayOf("survived", "a survécu et vous remercie :)"),
        arrayOf("didntSurvive", "n'est pas arrivé au bout et ne vous félicite pas :("),
        arrayOf("score", "score obtenu:"),
        )

    override fun getContents(): Array<Array<String>> {
        return contents;
    }
}