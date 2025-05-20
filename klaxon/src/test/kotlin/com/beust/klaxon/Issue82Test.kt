package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain

class Issue82Test : FunSpec({

    fun assertTest(jv: String) {
        jv shouldContain "firstName"
        jv shouldContain "John"
        jv shouldNotContain "id"
        jv shouldNotContain "1"
    }

    test("serializePrivateVal") {
        data class Person(private val id: String, val firstName: String)
        val obj = Person("1", "John")
        assertTest(Klaxon().toJsonString(obj))
    }

    /**
     * Ignoring a field with the @Json annotation does nothing
     * Test fails.  Serialized output is actually "{\"firstName\" : \"John\", \"id\" : \"1\"}"
     */
    test("serializeIgnoredVal") {
        data class Person(@Json(ignored=true) val id: String, val firstName: String)
        val obj = Person("1", "John")
        assertTest(Klaxon().toJsonString(obj))
    }


})