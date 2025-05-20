package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class Issue95Test: FunSpec({
    test("deserializeStringArray") {
        val mapper = Klaxon()
        val data = listOf("foo", "bar", "baz")
        val json = mapper.toJsonString(data)
        mapper.parseArray<String>(json) shouldBe data
    }

    test("deserializeIntArray") {
        val mapper = Klaxon()
        val data = listOf(1, 2, 3)
        val json = mapper.toJsonString(data)
        mapper.parseArray<Int>(json) shouldBe data
    }

    test("deserializeObjectArray") {
        val mapper = Klaxon()
        data class Person(val name: String)
        val data = listOf(Person("John"), Person("Jane"))
        val json = mapper.toJsonString(data)
        mapper.parseArray<Person>(json) shouldBe data
    }


    test("serializeStringArrayToObjectArray") {
        data class Person(val id: String, val name: String)
        class PersonConverter: Converter {
            override fun canConvert(cls: Class<*>) = cls == Person::class.java
            override fun toJson(value: Any) = (value as Person).let { value -> "\"${value.id}:${value.name}\"" }
            override fun fromJson(jv: JsonValue): Person {
                val (id, name) = jv.string!!.split(":")
                return Person(id, name)
            }
        }

        val data = listOf(Person("1", "John"), Person("2", "Jane"))
        val mapper = Klaxon().converter(PersonConverter())
        val json = mapper.toJsonString(data)
        val result = mapper.parseArray<Person>(json)
        result.shouldNotBeNull()
        result shouldBe data
    }
})
