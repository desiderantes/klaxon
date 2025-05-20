package com.beust.klaxon

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KClass


class TypeAdapterTest : AnnotationSpec() {
    open class Shape
    data class Rectangle(val width: Int, val height: Int): Shape()
    data class Circle(val radius: Int): Shape()

    class ShapeTypeAdapter: TypeAdapter<Shape> {
        override fun classFor(type: Any): KClass<out Shape> = when(type as Int) {
            1 -> Rectangle::class
            2 -> Circle::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

    val json = """
            [
                { "type": 1, "shape": { "width": 100, "height": 50 } },
                { "type": 2, "shape": { "radius": 20} }
            ]
        """

    fun typeAdapterTest() {
        class Data (
                @TypeFor(field = "shape", adapter = ShapeTypeAdapter::class)
                val type: Integer,

                val shape: Shape
        )

        val shapes = Klaxon().parseArray<Data>(json)
        shapes.shouldNotBeNull()
        val rect = shapes[0].shape
        rect.shouldBeInstanceOf<Rectangle>()
        rect shouldBe Rectangle(100, 50)
        val circ = shapes[1].shape
        circ.shouldBeInstanceOf<Circle>()
        circ shouldBe Circle(20)
    }

    @Test(expected = KlaxonException::class)
    fun typoInFieldName() {
        class BogusData (
            @TypeFor(field = "___nonexistentField", adapter = ShapeTypeAdapter::class)
            val type: Integer,

            val shape: Shape
        )
        val shapes = Klaxon().parseArray<BogusData>(json)
    }

    class BogusShapeTypeAdapter: TypeAdapter<Shape> {
        override fun classFor(type: Any): KClass<out Shape> = when(type as Int) {
            1 -> Rectangle::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }
    @Test
    fun unknownDiscriminantValue() {
        class BogusData (
                @TypeFor(field = "shape", adapter = BogusShapeTypeAdapter::class)
                val type: Integer,

                val shape: Shape
        )

        val exception = shouldThrowExactly<IllegalArgumentException> {
          Klaxon().parseArray<BogusData>(json)
        }
        exception shouldHaveMessage ".*Unknown type.*".toRegex()
    }

    class AnimalTypeAdapter: TypeAdapter<Animal> {
        override fun classFor(type: Any): KClass<out Animal> = when(type as String) {
            "dog" -> Dog::class
            "cat" -> Cat::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }
    }

    @TypeFor(field = "name", adapter = AnimalTypeAdapter::class)
    open class Animal {
        var name: String = ""
    }
    class Cat: Animal()
    class Dog: Animal()

    fun embedded() {
        val json = """
            [
                { "name": "dog" },
                { "name": "cat" }
            ]
        """
        val r = Klaxon().parseArray<Animal>(json)
        r.shouldNotBeNull()
        r[0].shouldBeInstanceOf<Dog>()
        r[1].shouldBeInstanceOf<Cat>()
    }


    @TypeFor(field = "type", adapter = VehicleTypeAdapter::class)
    open class Vehicle(open val type: String)
    data class Car(override val type: String = "car") : Vehicle(type)
    data class Truck(override val type: String = "truck") : Vehicle(type)

    class VehicleTypeAdapter : TypeAdapter<Vehicle> {

        override fun classFor(type: Any): KClass<out Vehicle> {
            TODO("Not used - classForNullable replaces this")
        }

        override fun classForNullable(type: Any?): KClass<out Vehicle> = when (type) {
            null -> Car::class
            "car" -> Car::class
            "truck" -> Truck::class
            else -> throw IllegalArgumentException("Unknown type: $type")
        }

    }

    @Test
    fun should_default_to_car() {
        val json = """
            [
                { "type": "car" },
                { "type": "truck" }
                { "no_type": "should default to car..." }
            ]
        """
        val r = Klaxon().parseArray<Vehicle>(json)
        r.shouldNotBeNull()
        println(r)
        r[0].shouldBeInstanceOf<Car>()
        r[1].shouldBeInstanceOf<Truck>()
        r[2].shouldBeInstanceOf<Car>()
    }

}
