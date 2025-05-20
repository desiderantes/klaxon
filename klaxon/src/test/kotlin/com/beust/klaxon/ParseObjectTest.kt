package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class ParseObjectTest : FunSpec( {
    test ("parsing an object") {
        Klaxon().parse<Foo>("{}") shouldBe Foo
    }
}) {
    object Foo
}
