package com.beust.klaxon

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.style.FunSpec


data class MeasurementConditionDouble(val name: String, val min: Double, val max: Double)
data class MeasurementConditionFloat(val name: String, val min: Float, val max: Float)

val measurementConditionDouble = MeasurementConditionDouble("foo", 1.0, 2.0)
val measurementConditionFloat = MeasurementConditionFloat("foo", 1.0f, 2.0f)

class Issue198Test : FunSpec({
    test("issue198") {
        shouldNotThrow<KlaxonException> {
            Klaxon().toJsonString(measurementConditionDouble)
        }
        shouldNotThrow<KlaxonException> {
            Klaxon().toJsonString(measurementConditionFloat)
        }
    }
})
