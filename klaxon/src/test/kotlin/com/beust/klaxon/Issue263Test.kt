package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KClass

class Issue263Test : FunSpec(){
    data class GatewayMessage(
        @TypeFor(field = "data", adapter = GatewayPayloadTypeAdapter::class)
        @Json("op") val opCode: Int,
        @Json("t") val type: String?,
        @Json("d") val data: GatewayPayload
    )

    open class GatewayPayload

    data class ServerHello(
        @Json("heartbeat_interval") val heartbeatInterval: Long
    ): GatewayPayload()

    class GatewayPayloadTypeAdapter : TypeAdapter<GatewayPayload> {
        override fun classFor(type: Any): KClass<out GatewayPayload> = when(type as Int) {
            10 -> ServerHello::class
            else -> throw RuntimeException("No type for this opcode")
        }
    }

    init {
        test("issue263") {
            val input = """
            {
                "t": null,
                "op": 10,
                "d": {
                    "heartbeat_interval": 41250
                }
            }
            """.trimIndent()

            val parsed = Klaxon().parse<GatewayMessage>(input)!!
            parsed.data.shouldBeInstanceOf<ServerHello>()
        }
    }
}
