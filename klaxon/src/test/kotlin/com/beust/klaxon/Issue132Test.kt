package com.beust.klaxon

import io.kotest.core.spec.style.AnnotationSpec


class Issue132Test : AnnotationSpec() {
    @Test(expected = KlaxonException::class)
    fun recursion() {
        class KNode(val next: KNode?)

        val converter = object : Converter {
            override fun canConvert(cls: Class<*>): Boolean = cls == Node::class.java

            override fun toJson(value: Any): String {
                return "string"
            }

            override fun fromJson(jv: JsonValue): Any {
                return ""
            }
        }
        Klaxon().converter(converter).parse<KNode>("{}")
    }
}