package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class JsonAnnotationTest : FunSpec({
    val jsonString: String = json {
        obj(
            "name" to "John",
            "change" to 1
        )
    }.toJsonString()

    test("ignoredWithAnnotation") {
        class IgnoredWithAnnotation(
            val name: String,
            @Json(ignored = true)
            val change: Int = 0
        )

        val result = Klaxon().parse<IgnoredWithAnnotation>(jsonString)
        result.shouldNotBeNull()
        result.name shouldBe "John"
        result.change shouldBe 0
    }

    test("ignoredWithPrivate") {
        class IgnoredWithPrivate(
            val name: String,
            private val change: Int = 0
        ) {
            fun changed(): Boolean = change != 0
        }

        val result = Klaxon().parse<IgnoredWithPrivate>(jsonString)
        result.shouldNotBeNull()
        result.name shouldBe "John"
        result.changed() shouldBe false
    }

    test("privateNotIgnored") {
        data class Config(
            val version: String,
            @Json(ignored = false)
            private val projects: Set<String>
        ) {
            fun contains(name: String) = projects.contains(name)
        }

        val jsonString = """{"version": "v1", "projects": ["abc"]}"""
        val r = Klaxon().parse<Config>(jsonString)
        r.shouldNotBeNull()
        r shouldBeEqual Config("v1", setOf("abc"))

    }

    test("serializeNullFalseRoundtripWithoutDefault") {
        // when serializeNull == false, null is the default value during parsing
        data class ObjWithSerializeNullFalse(
            @Json(serializeNull = false)
            val value: Int?
        )

        val originalObj = ObjWithSerializeNullFalse(null)
        val serialized = Klaxon().toJsonString(originalObj)
        serialized shouldBe "{}" // with serializeNull = false, the null property is not serialized
        val parsed = Klaxon().parse<ObjWithSerializeNullFalse>(serialized)
        val expected = ObjWithSerializeNullFalse(null)
        parsed shouldBe expected
    }

    test("serializeNullFalseRoundtripWithDefault") {
        // Kotlin defaults are ignored when serializeNull == false and replaced with null during parsing
        data class ObjWithSerializeNullFalseAndDefault(
            @Json(serializeNull = false)
            val value: Int? = 1
        )

        val originalObj = ObjWithSerializeNullFalseAndDefault(null)
        val serialized = Klaxon().toJsonString(originalObj)
        serialized shouldBe "{}"
        val parsed = Klaxon().parse<ObjWithSerializeNullFalseAndDefault>(serialized)
        val expected = ObjWithSerializeNullFalseAndDefault(null)

        parsed shouldBe expected
    }

    test("serializeNullFalseValueSet") {
        data class ObjWithSerializeNullFalse(
            @Json(serializeNull = false)
            val value: Int?
        )

        val result = Klaxon().toJsonString(ObjWithSerializeNullFalse(1))
        result shouldBe """{"value" : 1}"""
    }

    test("serializeNullTrue") {
        data class ObjWithSerializeNullTrue(
            @Json(serializeNull = true)
            val value: Int?
        )

        var parsedString = Klaxon().toJsonString(ObjWithSerializeNullTrue(null))
        parsedString shouldBe """{"value" : null}"""

        parsedString = Klaxon().toJsonString(ObjWithSerializeNullTrue(1))

        parsedString shouldBe """{"value" : 1}"""
    }

    test("serializeNullWithoutNullableProperty") {
        data class ObjWithSerializeNullFalse(
            @Json(serializeNull = false)
            val value: Int = 1
        )

        val parsed = Klaxon().parse<ObjWithSerializeNullFalse>("{}")
        val expected = ObjWithSerializeNullFalse(1)

        parsed shouldBe expected
    }
})