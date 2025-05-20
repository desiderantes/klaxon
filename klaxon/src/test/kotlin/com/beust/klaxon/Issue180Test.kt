package com.beust.klaxon

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlin.reflect.KProperty


class Issue180Test : FunSpec({
    test("issue180") {
        val x = Klaxon().toJsonString(CharacterPlayer(12))
        val char = Klaxon().parse<CharacterPlayer>(x)
        char.shouldNotBeNull()
        char.id shouldBe 12
    }
})


class CharacterPlayer(val id: Int){

    var characters = listOf<CharacterPlayer>()

    @Json(ignored = true)
    val visibleDelegate = InvalidatableLazyImpl({
        characters.toMutableList()
    })
    @Json(ignored = true)
    val visibleChars: List<CharacterPlayer> by visibleDelegate

}

private object UNINITIALIZED_VALUE
class InvalidatableLazyImpl<T>(private val initializer: () -> T, lock: Any? = null) : Lazy<T> {
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    private val lock = lock ?: this
    fun invalidate(){
        _value = UNINITIALIZED_VALUE
    }

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    _v2 as T
                }
                else {
                    val typedValue = initializer()
                    _value = typedValue
                    typedValue
                }
            }
        }


    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    operator fun setValue(any: Any, property: KProperty<*>, t: T) {
        _value = t
    }
}