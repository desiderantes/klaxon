package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe

class PropertyOrderTest: FunSpec ({
    /**
     * Order the list based on the value ordering specified in order. The result string needs to contain
     * the values specified in order in the same order, while values in list but not in order will be
     * returned at the end of the list, in the same order they were in list.
     *
     * For example, "abcdef" ordered with "df" will produce "dfabce".
     */
    fun orderWithKeys(list: List<String>, order: List<String>): List<String> {
        val comparator = Comparator<String> { o1, o2 ->
            val index1 = order.indexOf(o1)
            // If the current value is not specified in the order, it stays where it is.
            if (index1 == -1) 1
            else {
                val index2 = order.indexOf(o2)
                // The current value is found in the order, compare its index to the second value
                // Either it's not found, or it's already in the right order: stay where it is
                if (index2 == -1 || index1 < index2) -1
                // Or it's found after in the order, swap
                else 1
            }
        }
        return list.sortedWith(comparator)
    }

    arrayOf(
            arrayOf(listOf("a", "b"), listOf("a", "b"), listOf("a", "b")),
            arrayOf(listOf("a", "b"), listOf("b", "a"), listOf("b", "a")),
            arrayOf(listOf("a", "b", "c"), listOf("a", "b"), listOf("a", "b", "c")),
            arrayOf(listOf("a", "b", "c"), listOf("b", "a"), listOf("b", "a", "c")),

            arrayOf(listOf("a", "b", "c", "d", "e", "f"), listOf("f", "e", "d"), listOf("f", "e", "d", "a", "b", "c")),
            arrayOf(listOf("a", "c", "e", "d", "f", "b"), listOf("f", "e", "d"), listOf("f", "e", "d", "a", "c", "b"))
    ).forEach { (list: List<String>, order: List<String>, expected: List<String>) ->
        test("orderWithKeys: $list $order") {
            val result = orderWithKeys(list, order)
            result shouldBe expected
        }
    }

    test("OrderWithIndex") {
        class Data(@Json(index = 1) val id: String,
                @Json(index = 2) val name: String)
        var result = Klaxon().toJsonString(Data("id", "foo"))

        result.indexOf("id").shouldBeLessThan(result.indexOf("name"))


        class Data2(@Json(index = 3) val id: String,
                @Json(index = 2) val name: String)

        result = Klaxon().toJsonString(Data2("id", "foo"))

        result.indexOf("name").shouldBeLessThan(result.indexOf("id"))

    }
})