package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.reflect.KProperty


class PropertyStrategyTest : FunSpec() {
    fun runTest(enabled: Boolean) {
        data class Simple(val field1: String, val field2: String = "right")

        val ps = object : PropertyStrategy {
            override fun accept(property: KProperty<*>) = property.name != "field2"
        }
        val ps2 = object : PropertyStrategy {
            override fun accept(property: KProperty<*>) = true
        }
        val klaxon = Klaxon()
            .propertyStrategy(ps2)
        if (enabled) klaxon.propertyStrategy(ps)

        val r = klaxon.parse<Simple>(
            """
                { "field1": "b", "field2": "shouldBeIgnored" }
            """
        )
        if (enabled) {
            r shouldBe Simple("b", "right")
        } else {
            r shouldBe Simple("b", "shouldBeIgnored")
        }
    }

    init {

        test("test1") {
            runTest(true)
        }

        test("test2") {
            runTest(false)
        }
    }
}