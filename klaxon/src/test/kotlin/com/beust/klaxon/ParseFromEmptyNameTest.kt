package com.beust.klaxon

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class ParseFromEmptyNameTest : FunSpec({

    test("nameSetToEmptyString") {
        data class EmptyName (
                @Json(name = "")
                val empty: String)
        val sampleJson = """{"":"value"}"""
        val result = Klaxon().parse<EmptyName>(sampleJson)

        result.shouldNotBeNull()
        result.empty shouldBe "value"
    }

    test("nameSetToDefaultValue") {
        data class SpecificName (
                @Json(name = NAME_NOT_INITIALIZED)
                val oddName: String)
        val sampleJson = """{"$NAME_NOT_INITIALIZED":"value"}"""
        shouldThrow<KlaxonException> {
            Klaxon().parse<SpecificName>(sampleJson)
        }
    }
})
