package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class Issue237Test : FunSpec({

    data class PlainObject(
        val a: Int,
        @Json(name = "filed_b")
        val b: String,
        val c: Float
    )

    test("issue237") {
        val aPlainObject = PlainObject(10, "test string", 3.141659f)

        val aJsonArray = JsonArray(listOf(
            JsonObject(mapOf("testing" to "Json")),
            JsonObject(mapOf("Array" to "Objects")),
            aPlainObject
        ))

        val aMix = json {
            obj (
                "theArray" to aJsonArray,
                "secondArray" to array(listOf("testing", "JsonArray", "Objects", "again"))
            )
        }

        val str = Klaxon().toJsonString(aMix)
        str shouldBe "{\"theArray\": [{\"testing\": \"Json\"}, {\"Array\": \"Objects\"}, {\"a\" : 10, \"filed_b\" : \"test string\", \"c\" : 3.141659}], \"secondArray\": [\"testing\", \"JsonArray\", \"Objects\", \"again\"]}"
    }
})
