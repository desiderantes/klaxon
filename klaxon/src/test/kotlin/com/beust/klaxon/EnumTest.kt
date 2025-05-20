package com.beust.klaxon

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


class EnumTest : AnnotationSpec(){

    val convertColor = object: Converter {
        override fun canConvert(cls: Class<*>) = cls == Color::class.java

        override fun toJson(value: Any): String = when(value as Color) {
            Color.R -> "red"
            Color.G -> "green"
            Color.B -> "blue"
        }

        override fun fromJson(jv: JsonValue): Color = when(jv.inside) {
            "red" -> Color.R
            "green" -> Color.G
            "blue" -> Color.B
            else -> throw IllegalArgumentException("Invalid Color")
        }
    }

    enum class Color { R, G, B }
    data class Root (val colors: List<Color>)

    @Test
    fun listOfEnums() {
        val klaxon = Klaxon().converter(convertColor)
        val result = klaxon.parse<Root>("""
        {
            "colors": ["red", "green", "blue"]
        }
        """)
    }

    enum class Cardinal { NORTH, SOUTH }
    class Direction(var cardinal: Cardinal? = null)
    @Test
    fun enum() {
        val result = Klaxon().parse<Direction>("""
            { "cardinal": "NORTH" }
        """
        )
        result.shouldNotBeNull()
        result.cardinal shouldBe Cardinal.NORTH
    }


}