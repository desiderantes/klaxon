package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec


data class TestObject(
    val a: Int,
    @Json(name = "filed_b")
    val b: String,
    val c: Float)

class Issue167Test : FunSpec({
    test("issue167") {
        val aJsonObject = json {
            obj("a" to 1, "b" to "value")
        }

        val anArray = json {
            array("a", 1, false)
        }

        val aPlainObject = TestObject(10, "test string", 3.141659f)

        val aMix = json {
            obj (
                "theArray" to anArray,
                "theObject" to aJsonObject,
                "thePlainObject" to aPlainObject,   // This entry would previously fail with IllegalArgumentException
                "anInt" to 4
            )
        }
    }
})
