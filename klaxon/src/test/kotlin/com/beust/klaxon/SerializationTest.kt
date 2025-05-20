package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

enum class Sauce { ONION }

class SerializationTest : FunSpec({

    val klaxon = Klaxon()

    fun serializationTest(expected: String, actual: Any) {
        val actualSerialization = klaxon.toJsonString(actual)
        actualSerialization shouldBe expected
    }

    test("int") {
        serializationTest("1", 1)
    }
    test("float") {
        serializationTest("0.55", 0.55f)
    }
    test("double") {
        serializationTest("0.332", 0.332)
    }
    test("boolean") {
        serializationTest("true", true)
    }
    test("char") {
        serializationTest("\"a\"", 'a')
    }
    test("byte") {
        serializationTest("1", 1.toByte())
    }
    test("short") {
        serializationTest("1", 1.toShort())
    }
    test("long") {
        serializationTest("200100", 200100L)
    }
    test("string") {
        serializationTest("\"Onion Sauce !\"", "Onion Sauce !")
    }
    test("enum") {
        serializationTest("\"ONION\"", Sauce.ONION)
    }
    test("collection") {
        val collection = listOf("mole", "ratty", "badger", "toad")
        serializationTest("[\"mole\", \"ratty\", \"badger\", \"toad\"]", collection)
    }
    test("map") {
        val map = mapOf(1 to "uno", 2 to "dos", 3 to "tres")
        serializationTest("{\"1\": \"uno\", \"2\": \"dos\", \"3\": \"tres\"}", map)
    }
    test("array") {
        val arrStrings = arrayOf("uno", "dos", "tres")
        serializationTest("[\"uno\", \"dos\", \"tres\"]", arrStrings)

        val arrPairs = arrayOf(Pair(1, "uno"), Pair(2, "dos"), Pair(3, "tres"))
        serializationTest("[{\"first\" : 1, \"second\" : \"uno\"}, {\"first\" : 2, \"second\" : \"dos\"}, {\"first\" : 3, \"second\" : \"tres\"}]", arrPairs)
    }
})
