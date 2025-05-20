package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class IssuesTest : FunSpec({

    test("issue219") {
        class Test(val values: Array<Int>)
        val test: Test? = Klaxon().parse(""" { "values": [1,2,4] } """.trimIndent())!!
        test.shouldNotBeNull()
        test.values shouldBe arrayOf(1, 2, 4)
    }

    data class Product(
            val foo: Double? = 0.0
    )

    test("issue282") {
        val expected = 434343000000
        val r = Klaxon().parse<Product>("""{"foo": $expected}""")
        r.shouldNotBeNull()
        r.foo shouldBe expected.toDouble()
    }
})