package com.beust.klaxon

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class Issue253Test : FunSpec( {

    data class ObjWithNullAttr(
            val myAttr: Int?
    )

    test("issue253") {
        val obj = ObjWithNullAttr(null)
        val jsonStr = Klaxon().toJsonString(obj)

        val trimmedStr = jsonStr.replace(" ", "")

        trimmedStr shouldBe """{"myAttr":null}"""
        shouldNotThrow<KlaxonException> {
            Klaxon().parse<ObjWithNullAttr>(jsonStr)
        }
    }
})
