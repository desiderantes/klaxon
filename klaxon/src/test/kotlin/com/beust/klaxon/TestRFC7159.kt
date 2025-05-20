package com.beust.klaxon;

import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrowWithMessage
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equals.shouldBeEqual
import java.io.ByteArrayInputStream

class TestRFC7159 : FunSpec(){

    private fun fromJsonString(jsonData: String) : Any? =
        Parser.default().parse(ByteArrayInputStream(jsonData.toByteArray(Charsets.UTF_8)))


    private fun jsonEquals(testData: String, expectedData: String) {
        val j = fromJsonString(testData)
        when(j) {
            is JsonObject -> j.toJsonString() shouldBeEqual expectedData
            is JsonArray<*> -> j.toJsonString() shouldBeEqual expectedData
            else -> fail("not an object or array")
        }
    }

    fun objectEmpty() {
        jsonEquals("{}", "{}")
    }

    fun objectStringValue() {
        jsonEquals("{ \"v\":\"1\"}", "{\"v\":\"1\"}")
    }

    fun objectIntValue() {
        jsonEquals("{ \"v\":1}", "{\"v\":1}")
    }

    fun objectQuote() {
        jsonEquals("{ \"v\":\"ab'c\"}", "{\"v\":\"ab'c\"}")
    }

    fun objectFloat() {
        jsonEquals("{ \"PI\":3.141E-10}", "{\"PI\":3.141E-10}")
    }

    fun objectFloatLowerCase() {
        jsonEquals("{ \"PI\":3.141e-10}", "{\"PI\":3.141E-10}")
    }

    fun objectLongNumber() {
        jsonEquals("{ \"v\":12345123456789}", "{\"v\":12345123456789}")
    }

    fun objectBigInt() {
        jsonEquals("{ \"v\":123456789123456789123456789}", "{\"v\":123456789123456789123456789}")
    }

    fun arrayDigits() {
        jsonEquals("[ 1,2,3,4]", "[1,2,3,4]")
    }

    fun arrayStrings() {
        jsonEquals("[ \"1\",\"2\",\"3\",\"4\"]", "[\"1\",\"2\",\"3\",\"4\"]")
    }

    fun arrayObjects() {
        jsonEquals("[ { }, { },[]]", "[{},{},[]]")
    }

    fun objectLowerCaseUnicode() {
        jsonEquals("{ \"v\":\"\\u2000\\u20ff\"}", "{\"v\":\"\\u2000\\u20ff\"}")
    }

    fun objectUpperCaseUnicode() {
        jsonEquals("{ \"v\":\"\\u2000\\u20FF\"}", "{\"v\":\"\\u2000\\u20ff\"}")
    }

    fun objectNonProtectedText() {
        jsonEquals("{ \"a\":\"hp://foo\"}", "{\"a\":\"hp://foo\"}")
    }

    fun unicodeUnescaped() {
        jsonEquals("{\"a\":\"ú\"}", "{\"a\":\"ú\"}")
    }

    fun objectNullValue() {
        jsonEquals("{ \"a\":null}", "{\"a\":null}")
    }

    fun objectBooleanValue() {
        jsonEquals("{ \"a\":true}", "{\"a\":true}")
    }

    fun objectNonTrimmedData() {
        jsonEquals("{ \"a\" : true }", "{\"a\":true}")
    }

    fun objectDoublePrecisionFloatingPoint() {
        jsonEquals("{ \"v\":${Double.MAX_VALUE}}", "{\"v\":${Double.MAX_VALUE}}")
    }

    init {
        test("truncated value") {
            shouldThrowWithMessage<RuntimeException>("Unterminated string") {
                fromJsonString("{\"X\":\"s")
            }
        }

        test("truncated key") {
            shouldThrowWithMessage<RuntimeException>("Unterminated string") {
                fromJsonString("{\"X")
            }
        }
    }
}
