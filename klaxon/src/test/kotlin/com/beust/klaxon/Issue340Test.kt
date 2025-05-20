package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.util.UUID


class Issue340Test : FunSpec({

    test("issue340") {
        val data:Map<Any, Any?> = mapOf(
            12 to "dummy",
            "test" to "123",
            "test2" to 123,
            23.12 to 12,
            UUID.fromString("418ef170-f770-4172-8a19-2990e65c6fda") to "abc",
        )

        val json = JsonObject(data as Map<String, Any?>)

        val jsonString = Klaxon().toJsonString(json)

        val expected = """{"12": "dummy", "test": "123", "test2": 123, "23.12": 12, "418ef170-f770-4172-8a19-2990e65c6fda": "abc"}"""

        jsonString shouldBe expected
    }
})
