package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe


class Issue149 : FunSpec({
    test("issue149") {
        val nodes = """{
            "nodes": {
                "1": {
                    "x": 123.3236517713532,
                    "y": 12.932982478667043
                },
                "2": {
                    "x": 295.5807797559159,
                    "y": 166.55006043069116
                },
                "92": {
                    "x": 2.4180219278016875,
                    "y": 311.4574411103677
                },
                "93": {
                    "x": 1.085066656019426,
                    "y": 324.7411280720195
                }
        }}
        """

        data class Node(val x: Double, val y: Double)
        data class Floor(val nodes: HashMap<String, Node>)

        val r = Klaxon().parse<Floor>(nodes)
        r!!.nodes["1"] shouldBe Node(123.3236517713532, 12.932982478667043)
    }

})