package com.beust.klaxon


import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.throwable.shouldHaveStackTraceContaining
import java.time.LocalDate

class Issue220Test : FunSpec() {
    private val dateTime = object : Converter {
        override fun canConvert(cls: Class<*>) = cls == LocalDate::class.java
        override fun fromJson(jv: JsonValue): LocalDate = LocalDate.parse(jv.string)
        override fun toJson(value: Any): String = "\"$value\""
    }

    // numberOfEyes contains a String instead of an Int
    private val referenceFailingTestFixture = """
    {
      "lastName": "Doe",
      "firstName": "Jane",
      "dateOfBirth": "1990-11-23",
      "numberOfEyes": "2"
    }
    """.trimIndent()

    data class Person(
        val lastName: String,
        val firstName: String,
        val dateOfBirth: LocalDate,
        val numberOfEyes: Int
    )

    init {
        test("displayMeaningfulErrorMessage") {
            val klaxon = Klaxon().apply {
                converter(dateTime)
            }

            // This should fail because numberOfEyes is a String instead of an Int
            val e = shouldThrowExactly<KlaxonException> {
                klaxon.parse<Person>(referenceFailingTestFixture)
            }
            e shouldHaveStackTraceContaining "Parameter numberOfEyes"

        }

    }
}