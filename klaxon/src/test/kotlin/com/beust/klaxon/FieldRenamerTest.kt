package com.beust.klaxon

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain


class FieldRenamerTest : FunSpec(){


    val renamer = object: FieldRenamer {
        override fun toJson(fieldName: String) = FieldRenamer.camelToUnderscores(fieldName)
        override fun fromJson(fieldName: String) = FieldRenamer.underscoreToCamel(fieldName)
    }

    class C(val someField: Int)

    private fun privateRenaming(useRenamer: Boolean): C? {
        val json = """
           {
              "some_field": 42
           }
        """


        val klaxon = Klaxon()
        if (useRenamer) klaxon.fieldRenamer(renamer)
        return klaxon.parse<C>(json)
    }

    init {
        test("stringTest") {
            FieldRenamer.camelToUnderscores("abc").shouldBe("abc")
            FieldRenamer.camelToUnderscores("abcDef").shouldBe("abc_def")
            FieldRenamer.camelToUnderscores("abcDefGhi").shouldBe("abc_def_ghi")
        }
        test("withoutRenamerFromJson") {
            shouldThrow<KlaxonException> { privateRenaming(false) }

        }
        test("withRenamerFromJson") {
            val c = privateRenaming(true)
            c.shouldNotBeNull()
            c.someField.shouldNotBeNull()
            c.someField.shouldBe(42)
        }


        fun privateRenamingToJson(useRenamer: Boolean): String {
            val c = C(42)
            val klaxon = Klaxon()
            if (useRenamer) klaxon.fieldRenamer(renamer)
            return klaxon.toJsonString(c)
        }

        test("withoutRenamerToJson") {
            val c = privateRenamingToJson(false)
            c.shouldNotBeNull()
            c shouldContain "someField"
        }

        test("withRenamerToJson") {
            val c = privateRenamingToJson(true)
            c.shouldNotBeNull()
            c shouldContain "some_field"
        }
    }
}

