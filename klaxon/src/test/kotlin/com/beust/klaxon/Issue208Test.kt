package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain


class KlaxonTest2 : FunSpec() {

    enum class MessageType {
        RECEIVED, SENDING
    }

    sealed class Message(
        open val text: String,
        @Json(name = "message_type")
        val messageType: MessageType
    ) {

        data class MessageReceived(
            val sender: String,
            override val text: String
        ) : Message(text, MessageType.RECEIVED)

        data class MessageSending(
            val recipient: String,
            override val text: String
        ) : Message(text, MessageType.SENDING)
    }

    init {
        test("testEncoding") {
            val klaxon = Klaxon()
            val received = Message.MessageReceived("Alice", "Hello")
            val receivedString = klaxon.toJsonString(received)
            receivedString shouldContain "message_type"
        }
    }
}

class KlaxonTest3 : FunSpec() {

    enum class MessageType {
        RECEIVED, SENDING
    }

    abstract class Message(
        open val text: String,
        @Json(name = "message_type")
        val messageType: MessageType
    )

    data class MessageReceived(
        val sender: String,
        override val text: String
    ) : Message(text, MessageType.RECEIVED)

    data class MessageSending(
        val recipient: String,
        override val text: String
    ) : Message(text, MessageType.SENDING)

    init {
        test("testEncoding") {
            val klaxon = Klaxon()
            val received = MessageReceived("Alice", "Hello")
            val receivedString = klaxon.toJsonString(received)
            receivedString shouldContain "message_type"
        }
    }
}