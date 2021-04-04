package server

import java.io.BufferedReader
import java.io.PrintWriter

class PlayerCommunication(private val writer: PrintWriter, private val reader: BufferedReader) {

    fun print(display: DisplayPatterns) {
        writer.println(display.toJsonString())
    }

    fun getVerifiedResponse(display: DisplayPatterns, possibilities: List<String>, printPossibilities: Boolean? = true): String {
        while (true) {
            if (printPossibilities == null || printPossibilities) display.tooltip = possibilities.toString()
            print(display)
            val response = reader.readLine().trim()
            if (possibilities.contains(response)) return response
            display.information = "votre réponse est invalide, veuillez la réécrire"
        }
    }

    fun getNonNullResponse(): String {
        while (true) {
            val response = reader.readLine().trim()
            if (response != "") return response
            print(DisplayPatterns(information = "votre réponse est vide, veuillez en indiquer une"))
        }
    }
}