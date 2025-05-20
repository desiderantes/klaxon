
package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class Issue311Test : FunSpec() {
    enum class ProtocolState {
        WAITING {
            override fun signal() = TALKING
        },
        TALKING {
            override fun signal() = WAITING
        };
        abstract fun signal(): ProtocolState
    }

    class Klass(val state: ProtocolState)

    init {
        test("Issue 311: Serializing Enum values with their own anonymous classes") {
            val klass = Klass(ProtocolState.WAITING)
            val json = Klaxon().toJsonString(klass)
            json shouldBe """{"state" : "WAITING"}"""
        }
    }
}
