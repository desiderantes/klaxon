package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec


enum class THING {
    TYPE1,
    TYPE2
}

data class Data(val types: List<THING> = listOf(THING.TYPE1))


class Issue133Test : FunSpec({
    test("issue133") {
        val json = """{"types" : ["TYPE1"]}"""
        val instance = Klaxon().parse<Data>(json)!! //parse JSON without failure
        /*
            The line below will fail. The (correctly) inferred type is THING,
            but what we get back is type String.
         */
        val type = instance.types[0]
    }
})
