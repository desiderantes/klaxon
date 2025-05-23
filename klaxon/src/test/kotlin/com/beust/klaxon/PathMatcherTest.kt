package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.StringReader
import java.util.regex.Pattern

class PathMatcherTest : FunSpec({

    /**
     * Illustrate how we can create a Kotlin object made of fields scattered
     * inside the JSON document.
     */
    test("scatteredObject") {
        data class UserWithZipCode(val name: String, val zipCode: Int)

        val pm = object: PathMatcher {
            val namePath = "$.description.person.name"
            val zipCodePath = "$.description.location.zipCode"
            val paths = setOf(namePath, zipCodePath)
            var name: String? = null
            var zipCode: Int? = null

            override fun pathMatches(path: String) = paths.contains(path)

            override fun onMatch(path: String, value: Any) {
                when(path) {
                    namePath -> name = value.toString()
                    zipCodePath -> zipCode = value as Int
                }
            }

        }
        val r = Klaxon()
            .pathMatcher(pm)
            .parseJsonObject(StringReader("""{
                "description": {
                    "person": {
                        "name": "John"
                    },
                    "location": {
                        "zipCode": 90210
                    }
                }
            }"""))
        val user = UserWithZipCode(pm.name!!, pm.zipCode!!)
        user shouldBe UserWithZipCode("John", 90210)
    }

    test("pathMatcher") {
        val po = object : PathMatcher {
            override fun pathMatches(path: String) = Pattern.matches(".*store.book.*author.*", path)

            val authors = arrayListOf<String>()
            override fun onMatch(path: String, value: Any) {
                authors.add(value.toString())
            }
        }
        Klaxon()
            .pathMatcher(po)
            .parseJsonObject(StringReader("""{
                "name": "John",
                "store": {
                    "book": [
                        {
                            "category": "reference",
                            "author": "Nigel Rees",
                            "title": "Sayings of the Century",
                            "price": 8.95
                        },
                        {
                            "category": "fiction",
                            "author": "Evelyn Waugh",
                            "title": "Sword of Honour",
                            "price": 12.99
                        },
                        {
                            "category": "fiction",
                            "author": "Herman Melville",
                            "title": "Moby Dick",
                            "isbn": "0-553-21311-3",
                            "price": 8.99
                        },
                        {
                            "category": "fiction",
                            "author": "J. R. R. Tolkien",
                            "title": "The Lord of the Rings",
                            "isbn": "0-395-19395-8",
                            "price": 22.99
                        }
                    ],
                    "bicycle": {
                        "color": "red",
                        "price": 19.95
                    }
                },
                "expensive": 10
            }"""))
        po.authors shouldContainExactly listOf("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien")
    }

    data class WithPath(
        val id: Int,
        @Json(path = "$.person.name")
        val name: String
    )

    test("fieldWithPath") {
        val result = Klaxon()
            .parse<WithPath>(StringReader("""{
                "id": 2,
                "person": {
                   "name": "John"
                }
            }"""))
        result shouldBe WithPath(2, "John")
    }

    data class Author(
        @Json(path = "$.people[0].authorName")
        val authorName: String
    )

    data class Book(
            @Json(path = "$.titles[1]")
            val title: String,
            val author: Author
    )


    data class Library(val titles: List<String>, val books: List<Book>, val people: List<Author>)

    xtest("fieldWithNestedPath") {
        val k = Klaxon()
        val result = k
//                .parseJsonObject(StringReader("""{
                .parse<Library>(StringReader("""{
                "titles": [
                  "Bad title",
                  "Hyperion"
                ],
                "book": {
                    "title": "Wrong title",
                    "author": {
                        "authorName": "Wrong author"
                    }
                },
                "people": [
                    { "authorName": "Simmons" }
                ],
            }"""))
        println("Result: $result")
//      result shouldBe Library(Book("Hyperion", Author("Simmons")))
    }
})
