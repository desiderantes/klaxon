package com.beust.klaxon


import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KClass


class Issue229Test : FunSpec(){
    @TypeFor(field = "type", adapter = SettingValueAdapter::class)
    open class SettingValue(val type: String)
    data class MonitoringTime(val value: Int) : SettingValue("MonitoringTime")
    data class Threshold(val value: Double) : SettingValue("Threshold")

    class SettingValueAdapter : TypeAdapter<SettingValue> {
        override fun classFor(type: Any): KClass<out SettingValue> = when (type as String) {
            "MonitoringTime" -> MonitoringTime::class
            "Threshold" -> Threshold::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

    init {
        test("issue229") {
            val r = Klaxon().parse<SettingValue>(
                """
            {"type":"Threshold","value":0.4}
        """.trimIndent()
            )
            r.shouldNotBeNull()
            r.type shouldBe "Threshold"
            r.shouldBeInstanceOf<Threshold>()
            r.value shouldBe 0.4
        }
    }
}