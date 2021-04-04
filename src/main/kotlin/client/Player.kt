package client

import server.DisplayPatterns
import server.DisplayTama
import server.toDisplayPatterns
import java.awt.BorderLayout
import java.awt.Choice
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.ItemEvent
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import javax.swing.*
import kotlin.concurrent.thread

fun main() {
    val player = Player("localhost", 4000)
}

class Player(host: String, serverPort: Int) : JFrame(), ActionListener {
    private val client: Socket
    private val writer: PrintWriter
    private val reader: BufferedReader
    private val informationLabel: JLabel = JLabel("")
    private val actionLabel: JLabel = JLabel("")
    private val actionInput: JTextField = JTextField(15)
    private val sendButton: JButton = JButton("Envoyer")
    private var lastQuestion: String? = ""
    private val turnLabel: JLabel = JLabel("", JLabel.CENTER)
    private val tamaStateLabel: JLabel = JLabel("", JLabel.CENTER)
    private val tamaReactionLabel: JLabel = JLabel("", JLabel.CENTER)
    private val chooseButton: JButton = JButton("Choisir")
    private val tamaSelect: Choice = Choice()
    private var currentTama: DisplayTama? = null
    private var displayTamaList: HashMap<String, DisplayTama> = HashMap()
    private val actionsPanel = JPanel()
    private val tamaPanel = JPanel()
    private val tamaInformationLabel: JLabel = JLabel("")
    private val tamaActionLabel: JLabel = JLabel("")
    private val deadTamaLabel: JLabel = JLabel("Vos tamagoshi mort:")
    private val endText: JTextArea = JTextArea("")
    private var responseSent: Boolean = false

    init {
        client = Socket(host, serverPort)
        writer = PrintWriter(client.getOutputStream(), true)
        reader = BufferedReader(InputStreamReader(client.getInputStream()))
        thread(start = true, name = "client listener from the server") {
            listener()
        }
        title = "Player"
        setBounds(500, 250, 500, 500)
        contentPane.layout = FlowLayout()
        actionsPanel.layout = BoxLayout(actionsPanel, BoxLayout.Y_AXIS)
        tamaPanel.layout = BoxLayout(tamaPanel, BoxLayout.Y_AXIS)
        tamaPanel.add(turnLabel)
        tamaPanel.add(tamaInformationLabel)
        tamaPanel.add(tamaActionLabel)
        tamaPanel.add(tamaSelect)
        tamaPanel.add(tamaStateLabel)
        tamaPanel.add(chooseButton)
        tamaPanel.add(tamaReactionLabel)
        tamaPanel.add(deadTamaLabel)
        tamaPanel.isVisible = false
        actionsPanel.add(informationLabel)
        actionsPanel.add(actionLabel)
        actionsPanel.add(actionInput)
        actionsPanel.add(sendButton)
        add(tamaPanel, BorderLayout.SOUTH)
        add(actionsPanel, BorderLayout.SOUTH)
        add(endText)
        sendButton.addActionListener(this)
        chooseButton.addActionListener(this)
        tamaSelect.addItemListener { e: ItemEvent? ->
            updateTamaStates()
        }
        isVisible = true
    }

    fun listener() {
        while (true) {
            if (client.isClosed) break
            val response = reader.readLine()
            displayByPattern(response)
            if (response == null) break
        }
        close()
    }

    fun displayByPattern(message: String) {
        val display = message.toDisplayPatterns()
        println(display)
        actionLabel.text = display.question
        lastQuestion = display.question
        if (!display.keepInformation) {
            informationLabel.text = display.information
            tamaInformationLabel.text = display.information
        }
        actionLabel.text = if (display.tooltip != null) {
            "${actionLabel.text} ${display.tooltip}"
        } else {
            actionLabel.text
        }
        tamaActionLabel.text = actionLabel.text
        if (display.turn != null) {
            turnLabel.text = display.turn
            tamaPanel.isVisible = true
            actionsPanel.isVisible = false
        }
        if (display.endText != null) {
            endText.text = display.endText
            tamaPanel.isVisible = false
            actionsPanel.isVisible = true
        }
        updateTamaList(display)
        if (!display.waitingState) {
            responseSent = false
        }
    }

    fun updateTamaList(display: DisplayPatterns) {
        if (display.rewriteTamaList) {
            tamaSelect.removeAll()
            displayTamaList = HashMap()
        }
        display.tamaList.forEach {
            val tamaExisting = displayTamaList.get(it.name)
            if (tamaExisting?.state != null && it.state == null) {
                it.state = tamaExisting.state
            }
            if (tamaExisting != null) {
                if (it.isDead == true) {
                    displayTamaList.remove(it.name)
                    tamaSelect.remove(it.name)
                    deadTamaLabel.text = deadTamaLabel.text + " ${it.name},"
                } else {
                    displayTamaList.replace(it.name, it)
                }
            } else {
                if (it.isDead == true) {
                    deadTamaLabel.text = deadTamaLabel.text + " ${it.name},"
                } else {
                    tamaSelect.add(it.name)
                    displayTamaList.set(it.name, it)
                }
            }
        }
        updateTamaStates()
    }

    fun updateTamaStates() {
        currentTama = displayTamaList.get(tamaSelect.selectedItem)
        tamaStateLabel.text = currentTama?.state
        tamaReactionLabel.text = currentTama?.reaction
    }

    fun resetInputs() {
        actionInput.text = ""
        endText.text = ""
    }

    override fun actionPerformed(e: ActionEvent?) {
        if (e!!.source == sendButton && !responseSent) {
            val text = actionInput.text
            if (text == "quit") {
                close()
            } else {
                writer.println(text)
                responseSent = true
                resetInputs()
            }
            if (lastQuestion!!.trim() == "Entrez votre nom:" && text.trim() != "") {
                title = "Player ${text.trim()}"
            }
        }
        if (e.source == chooseButton && !responseSent) {
            val tama = tamaSelect.selectedIndex
            responseSent = true
            writer.println(tama)
        }
    }

    fun close() {
        writer.close()
        reader.close()
        client.close()
    }
}

