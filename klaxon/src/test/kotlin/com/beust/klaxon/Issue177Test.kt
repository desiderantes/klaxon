package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class Issue177Test : FunSpec({

    data class UserData(val id: Int,
                      val name: String,
                      val role: String,
                      val additionalRole: String? = ""
                     )

    val expected = UserData(1, "Jason", "SuperUser", null)

    test("issue177") {

        // language=JSON
        val jsonToTest = """
            {
              "id": 1,
              "name": "Jason",
              "role": "SuperUser",
              "additionalRole": null
            }
        """.trimIndent()

        val toTest = Klaxon().parse<UserData>(jsonToTest)

        toTest.shouldNotBeNull()

        toTest.additionalRole.shouldBeNull()

        toTest shouldBe expected

    }
})