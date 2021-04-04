package ressource

import java.util.*

class GameStrings_fr_FR : ListResourceBundle() {

    private val contents = arrayOf(
        arrayOf("enterYourName", "Entrez votre nom:"),
        arrayOf("gameIsFull", "La partie est pleine vous ne pouvez pas rentrer dedans"),
        arrayOf("askReplay", "voulez vous rejouez?"),
        arrayOf("replay", "rejouer"),
        arrayOf("quit", "quitter"),
        arrayOf("chooseDifficulty", "Choississez la difficulté de la partie"),
        arrayOf("easy", "facile"),
        arrayOf("medium", "moyen"),
        arrayOf("hard", "difficile"),
        arrayOf("askMultiplier", "Voulez-vous jouer seul ou à 2"),
        arrayOf("waitingSecond", "En attente d'un deuxième joueur"),
        arrayOf("enteredYourGame", "est entré dans votre partie"),
        arrayOf("easy", "facile"),
        )

    override fun getContents(): Array<Array<String>> {
        return contents;
    }
}