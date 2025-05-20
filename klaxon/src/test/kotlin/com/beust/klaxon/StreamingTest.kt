package com.beust.klaxon

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.StringReader
import java.math.BigInteger


class StreamingTest : FunSpec({

    test("streamingObject") {
        val objectString = """{
             "name": "Joe", "age": 23, "height": 1.85, "flag": true, "array": [1, 3],
             "obj1": { "a":1, "b":2 }
        }"""

        JsonReader(StringReader(objectString)).use { reader ->
            reader.beginObject {
                var name: String? = null
                var age: Int? = null
                var height: Double? = null
                var flag: Boolean? = null
                var array: List<Any> = arrayListOf<Any>()
                var obj1: JsonObject? = null
                val expectedObj1 = JsonObject().apply {
                    this["a"] = 1
                    this["b"] = 2
                }
                while (reader.hasNext()) {
                    val readName = reader.nextName()
                    shouldNotThrow<Exception> {
                        when (readName) {
                            "name" -> name = reader.nextString()
                            "age" -> age = reader.nextInt()
                            "height" -> height = reader.nextDouble()
                            "flag" -> flag = reader.nextBoolean()
                            "array" -> array = reader.nextArray()
                            "obj1" -> obj1 = reader.nextObject()
                            else -> throw IllegalArgumentException("Expected either \"name\" or \"age\" but got $name")
                        }
                    }
                }
                name shouldBe "Joe"
                age shouldBe 23
                height shouldBe 1.85
                flag shouldBe true
                array shouldContainExactly listOf(1, 3)
                obj1 shouldBe expectedObj1
            }

        }
    }

    data class Person1(val name: String, val age: Int)
    val array = """[
            { "name": "Joe", "age": 23 },
            { "name": "Jill", "age": 35 }
        ]"""
    test("streamingArray") {
        val klaxon = Klaxon()
        JsonReader(StringReader(array)).use { reader ->
            val result = arrayListOf<Person1>()
            reader.beginArray {
                while (reader.hasNext()) {
                    val person = klaxon.parse<Person1>(reader)
                    result.add(person!!)
                }
                result shouldBe listOf(Person1("Joe", 23), Person1("Jill", 35))
            }
        }
    }

    data class Address(val street: String)
    data class Person2(val name: String, val address:Address)
    test("nestedObjects") {
        val objectString = """[
            { "name": "Joe", "address": { "street": "Klaxon Road" }}
        ]""".trimIndent()

        val klaxon = Klaxon()
        JsonReader(StringReader(objectString)).use { reader ->
            val result = arrayListOf<Person2>()
            reader.beginArray {
                while (reader.hasNext()){
                    val person = klaxon.parse<Person2>(reader)
                    result.add(person!!)
                }
            }
            result[0] shouldBe  Person2("Joe", Address("Klaxon Road"))
        }
    }

    val arrayInObject = """{ "array": [
            { "name": "Joe", "age": 23 },
            { "name": "Jill", "age": 35 }
        ] }"""

    test("streamingArrayInObject") {
        val klaxon = Klaxon()
        JsonReader(StringReader(arrayInObject)).use { reader ->
            val result = arrayListOf<Person1>()
            reader.beginObject {
                val name = reader.nextName()
                name shouldBe "array"

                reader.beginArray {
                    while (reader.hasNext()) {
                        val person = klaxon.parse<Person1>(reader)
                        result.add(person!!)
                    }

                    result shouldBe  listOf(Person1("Joe", 23), Person1("Jill", 35))
                }
            }
        }
    }



    fun assertParsingExceptionFromArray(json: String, nextValue: (JsonReader) -> Unit) {
        JsonReader(StringReader(json)).use { reader ->
            reader.beginArray {
                shouldThrow <JsonParsingException> {
                    nextValue(reader)
                }
            }
        }

    }

    test("testNextString") {
        // String read normally
        JsonReader(StringReader("[\"text\"]")).use { reader ->
            val actual = reader.beginArray { reader.nextString() }
            actual shouldBe "text"
        }
    }

    listOf(
        "[null]", // null
        "[true]", // Boolean
        "[123]", // Int
        "[9223372036854775807]", // Long
        "[0.123]", // Double
    ).forEach {
        test("testStringInvalidInput (nonStringValue: $it)") {
            assertParsingExceptionFromArray(it) { reader ->
                reader.nextString()
            }
        }
    }

    test("testNextInt") {
        // Int read normally
        JsonReader(StringReader("[0]")).use { reader ->
            val actual = reader.beginArray { reader.nextInt() }
            actual shouldBe 0
        }
    }


    listOf(
        "[null]", // null
        "[true]", // Boolean
        "[\"123\"]", // String
        "[9223372036854775807]", // Long
        "[0.123]", // Double
    ).forEach {
        test("testIntInvalidInput (nonIntValue: $it)") {
            assertParsingExceptionFromArray(it) { reader ->
                reader.nextInt()
            }
        }
    }

    test("estNextLong") {
        // Integer values should be auto-converted
        JsonReader(StringReader("[0]")).use { reader ->
            val actual = reader.beginArray { reader.nextLong() }
            actual shouldBe 0L
        }

        // Long read normally
        JsonReader(StringReader("[9223372036854775807]")).use { reader ->
            val actual = reader.beginArray { reader.nextLong() }
            actual shouldBe Long.MAX_VALUE
        }
    }

    listOf(
        "[null]", // null
        "[true]", // Boolean
        "[\"123\"]", // String
        "[0.123]", // Double
    ).forEach {
        test("testLongInvalidInput (nonLongValue: $it)") {
            assertParsingExceptionFromArray(it) { reader ->
                reader.nextLong()
            }
        }
    }

    test("testNextBigInteger") {
        // Integer values should be auto-converted
        JsonReader(StringReader("[0]")).use { reader ->
            val actual = reader.beginArray { reader.nextBigInteger() }
            actual shouldBe BigInteger.valueOf(0)
        }

        // Long values should be auto-converted
        JsonReader(StringReader("[9223372036854775807]")).use { reader ->
            val actual = reader.beginArray { reader.nextBigInteger() }

            actual shouldBe BigInteger.valueOf(Long.MAX_VALUE)
        }

        // Long read normally
        JsonReader(StringReader("[9223372036854775808]")).use { reader ->
            val actual = reader.beginArray { reader.nextBigInteger() }
            actual shouldBe BigInteger.valueOf(Long.MAX_VALUE) + BigInteger.valueOf(1)
        }
    }


    listOf(
        "[null]", // null
        "[true]", // Boolean
        "[\"123\"]", // String
        "[0.123]", // Double
    ).forEach {
        test("testBigIntegerInvalidInput (nonBigIntegerValue: $it)") {
            assertParsingExceptionFromArray(it) { reader ->
                reader.nextBigInteger()
            }
        }
    }


    test("testNextDouble") {
        // Integer values should be auto-converted
        JsonReader(StringReader("[0]")).use { reader ->
            val actual = reader.beginArray { reader.nextDouble() }
            actual shouldBe 0.0
        }

        // Native doubles
        JsonReader(StringReader("[0.123]")).use { reader ->
            val actual = reader.beginArray { reader.nextDouble() }
            actual shouldBe 0.123
        }
    }

    listOf(
        "[null]", // null
        "[true]", // Boolean
        "[\"123\"]", // String
        "[\"NAN\"]", // NAN is not really specified
        "[9223372036854775807]", // Long
    ).forEach {
        test("testDoubleInvalidInput (nonDoubleValue: $it)") {
            assertParsingExceptionFromArray(it) { reader ->
                reader.nextDouble()
            }
        }
    }

    test("testNextBoolean") {
        // true read normally
        JsonReader(StringReader("[true]")).use { reader ->
            val actual = reader.beginArray { reader.nextBoolean() }
            actual shouldBe true
        }

        // false read normally
        JsonReader(StringReader("[false]")).use { reader ->
            val actual = reader.beginArray { reader.nextBoolean() }
            actual shouldBe false
        }
    }

    listOf(
        "[null]", // null
        "[\"123\"]", // String
        "[\"true\"]", // true as a String
        "[\"false\"]", // false as a String
        "[123]", // Int
        "[9223372036854775807]", // Long
        "[0.123]", // Double
    ).forEach {
        test("testBooleanInvalidInput (nonBooleanValue: $it)") {
            assertParsingExceptionFromArray(it) { reader ->
                reader.nextBoolean()
            }
        }
    }


//    fun streaming1() {
//        val reader = JsonReader(StringReader(array))//FileReader("src/test/resources/generated.json"))
//        reader.beginArray()
//        val gson = Gson()
////        gson.fromJson<>()
//        while (reader.hasNext()) {
//            val person = gson.fromJson<Person>(reader, Person::class.java)
//            println("Person:" + person)
//        }
//        reader.endArray()
//    }
})
