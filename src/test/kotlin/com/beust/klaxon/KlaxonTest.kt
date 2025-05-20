package com.beust.klaxon

import com.beust.klaxon.jackson.jackson
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.intellij.lang.annotations.Language
import java.math.BigDecimal
import java.util.Collections.emptyMap
import java.util.regex.Pattern
import kotlin.test.*

class ParserTest : FunSpec({

  context("parset tests") {

    listOf("default" to Parser.default(), "jackson" to Parser.jackson()).forEach { (parserName, parser) ->

      fun read(name: String): Any? {
        val cls = ParserTest::class.java
        return parser.parse(cls.getResourceAsStream(name)!!)
      }


      test("$parserName: generated") {
        @Suppress("UNCHECKED_CAST")
        val j = read("/generated.json") as JsonArray<JsonObject>
        assertEquals((j[0]["name"] as JsonObject)["last"], "Olson")
      }

      test("$parserName: simple") {
        val j = read("/b.json")
        val expected = json {
          array(1, "abc", false)
        }
        assertEquals(expected, j)
      }

      test("$parserName: basic") {
        val j = read("/a.json")
        val expected = json {
          obj(
            "a" to "b", "c" to array(1, "abc", false), "e" to obj("f" to 30, "g" to 31)
          )
        }
        assertEquals(expected, j)
      }

      test("$parserName: nulls parse") {
        assertEquals(json {
          array(
            1, null, obj(
              "a" to 1, "." to null
            )
          )
        }, read("/nulls.json"))
      }

      test("$parserName: nulls DSL") {
        val j = json {
          obj(
            "1" to null, "2" to 2
          )
        }

        assertEquals("""{"1":null,"2":2}""", j.toJsonString())
      }

      test("$parserName: pretty print object") {
        val j = json {
          obj(
            "a" to 1, "b" to "text"
          )
        }

        val expected = """{
  "a": 1,
  "b": "text"
}"""
        val actual = j.toJsonString(true).trim()
        assertEquals(expected, actual)
      }

      test("$parserName: pretty print empty object") {
        assertEquals("{}", JsonObject(emptyMap()).toJsonString(true))
      }

      test("$parserName: pretty print array") {
        assertEquals("[1, 2, 3]", JsonArray(1, 2, 3).toJsonString(true))
      }

      test("$parserName: pretty print empty array") {
        assertEquals("[]", JsonArray<Any>().toJsonString(true))
      }

      test("$parserName: pretty print nested objects") {
        val expected = """{
  "a": 1,
  "obj": {
    "b": 2
  }
}"""

        val actual = json {
          obj(
            "a" to 1, "obj" to obj("b" to 2)
          )
        }.toJsonString(true).trim()

        assertEquals(expected, actual)
      }

      test("$parserName: canonical json object") {
        val j = json {
          obj(
            "c" to 1, "a" to 2, "b" to obj(
              "e" to 1, "d" to 2
            )
          )
        }.toJsonString(canonical = true)

        val expected = """{"a":2,"b":{"d":2,"e":1},"c":1}"""

        assertEquals(j, expected)
      }

      test("$parserName: canonical json number") {
        val j = json {
          obj(
            "d" to 123456789.123456789, "f" to 123456789.123456789f
          )
        }.toJsonString(canonical = true)

        assert(Pattern.matches("\\{(\"[a-z]+\":\\d\\.\\d+E\\d+(,|}\$))+", j))
      }

      test("$parserName: render string escapes") {
        assertEquals(""" "test\"it\n" """.trim(), valueToString("test\"it\n"))
      }

      test("$parserName: parse string escapes") {
        val s = "text field \"s\"\nnext line\u000cform feed\ttab\\rev solidus/solidus\bbackspace\u2018"
        assertEquals(json {
          obj(s to s)
        }, read("/escaped.json"))
      }
    }

    test("issue91WithQuotedDoubleStrings") {
        val map = HashMap<String, String>()
        map["whoops"] = """ Hello "world" """
        val s = Klaxon().toJsonString(map)
      s shouldContain "\\\"world\\\""
    }

    test("arrayLookup") {
        val j = json {
          array(
            obj(
              "nick" to "SuperMan",
              "address" to obj("country" to "US"),
              "weight" to 89.4,
              "d" to 1L,
              "i" to 4,
              "b" to java.math.BigInteger("123456789123456789123456786")
            ), obj(
              "nick" to "BlackOwl",
              "address" to obj("country" to "UK"),
              "weight" to 75.7,
              "d" to 2L,
              "i" to 3,
              "b" to java.math.BigInteger("123456789123456789123456787")
            ), obj(
              "nick" to "Anonymous",
              "address" to null,
              "weight" to -1.0,
              "d" to 3L,
              "i" to 2,
              "b" to java.math.BigInteger("123456789123456789123456788")
            ), obj(
              "nick" to "Rocket",
              "address" to obj("country" to "Russia"),
              "weight" to 72.0,
              "d" to 4L,
              "i" to 1,
              "b" to java.math.BigInteger("123456789123456789123456789")
            )
          )
        }

      assertEquals(
        listOf("SuperMan", "BlackOwl", "Anonymous", "Rocket"), j.string("nick").filterNotNull()
      )
      assertEquals(
        listOf("US", "UK", null, "Russia"), j.obj("address").map { it?.string("country") })
        assertKlaxonEquals(JsonArray(89.4, 75.7, -1.0, 72.0), j.double("weight"))
        assertKlaxonEquals(JsonArray(1L, 2L, 3L, 4L), j.long("d"))
        assertKlaxonEquals(JsonArray(4, 3, 2, 1), j.int("i"))
      assertKlaxonEquals(
        JsonArray(
          java.math.BigInteger("123456789123456789123456786"),
          java.math.BigInteger("123456789123456789123456787"),
          java.math.BigInteger("123456789123456789123456788"),
          java.math.BigInteger("123456789123456789123456789")
        ), j.bigInt("b")
      )
    }



    test("objectLookup") {
        val j = json {
          obj(
            "nick" to "BlackOwl", "address" to obj("country" to "UK"), "weight" to 75.7, "d" to 1L, "true" to true
          )
        }

        assertEquals("BlackOwl", j.string("nick"))
        assertEquals(JsonObject(mapOf("country" to "UK")), j.obj("address"))
        assertEquals(75.7, j.double("weight"))
        assertEquals(1L, j.long("d"))
        assertTrue(j.boolean("true")!!)
    }

    test("arrayFiltering") {
        val j = json {
          array(1, 2, 3, obj("a" to 1L))
        }

        assertEquals(listOf(1L), j.filterIsInstance<JsonObject>().map { it.long("a") })
    }

    test("lookupObjects") {
        val j = json {
          obj(
            "users" to array(
              obj(
                "name" to "Sergey", "weight" to 65.0
              ), obj(
                "name" to "Bombshell", "weight" to 121.0
              ), null
            )
          )
        }

        assertEquals(JsonArray("Sergey", "Bombshell", null), j.lookup<String?>("/users/name"))
        assertEquals(JsonArray("Sergey", "Bombshell", null), j.lookup<String?>("users.name"))
    }

    test("lookupArray") {
        val j = json {
          array(
            "yo", obj("a" to 1)
          )
        }

        assertEquals(JsonArray(null, 1), j.lookup<Int?>("a"))
    }

    test("lookupNestedArrays") {
        val j = json {
          array(
            array(
              array(
                "yo", obj("a" to 1)
              )
            )
          )
        }

        assertEquals(JsonArray(null, 1), j.lookup<Int?>("a"))
    }

    test("lookupSingleObject") {
        val j = json {
          obj("a" to 1)
        }

        assertEquals(1, j.lookup<Int?>("a").single())
    }

    test("mapChildren") {
        val j = json {
          array(1, 2, 3)
        }

        val result = j.mapChildrenObjectsOnly { fail("should never reach here") }
        assertTrue(result.isEmpty())
    }

    test("mapChildrenWithNulls") {
        val j = json {
          array(1, 2, 3)
        }

        val result = j.mapChildren { fail("should never reach here") }
        assertKlaxonEquals(listOf(null, null, null), result)
    }



    test("renderMap") {
        val map = mapOf(
          "a" to 1, "b" to "x", "c" to null
        )

        assertEquals(valueToString(map), "{\"a\":1,\"b\":\"x\",\"c\":null}")
    }

    test("renderList") {
        val list = listOf(null, 1, true, false, "a")

        assertEquals(valueToString(list), "[null,1,true,false,\"a\"]")
    }


    data class StockEntry(
      val date: String, val close: Double, val volume: Int, val open: Double, val high: Double, val low: Double
    )

    fun issue77() {
        val json = """
            [
          {
            "date": "2018/01/10",
            "close": 0.25,
            "volume": 500000,
            "open": 0.5,
            "high": 0.5,
            "low": 0.25
          }
        ]
        """
        Klaxon().parseArray<StockEntry>(json)
    }



    test("array parse") {
        data class Child(val id: Int, val name: String)
        data class Parent(val children: Array<Child>)

        val array = """{
            "children":[
                {"id": 1, "name": "foo"},
                {"id": 2, "name": "bar"}
            ]
         }"""

        val r = Klaxon().parse<Parent>(array)
      assertNotNull(r)
      r.children[0] shouldBe Child(1, "foo")
      r.children[1] shouldBe Child(2, "bar")
    }

    test("nestedCollections") {
      data class Root(val lists: List<List<String>>)

      val result = Klaxon().parse<Root>(
        """
        {
            "lists": [["red", "green", "blue"]]
        }
        """
      )
      assertEquals(Root(listOf(listOf("red", "green", "blue"))), result)
    }

    test("nested") {
      val r = Klaxon().parse<PersonWitCity>(
        """
          {
            "name": "John",
            "city": {
                "name": "San Francisco"
          }
        }""")
      assertNotNull(r)
      r.name shouldBe "John"
      r.city.name shouldBe "San Francisco"
    }

    test("bigDecimal") {
        data class A(val data: BigDecimal)

      val something = Klaxon().parse<A>(
        """
            {"data": 0.00000001}
            """
      )

      assertEquals(0, BigDecimal(0.00000001).compareTo(BigDecimal(something!!.data.toDouble())))
    }



    fun serializeEnum() {
        val klaxon = Klaxon()
      assertEquals(klaxon.toJsonString(Colour.Red), "\"Red\"")
      assertEquals(klaxon.parse<ColourHolder>("{\"colour\": \"Green\"}"), ColourHolder(Colour.Green))
    }



    test("serialize Enum with renames") {
        val klaxon = Klaxon()
      assertEquals(klaxon.toJsonString(ShortColour.Red), "\"R\"")
      assertEquals(
        klaxon.parse<ShortColourHolder>("{\"colour\": \"G\"}"), ShortColourHolder(ShortColour.Green)
      )
    }

    class Vendor {
      var vendorName: String = ""
    }

    @Language("json") val someString = """{
        "name": "example",
        "foo": "cool",
        "boo": "stuff",
        "vendor": [
          { "vendorName": "example"}
        ]
    }"""

    class Registry(
      val name: String, val vendor: List<Vendor> = ArrayList()
    ) {
      var foo: String = ""
      var boo: String = ""
    }

    test("non-constructor Properties") {
      val result = Klaxon().parse<Registry>(someString)
      val vendors = result?.vendor!!
      result.name shouldBe "example"
      vendors[0].vendorName shouldBe "example"
    }




    test("parse Registry") {
      val result = Klaxon().parse<Registry>(someString)
      assertNotNull(result)
      val vendors = result.vendor
      assertNotNull(vendors)
      result.name shouldBe "example"
      result.foo shouldBe "cool"
      result.boo shouldBe "stuff"
      vendors[0].vendorName shouldBe "example"
    }



    test("issue 153") {
        abstract class FooBase(
          val id: String? = null
        )

        class BarImpl(
          val barValue: String? = null
        ) : FooBase()


      val barImpl = Klaxon().parse<BarImpl>(
        """
          {
            "id": "id123",
            "barValue" : "value123"
          }
        """
      )

      assertNotNull(barImpl)
      barImpl.barValue shouldBe "value123"
      barImpl.id shouldBe "id123"
    }

    test("toJsonObject Test") {
        data class Employee(val name: String, val age: Int)

      val jo = Klaxon().toJsonObject(Employee("Joe", 24))

      jo["age"] shouldBe "24"
      jo["name"] shouldBe "\"Joe\""
    }
  }

}) {
  companion object {

    enum class Colour { Red, Green, Blue }
    data class ColourHolder(val colour: Colour)
    enum class ShortColour {
      @Json("R")
      Red,

      @Json("G")
      Green,

      @Json("B")
      Blue
    }

    data class ShortColourHolder(val colour: ShortColour)

    class PersonWitCity(val name: String, val city: City) {
      class City(val name: String)
    }

    fun <T> assertKlaxonEquals(expected: List<T>, actual: JsonArray<T>) {
      for (i in 0..expected.size - 1) {
        assertEquals(expected[i], actual[i])
      }
    }

    fun valueToString(v: Any?, prettyPrint: Boolean = false, canonical: Boolean = false): String =
      StringBuilder().apply {
        Render.renderValue(v, this, prettyPrint, canonical, 0)
      }.toString()
  }
}
