
package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class Issue210Test : FunSpec({
    data class DClass(val some: String, val none: String?)

    test("issue210: Tests that null JSON values are correctly handled") {
        val json = """
        {
        "some": "test",
        "none": null
        }
        """

        val p = Klaxon().parse<DClass>(json)
        p.shouldNotBeNull()
        p.none.shouldBeNull()

    }
})
