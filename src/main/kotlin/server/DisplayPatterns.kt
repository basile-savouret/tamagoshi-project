package server

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*

@Serializable
data class DisplayPatterns(
    var question: String? = null,
    var information: String? = null,
    var turn: String? = null,
    var tooltip: String? = null,
    var endText: String? = null,
    var tamaList: List<DisplayTama> = emptyList(),
    var rewriteTamaList: Boolean = false,
    var waitingState: Boolean = false,
    var keepInformation: Boolean = false,
)

@Serializable
data class DisplayTama(
    val name: String,
    var state: String? = null,
    var reaction: String? = null,
    var isDead: Boolean? = false
)

fun DisplayPatterns.toJsonString(): String {
    return Json.encodeToString(this)
}

fun String.toDisplayPatterns():DisplayPatterns  {
    return Json.decodeFromString<DisplayPatterns>(this)
}

