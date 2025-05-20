package com.beust.klaxon


import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Issue221Test : FunSpec() {
    class WithDate constructor(val name: String, @ESDate val birth: LocalDate)

    @Target(AnnotationTarget.FIELD)
    annotation class ESDate

    private val dateConverter = object : Converter {
        override fun canConvert(cls: Class<*>) = cls == LocalDate::class.java
        override fun fromJson(jv: JsonValue) = throw KlaxonException("Couldn't parse date: ${jv.string}")
        override fun toJson(value: Any) = (value as LocalDate).format(DateTimeFormatter.ofPattern("Y/M/d"))
    }

    init {

        test("issue221") {
            val k = Klaxon().fieldConverter(ESDate::class, dateConverter)
            val s = k.toJsonString(WithDate("hha", LocalDate.of(2018, 11, 30)))
            s shouldContain "2018/11/30"
        }
    }
}
