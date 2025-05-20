package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class Issue216Test : FunSpec({
    test("issue216") {
        val m = mapOf("x" to "y", "n" to null)
        val result = Klaxon().toJsonString(m)
        result shouldBe """{"x": "y", "n": null}"""
    }
})