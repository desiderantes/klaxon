package com.beust.klaxon;

import com.beust.klaxon.jackson.jackson
import io.kotest.core.spec.style.FunSpec
import java.math.BigInteger
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class TestTypes : FunSpec({

    listOf("default" to Parser.default(), "jackson" to Parser.jackson()).forEach { (parserName, parser) ->
        fun read(name: String): Any? {
            val cls = TestTypes::class.java
            return parser.parse(cls.getResourceAsStream(name)!!)
        }

        fun getJsonObject(): JsonObject {
            return read("/types.json") as JsonObject
        }


        test("$parserName : typeInt") {
            val j = getJsonObject()
            assertEquals(123456789, j.int("int_value"))
        }

        test("$parserName : typeLong") {
            val j = getJsonObject()
            assertEquals(2147483649, j.long("long_value"))
        }

        test("$parserName : typeBigint") {
            val j = getJsonObject()
            assertEquals(BigInteger("123456789123456789123456789"), j.bigInt("bigint_value"))
        }

        test("$parserName : typeBoolean") {
            val j = getJsonObject()
            assertEquals(false, j.boolean("boolean_value"))
        }

        test("$parserName : typeFloat") {
            val j = getJsonObject()
            assertEquals(12.34f, j.float("float_value"))
        }

        test("$parserName : typeFloatExp") {
            val j = getJsonObject()
            assertEquals(3.141E-10f, j.float("float_exp_value"))
        }

        test("$parserName : typeString") {
            val j = getJsonObject()
            assertEquals("foo-bar", j.string("string_value"))
        }

        test("$parserName : typeUnicode") {
            val j = getJsonObject()
            assertEquals("foo\u20ffbar", j.string("unicode_value"))
        }

        test("$parserName : typeUnescapedUnicode") {
            val j = getJsonObject()
            val actual = j.string("unicode_unescaped_value")
            assertNotNull(actual)
            assertEquals(0x00FA, actual[0].code)
            assertEquals(0x00FA, actual[1].code)
            assertEquals("úú", actual)
        }

        test("$parserName : typeEscape") {
            val j = getJsonObject()
            assertEquals("[\"|\\|/|\b|\u000c|\n|\r|\t]", j.string("escape_value"))
        }

        test("$parserName : typeObject") {
            val j = getJsonObject()
            assertEquals(JsonObject(), j.obj("object_value"))
        }

        test("$parserName : typeArray") {
            val j = getJsonObject()
            assertEquals(JsonArray<Any>(), j.array<Any>("array_value"))
        }

        test("$parserName : typeNull") {
            val j = getJsonObject()
            assertNull(j["null_value"])
        }

        test("$parserName : testEscapeRender") {
            val j = read("/escaped.json") as JsonObject
            assertEquals(
                """{"text field \"s\"\nnext line\fform feed\ttab\\rev solidus/solidus\bbackspace\u2018":"text field \"s\"\nnext line\fform feed\ttab\\rev solidus/solidus\bbackspace\u2018"}""",
                j.toJsonString()
            )
        }
    }
})
