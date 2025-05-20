package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.types.shouldBeTypeOf
import java.math.BigDecimal


class Issue228Test : FunSpec({

    data class CurrencySnapshot(val rates: Map<String, BigDecimal>)

    test("issue228") {
        val parsed = Klaxon().parse<CurrencySnapshot>("{\"rates\":{\"EUR\":1,\"FJD\":2.434077,}}")
        listOf("FJD", "EUR").forEach {
            val v = parsed!!.rates[it]
            v.shouldNotBeNull()
            v.shouldBeTypeOf<BigDecimal>()
        }
    }
})