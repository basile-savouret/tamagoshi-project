package tamagoshi.jeu

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader


object Utilisateur {
    fun saisieClavier(): String? {

        /*il faut gérer les exceptions car l'entrée standard
    peut ne pas être disponible : le constructeur de la
    classe InputStreamReader peut renvoyer une exception.*/
        return try {
            val clavier = BufferedReader(InputStreamReader(System.`in`))
            clavier.readLine()
        } catch (e: IOException) {
            e.printStackTrace()
            System.exit(0)
            null
        }
    }

    // une méthode main juste pour tester
    @JvmStatic
    fun main(args: Array<String>) {
        val saisie = saisieClavier()
        println("la saisie est : $saisie")
    }
}