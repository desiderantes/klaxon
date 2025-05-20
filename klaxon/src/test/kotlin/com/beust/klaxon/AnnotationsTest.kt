package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import kotlin.reflect.full.memberProperties

class AnnotationsTest : FunSpec() {


  sealed class Message(
    open val text: String,
    @Json(name = "message_type")
    val messageType: KlaxonTest2.MessageType
  ) {

    data class MessageReceived(
      val sender: String,
      override val text: String
    ) : Message(text, KlaxonTest2.MessageType.RECEIVED)
  }

  init {
        val prop = Message.MessageReceived::class.memberProperties.find { it.name == "messageType" }
        val json = Annotations.findJsonAnnotation(Message.MessageReceived::class, prop!!.name)
    json.shouldNotBeNull()
    }
}