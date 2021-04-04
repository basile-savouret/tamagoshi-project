package ressource

import java.util.*

class MultiplayerTamaGameStrings_fr_FR : ListResourceBundle() {

    private val contents = arrayOf(
        arrayOf("turn", "tour n°"),
        arrayOf("waitingFor", "En attente de la fin du tour de"),
        arrayOf("cursedTurn", "Malédiction d'un des tamagoshi adverse"),
        arrayOf("chooseCursedOne", "Choississez un tamagoshi de votre adversaire qui sera maudit durant la partie:"),
        arrayOf("chooseAnnoyedOne", "choississez l'un des tamagoshi adverse à ennuyer:"),
        arrayOf("win", "a gagné avec"),
        arrayOf("didntSurvive", "n'est pas arrivé au bout et ne vous félicite pas :("),
        arrayOf("eguality", "Egalité! Vous avez le même score"),
        )

    override fun getContents(): Array<Array<String>> {
        return contents;
    }
}