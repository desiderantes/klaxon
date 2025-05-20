package com.beust.klaxon

import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain


class PrettyPrintTest : FunSpec ({
    test("shouldDisplayInts") {
        val test2 = json { JsonObject(mapOf(
                "test" to mapOf<String, String>(),
                "2" to mapOf("test" to 22)
        )) }
        val string = test2.toJsonString(true)

        withClue("22 should be displayed as an Int, not a String") {
            string shouldContain "22"
        }

    }
})
