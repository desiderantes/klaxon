package com.beust.klaxon

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.floats.shouldBeWithinPercentageOf
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

//
// Tests objects -> JSON string
//
class BindingTest : FunSpec({

    data class ArrayHolder(var listOfInts: List<Int> = emptyList(),
            var listOfStrings : List<String> = emptyList(),
            var listOfBooleans: List<Boolean> = emptyList(),
            var string: String = "foo", var isTrue: Boolean = true, var isFalse: Boolean = false)

  test("arrayToJson") {
        val klaxon = Klaxon()
        val h = ArrayHolder(listOf(1, 3, 5),
                listOf("d", "e", "f"),
                listOf(true, false, true))
        val s = klaxon.toJsonString(h)
        listOf("\"listOfInts\" : [1, 3, 5]",
                "\"listOfStrings\" : [\"d\", \"e\", \"f\"]",
                "\"listOfBooleans\" : [true, false, true]",
                "\"string\" : \"foo\"",
                "\"isTrue\" : true",
                "\"isFalse\" : false").forEach {
          s shouldContain it
        }
    }



  test("objectsToJson") {
        val deck1 = Deck1(cardCount = 1, card = Card(13, "Clubs"))
        val s = Klaxon()
          .converter(CardConverter)
                .toJsonString(deck1)
        listOf("\"CLUBS\"", "\"suit\"", "\"value\"", "13", "\"cardCount\"", "1").forEach {
          s shouldContain it
        }
    }

  test("doubleTest") {
    class C(val f: Float)
    val json = """ { "f": 1.23 } """
    val r = Klaxon().parse<C>(json)
    r.shouldNotBeNull()
    r.f.shouldNotBeNull()
    r.f.shouldBeWithinPercentageOf(1.23f, 1.0)
   }

    //
    // Tests parsing
    //

  data class AllTypes(
    var int: Int? = null,
    var string: String? = null,
    var isTrue: Boolean? = null,
    var isFalse: Boolean? = null,
    val balanceDouble: Double,
    var array: List<Int> = emptyList()
  )
  test("allTypes") {
        val expectedDouble = Double.MAX_VALUE - 1
        val result = Klaxon().parse<AllTypes>("""
        {
            "int": 42,
            "array": [11, 12],
            "string": "foo",
            "isTrue": true,
            "isFalse": false,
            "balanceDouble": $expectedDouble
        }
        """)
    result.shouldNotBeNull()
    result shouldBe  AllTypes(42, "foo", true, false, expectedDouble, listOf(11, 12))
    }

  test("compoundObject") {
        val jsonString = json {
            obj(
                "cardCount" to 2,
                "card" to obj(
                    "value" to 5,
                    "suit" to "Hearts"
                )
            )
        }.toJsonString()

        val result = Klaxon().parse<Deck1>(jsonString)
    result.shouldNotBeNull()
    result.cardCount shouldBe 2
    result.card.value shouldBe 5
    result.card.suit shouldBe "Hearts"

    }

  test("compoundObjectWithConverter") {
        val result = Klaxon()
          .converter(CardConverter)
                .parse<Deck1>("""
        {
          "cardCount": 2,
          "card":
            {"value" : 5,"suit" : "Hearts"}
        }
        """)

    result.shouldNotBeNull()
    result.cardCount shouldBe 2
    result.card.value shouldBe 5
    result.card.suit shouldBe "Hearts"
    }

    data class Deck2(
            var cards: List<Card> = emptyList(),
            var cardCount: Int? = null
    )

  test("compoundObjectWithArray") {
        val result = Klaxon()
                .parse<Deck2>("""
        {
          "cardCount": 2,
          "cards": [
            {"value" : 5, "suit" : "Hearts"},
            {"value" : 8, "suit" : "Spades"},
          ]
        }
    """)
    result.shouldNotBeNull()
    result.cardCount shouldBe 2
    result.cards shouldHaveSize 2
    result.cards[0] shouldBe Card(5, "Hearts")
    result.cards[1] shouldBe Card(8, "Spades")
    }

  test("compoundObjectWithObjectWithConverter") {
        val json = """{
            "preferences": [1,2,3],
            "properties":{"a":"b"}
            }"""

        data class Person(
                val preferences: List<Int>,
                val properties: Map<String, String> = sortedMapOf("a" to "b")
        )

        val p: Person = Klaxon().parse(json)!!
    p shouldBe Person(listOf(1, 2, 3), mapOf("a" to "b"))
    }

  test("compoundObjectWithArrayWithConverter") {
        val result = Klaxon()
          .converter(CardConverter)
                .parse<Deck2>("""
        {
          "cardCount": 2,
          "cards": [
            {"value" : 5, "suit" : "Hearts"},
            {"value" : 8, "suit" : "Spades"},
          ]
        }
    """)

    result.shouldNotBeNull()
    result.cardCount shouldBe 2
    result.cards shouldHaveSize 2
    result.cards[0] shouldBe Card(5, "Hearts")
    result.cards[1] shouldBe Card(8, "Spades")
    }

    class Mapping(
        @Json(name = "theName")
        val name: String
    )

  test("toJsonStringHonorsJsonAnnotation") {
        val s = Klaxon().toJsonString(Mapping("John"))
    s shouldContain "theName"
    }

  test("badFieldMapping") {
    shouldThrow<KlaxonException> {
      Klaxon().parse<Mapping>(
        """
        {
          "name": "foo"
        }
        """
      )
    }
    }

  test("goodFieldMapping") {
        val result = Klaxon().parse<Mapping>("""
        {
          "theName": "foo"
        }
        """)
    result.shouldNotBeNull()
    result.name shouldBe "foo"

    }


  test("enum") {
        val result = Klaxon().parse<Direction>("""
            { "cardinal": "NORTH" }
        """
        )
    result.shouldNotBeNull()
    result.cardinal.shouldNotBeNull()
    result.cardinal shouldBe  Cardinal.NORTH
    }

    class TestObj(var idShort: Long? = null, var idLong: Long? = null)

  test("longTest") {
        val expectedShort = 123 // Test widening Int -> Long property
        val expectedLong = 53147483640L
        val result = Klaxon().parse<TestObj>(""" {"idShort": $expectedShort, "idLong": $expectedLong } """)
    result.shouldNotBeNull()
    result.idShort shouldBe expectedShort
    result.idLong shouldBe expectedLong
    }

    class PersonWithDefaults(val age: Int, var name: String = "Foo")
  test("defaultParameters") {
        val result = Klaxon().parse<PersonWithDefaults>(json {
            obj(
                "age" to 23
            )
        }.toJsonString())!!
    result.shouldNotBeNull()
    result.age shouldBe 23
    result.name shouldBe "Foo"
  }



  test("sealedClass") {
        val result = Klaxon().parse<Dir.Left>("""{
            "n": 2
        }"""
        )!!
    result.shouldNotBeNull()
    result.n shouldBe 2
    }

  test("serializeMap") {
        val data = mapOf("firstName" to "John")
        val result = Klaxon().toJsonString(data)
    result.shouldNotBeNull()
    result shouldContain "firstName"
    result shouldContain "John"
    }


  test("generics") {
        class LongEntity(override val value: Long) : Entity<Long>

        val result = Klaxon().parse<LongEntity>("""{
            "value": 42
        }""")
    result.shouldNotBeNull()
    result.value shouldBe 42

    }

  test("set") {
        data class A(val data: Set<String>)
        val b = A(setOf("test"))
        val json = Klaxon().toJsonString(b)
        Klaxon().parse<A>(json)
    }

}) {


  data class Card(val value: Int, val suit: String)
  data class Deck1(val card: Card, val cardCount: Int)
  enum class Cardinal { NORTH, SOUTH }
  class Direction(var cardinal: Cardinal? = null)
  sealed class Dir(val name: String) {
    class Left(val n: Int) : Dir("Left")
  }

  object CardConverter : Converter {
    override fun canConvert(cls: Class<*>) = cls == Card::class.java

    override fun fromJson(jv: JsonValue) = Card(jv.objInt("value"), jv.objString("suit"))

    override fun toJson(v: Any) = (v as Card).let { value ->
      """
                    "value" : ${value.value},
                    "suit": "${value.suit.uppercase()}"
                """
    }
  }

  interface Entity<T> {
    val value: T
  }
}