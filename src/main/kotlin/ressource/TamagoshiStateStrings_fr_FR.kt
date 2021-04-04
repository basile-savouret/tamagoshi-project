package ressource

import java.util.*

class TamagoshiStateStrings_fr_FR : ListResourceBundle() {

    private val contents = arrayOf(
        arrayOf("happyState", "je suis heureux"),
        arrayOf("hungryState", "je suis affamé"),
        arrayOf("satisfiedState", "BURPS!"),
        arrayOf("boredState", "je m'ennuie à mourrir"),
        arrayOf("playFulState", "On se marre !"),
        arrayOf("annoyedState", "laisse-moi tranquille, je bouquine !!"),
        arrayOf("notHungryState", "je n'ai pas faim!"),
        )

    override fun getContents(): Array<Array<String>> {
        return contents;
    }
}