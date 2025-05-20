package com.beust.klaxon


import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import java.math.BigDecimal

class Issue236Test : FunSpec({
    test("issue236Decimal") {
        data class Poke(
                var block: Long = 0L,
                var value: BigDecimal = BigDecimal.ZERO // Could be String/Long/Int/doesntmatter

        )

        val json = """
        [
            {
                "block": 7102460,
                "value": 114470000000000000000.0
            },
            {
                "block": 7102393,
                "value": 114455000000000000000.0
            }
        ]
        """.trimIndent()

        val poke = Klaxon().parseArray<Poke>(json)
        poke.shouldNotBeNull()
        poke[0] shouldBe Poke(7102460, BigDecimal(114470000000000000000.0))
        poke[1] shouldBe Poke(7102393, BigDecimal(114455000000000000000.0))
    }
})
