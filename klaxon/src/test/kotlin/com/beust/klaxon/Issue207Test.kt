package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class Issue207Test : FunSpec() {
    private val klaxon = Klaxon()

    interface Message {
        val text: String
    }

    data class MessageReceived(
        val sender: String, override val text: String
    ) : Message

    data class MessageToSend(
        val recipient: String, override val text: String
    ) : Message


    data class Root(val id: Long, val message: Message)

    init {
        test("testIssue297Received") {
            val jsonString = """
        {
            "sender":"Alice",
            "text":"Hello"
        }
        """.trimIndent()
            val root = klaxon.parse<MessageReceived>(jsonString)
            root.shouldNotBeNull()
            root.sender shouldBe "Alice"
            root.text shouldBe "Hello"
        }

        test("testIssue297Sending") {
            val jsonString = """
        {
            "recipient":"Bob",
            "text":"Hello"
        }
        """.trimIndent()
            val root = klaxon.parse<MessageToSend>(jsonString)
            root.shouldNotBeNull()
            root.recipient shouldBe "Bob"
            root.text shouldBe "Hello"
        }
    }
}