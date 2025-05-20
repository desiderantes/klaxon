package com.beust.klaxon

import com.beust.klaxon.token.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import java.io.StringReader


class LexerTest : FunSpec({

    fun value(name: String, value: Any): Array<Token>
        = arrayOf(Value(name), COLON, Value(value))

    val expected = listOf(
        LEFT_BRACE,
        *value("a", 1),
        COMMA,
        *value("ab", 1),
        COMMA,
        *value("ab", 12),
        RIGHT_BRACE
    )
    
    fun testLexer(lexer: Lexer) {
        val result = Sequence{ -> lexer }.map { it }.toList()
        result shouldContainExactly expected
    }
    test("basic") {
        val s = """{
            "a": 1,
            "ab": 1,
            "ab": 12
        }"""
        testLexer(Lexer(StringReader(s)))
    }

    test("lenient") {
        val s = """{
            a : 1,
            ab: 1,
            ab: 12
            }
            """
        testLexer(Lexer(StringReader(s), lenient = true))
    }
})