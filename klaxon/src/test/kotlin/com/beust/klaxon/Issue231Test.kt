package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.reflect.KClass


class Issue231Test : FunSpec() {
    @TypeFor(field = "type", adapter = SettingValueAdapter::class)
    open class SettingValue(val type: String)
    data class MonitoringTime(val value: Int) : SettingValue("MonitoringTime")
    data class Threshold(val value: Double) : SettingValue("Threshold")

    data class Data(val threshold: Threshold, val monitoringTime: MonitoringTime)

    class SettingValueAdapter : TypeAdapter<SettingValue> {
        override fun classFor(type: Any): KClass<out SettingValue> = when (type as String) {
            "MonitoringTime" -> MonitoringTime::class
            "Threshold" -> Threshold::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

    init {
        test("issue231") {
            val json = """
        {
            "threshold": { "type": "Threshold", "value":0.4 },
            "monitoringTime" : {"type": "MonitoringTime", "value":4}
        }"""

            val r = Klaxon().parse<Data>(json)
            r.shouldNotBeNull()
            r.threshold.value shouldBe 0.4
            r.monitoringTime.value shouldBe 4
        }
    }
}