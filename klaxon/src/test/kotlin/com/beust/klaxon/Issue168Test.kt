package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec


class Issue168Test : FunSpec({
    val jsonString = """
    {
        "data": [
            ["a", "b", null, "c"],
            ["d", "e", null, "f"]
        ]
    }
    """

    data class Data(
        val data: List<List<String?>>
    )

    test("issue168") {
        val klaxon = Klaxon()

        val parsed = klaxon.parse<Data>(jsonString)
    }
})