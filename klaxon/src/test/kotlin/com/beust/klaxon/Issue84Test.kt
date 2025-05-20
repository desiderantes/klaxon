package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.time.Instant

class Issue84Test : FunSpec({
    /**
     * This test passes.
     */
    test("serializeNestedInstant") {
        data class Person(val firstName: String, val dob: Instant)

        class EpochMilliInstantConverter: Converter {
            override fun canConvert(cls: Class<*>) = cls == Instant::class.java
            override fun toJson(value: Any) = (value as Instant).toEpochMilli().toString()
            override fun fromJson(jv: JsonValue) = throw NotImplementedError()
        }

        // No custom converter, should be the toString() of the instant
        val obj = Person("John", Instant.ofEpochMilli(9001))
        val result = Klaxon().toJsonString(obj)

        // Custom converter, expect the converted value
        val mapper = Klaxon().converter(EpochMilliInstantConverter())
        val result2 = mapper.toJsonString(obj)
        result2 shouldContain "9001"
    }

    test("serializeListOfInstants") {
        val dates = listOf(Instant.ofEpochMilli(9001))

        class EpochMilliInstantConverter: Converter{
            override fun canConvert(cls: Class<*>) = cls == Instant::class.java
            override fun toJson(value: Any) = (value as Instant).toEpochMilli().toString()
            override fun fromJson(jv: JsonValue) = throw NotImplementedError()
        }

        // despite custom converter being provided, instant is not serialized.  Empty value in list
        val mapper = Klaxon().converter(EpochMilliInstantConverter())
        val result = mapper.toJsonString(dates)
        result shouldContain "9001"
    }
})