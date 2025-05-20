package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.maps.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull


class Issue215Test : FunSpec({
    test("issue215") {
        val input = """{"hi" : "hello"}"""
        val map = Klaxon().parse<Map<String, String>>(input)
        map.shouldNotBeNull()
        map shouldContainExactly mapOf("hi" to "hello")
    }
})