package com.beust.klaxon

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec


class JazzerTest : FunSpec ({
    test("characterInNumericLiteral") {
        val json = "0r"
        shouldThrow<KlaxonException> {
            Parser.default().parse(StringBuilder(json))
        }
    }

    test("numericKeyAndObject") {
        val json = "{1{"
        shouldThrow<KlaxonException> {
            Parser.default().parse(StringBuilder(json))
        }
    }

    test("numericKeyAndArray") {
        val json = "{3["
        shouldThrow<KlaxonException> {
            Parser.default().parse(StringBuilder(json))
        }
    }

    test("numericKeyAndString") {
        val json = "{0\"\""
        shouldThrow<KlaxonException> {
            Parser.default().parse(StringBuilder(json))
        }
    }

    test("incompleteUnicodeEscape") {
        val json = "\"\\u"
        shouldThrow<KlaxonException> {
            Parser.default().parse(StringBuilder(json))
        }
    }

    test("nonNumericUnicodeEscape") {
        val json = "\"\\u\\\\{["
        shouldThrow<KlaxonException> {
            Parser.default().parse(StringBuilder(json))
        }
    }
})
