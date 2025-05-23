package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec


class Issue118Test : FunSpec( {
    xtest("Work in progress - Issue 118") {
        val originalJson= """{"foo" : {"x" : 1}, "y" : 1}"""
        val instance = Klaxon().parse<BarImpl>(originalJson)!! //Going from JSON to BarImpl
        val newJson = Klaxon().toJsonString(instance) //Going back from BarImpl to JSON
        println(newJson) //prints {"foo" : {"x" : 1}, "x" : 1, "y" : 1} instead of the original JSON
        try {
            /* Attempting to go back to BarImpl again, from our newly generated JSON.
             * The following line would succeed if newJson was equal to originalJson, but since they differ,
             * it fails with a NoSuchFieldException */
            Klaxon().parse<BarImpl>(newJson)!!
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}) {
    interface Foo{ val x: Int }
    data class FooImpl(override val x: Int): Foo
    data class BarImpl(val y: Int, private val foo: FooImpl): Foo { // by foo {
        @Json(ignored = true)
        override val x = foo.x
    }
}

