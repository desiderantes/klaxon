package com.beust.klaxon

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe


/**
 * https://github.com/cbeust/klaxon/issues/125
 */
class Issue125Test : AnnotationSpec() {
    open class Parent(open val foo: String)
    class Child(@Json(ignored = false) override val foo: String, val bar: String) : Parent(foo)

    fun runTest() {
        val jsonString = """
        {
            "foo": "fofo" ,
            "bar": "baba"
        }
        """

        val parent = Klaxon().parse<Parent>(jsonString)
        parent.shouldNotBeNull()
        parent.foo shouldBe "fofo"
//        val child = Klaxon().parse<Child>(jsonString)
//        child.shouldNotBeNull()
//        child.foo shouldBe "fofo"
//        child.bar shouldBe "baba"
    }

    @Test
    @Ignore
    //"List of maps not supported yet"
    fun objectWithListOfMaps() {
        val mapper = Klaxon()
        data class Data(val data: List<Map<String, String>>)

        val data = Data(listOf(mapOf("name" to "john")))
        val json = mapper.toJsonString(data)
        mapper.parse<Data>(json) shouldBe data
    }
}
