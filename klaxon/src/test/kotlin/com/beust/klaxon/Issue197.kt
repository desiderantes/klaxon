package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.io.StringReader

data class Thinger(
        val width: Float?=null
)

class ThingerTest : FunSpec({
    test("issue197") {
        val input = """{"width": 2}"""
        val thinger = Klaxon().parse<Thinger>(StringReader(input))

        thinger.shouldNotBeNull()
        thinger.width shouldBe 2f
    }
})