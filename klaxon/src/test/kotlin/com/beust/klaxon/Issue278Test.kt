package com.beust.klaxon


import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal


class Issue278Test : FunSpec() {
    val BigDecimalConverter = object : Converter {
        override fun canConvert(cls: Class<*>): Boolean = cls == BigDecimal::class.java

        override fun fromJson(jv: JsonValue): Any? {
            println("JsonValue = ${jv}")
            return BigDecimal.valueOf(jv.longValue!!)
        }

        override fun toJson(value: Any): String {
            TODO("not implemented")
        }
    }

    init {
        test("issue278") {
            data class Economy (
                val nationalDebt : BigDecimal
            )
            val expected = 9007199254740991
            val json = "{ \"nationalDebt\" : $expected }"
            val klaxon = Klaxon().converter(BigDecimalConverter)
            val obj = klaxon.parse<Economy>(json)
            obj!!.nationalDebt shouldBe BigDecimal.valueOf(expected)
        }
    }
}