package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain


class InstanceSettingsTest : FunSpec() {

    @Suppress("unused")
    class UnannotatedGeolocationCoordinates(
        val latitude: Int,
        val longitude: Int,
        val speed: Int? // nullable field
    )

    @Suppress("unused")
    class NoNullAnnotatedGeolocationCoordinates(
        val latitude: Int,
        val longitude: Int,
        @Json(serializeNull = false) val speed: Int? // nullable field
    )

    @Suppress("unused")
    class NullAnnotatedGeolocationCoordinates(
        val latitude: Int,
        val longitude: Int,
        @Json(serializeNull = true) val speed: Int? // nullable field
    )

    private val unannotatedCoordinates = UnannotatedGeolocationCoordinates(1, 2, null)
    private val noNullCoordinates = NoNullAnnotatedGeolocationCoordinates(1, 2, null)
    private val nullCoordinates = NullAnnotatedGeolocationCoordinates(1, 2, null)


    init {
        test("Defaults & single-type settings") {
            val klaxon = Klaxon()
            val json = klaxon.toJsonString(unannotatedCoordinates)
            json shouldContain "null" // {"latitude" : 1, "longitude" : 2, "speed" : null}
        }

        test("no local settings, instance serializeNull = true -> null") {
            val klaxon = Klaxon(instanceSettings = KlaxonSettings(serializeNull = true))
            val json = klaxon.toJsonString(unannotatedCoordinates)
            json shouldContain "null" // {"latitude" : 1, "longitude" : 2, "speed" : null}
        }

        test("no local settings, instance serializeNull = false -> no null") {
            val klaxon = Klaxon(KlaxonSettings(serializeNull = false))
            val json = klaxon.toJsonString(unannotatedCoordinates)
            json shouldNotContain "null" // {"latitude" : 1, "longitude" : 2}
        }

        test("local serializeNull = false, no instance settings -> no null") {
            val klaxon = Klaxon()
            val json = klaxon.toJsonString(noNullCoordinates)
            json shouldNotContain "null" // {"latitude" : 1, "longitude" : 2}
        }

        test("local serializeNull = true, no instance settings -> null") {
            val klaxon = Klaxon()
            val json = klaxon.toJsonString(nullCoordinates)
            json shouldContain "null"// {"latitude" : 1, "longitude" : 2, "speed" : null}
        }

        //
        // Mixed tests

        test("local serializeNull = true, instance serializeNull = false -> null") {
            val klaxon = Klaxon(KlaxonSettings(serializeNull = false))
            val json = klaxon.toJsonString(nullCoordinates)
            json shouldContain "null" // {"latitude" : 1, "longitude" : 2, "speed" : null}
        }

        test("local serializeNull = false, instance serializeNull = true -> no null") {
            val klaxon = Klaxon(KlaxonSettings(serializeNull = true))
            val json = klaxon.toJsonString(noNullCoordinates)
            json shouldNotContain "null" // {"latitude" : 1, "longitude" : 2}
        }
    }
}
