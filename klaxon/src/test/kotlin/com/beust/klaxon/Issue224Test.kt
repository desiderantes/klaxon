package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class Issue224Test : FunSpec() {
    class Data1(val value: List<List<Double>>) {
        override fun toString() = "[${value.joinToString { "[${it.joinToString()}]" }}]"
    }

    class Data2(val value: List<List<List<Double>>>) {
        override fun toString() = "[${value.joinToString { "[${it.joinToString { "[${it.joinToString()}]" }}]" }}]"
    }

    init {

        test("issue224") {
            val klaxon = Klaxon()

            val input1 = Data1(listOf(listOf(1.0, 1.1, 1.2)))
            input1.toString() shouldBe "[[1.0, 1.1, 1.2]]"

            val input2 = Data2(listOf(listOf(listOf(1.0, 1.1, 1.2))))
            input2.toString() shouldBe "[[[1.0, 1.1, 1.2]]]"

            val json1 = klaxon.toJsonString(input1)
            json1 shouldBe "{\"value\" : [[1.0, 1.1, 1.2]]}"

            val json2 = klaxon.toJsonString(input2)
            json2 shouldBe "{\"value\" : [[[1.0, 1.1, 1.2]]]}"

            val output1 = klaxon.parse<Data1>(json1)
            output1.toString() shouldBe "[[1.0, 1.1, 1.2]]"

            val output2 = klaxon.parse<Data2>(json2)
            output2.toString() shouldBe "[[[1.0, 1.1, 1.2]]]"
        }
    }
}