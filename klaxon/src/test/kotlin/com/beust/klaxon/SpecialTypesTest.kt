package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class SpecialTypesTest : FunSpec({
    class MyEntity(
        @Json(name = "foo", ignored = true)
        var myFoo : String = "abc",

        @Json(name = "bar")
        var myBar : String
    )

    test("map") {
        val o = Klaxon().parse<Map<String, Any>>("""
            {
               "bar": "def"
               "entity": {
                   "bar": "isBar"
               }
            }
        """)
        o.shouldNotBeNull()
        o.keys shouldHaveSize 2
        o shouldContain ("bar" to "def")
        (o["entity"] as JsonObject)["bar"] shouldBe "isBar"
    }


})