package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class NullValueTest: FunSpec({
    test("null String") {
        Klaxon().toJsonString(listOf(1, 2, null, null, 3)) shouldBe "[1, 2, null, null, 3]"
    }
})
