package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class Issue169Test : FunSpec({

    data class Person(val id: Int,
                      val name: String,
                      val isEUResident: Boolean = false,
                      val city: String = "Paris"
                     )

    val expected = Person(id = 2,
                                  name = "Arthur")

    test("issue169") {

        // language=JSON
        val jsonToTest = """
            {
              "id": 2,
              "name": "Arthur"
            }
        """.trimIndent()

        val toTest = Klaxon().parse<Person>(jsonToTest)!!

        toTest.city.shouldNotBeNull()
        toTest shouldBe expected
    }
})