@file:Suppress("unused")

package com.beust.klaxon

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.maps.shouldHaveKey
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

data class ConferenceDataModel(
    val events: Map<String, EventModel>,
    val rooms: Map<String, RoomModel>,
    val sections: Map<String, SectionModel>,
    val speakers: Map<String, SpeakerModel>,
    val tracks: Map<String, TrackModel>
)

data class EventModel(
    val name: String,
    val description: String,
    val duration: String,
    val isGeneralEvent: Boolean,
    val isPublished: Boolean,
    val startTime: String, //Date,
    val roomIds: Map<String, Boolean>? = emptyMap(),
    val speakerIds: Map<String, Boolean>? = emptyMap(),
    val trackId: String? = null,
    var roomNames: Map<String, Boolean>? = emptyMap(),
    var speakerNames: Map<String, Boolean>? = emptyMap(),
    var trackName: String? = null,
    val updatedAt: Long,
    val updatedBy: String
)

data class RoomModel(
    val name: String,
    val updatedAt: Long,
    val updatedBy: String
)

data class SectionModel(
    val name: String,
    val title: String,
    val startTime: String,//Date,
    val endTime: String,//Date,
    val updatedAt: Long,
    val updatedBy: String
)

data class SpeakerModel(
    val name: String,
    val title: String? = null,
    val org: String? = null,
    val bio: String,
    val pictureId: String? = null,
    val pictureUrl: String? = null,
    val isFeatured: Boolean,
    val socialProfiles: Map<String, String>? = emptyMap(),
    val updatedAt: Long,
    val updatedBy: String
)

data class TrackModel(
    val name: String,
    val description: String,
    val sortOrder: Int,
    val updatedAt: Long,
    val updatedBy: String
)

data class Node(val nodeName: String)
class Root(val nodes: Map<String, Node>)


class MapTest : AnnotationSpec() {
    @Test
    @Ignore
    fun hashMap() {
        val r = Klaxon().parse<HashMap<String, Node>>(
            """{
                "key1": { "nodeName": "node1" },
                "key2": { "nodeName": "node2" }
            }"""
        )
        r.shouldNotBeNull()

        r shouldHaveSize 2
        r shouldHaveKey "key1"
        r shouldHaveKey "key2"
        r["key1"] shouldBe Node("node1")
        r["key2"] shouldBe Node("node2")
    }

    class Model(val events: Map<String, Node>)

    fun modelWithHashMap() {
        val r = Klaxon()
            .parse<Model>(
                """{
                "events": {
                    "key1": { "nodeName": "node1" },
                    "key2": { "nodeName": "node2" }
                }
            }"""
            )
        r.shouldNotBeNull()
        r.events shouldHaveKey "key1"
        r.events["key1"] shouldBe Node("node1")
    }

    @Ignore
    @Test
    //TODO: "Need to move data.json to a better place"
    fun bigFile() {
        val ins =
            MapTest::class.java.getResourceAsStream("/data.json")
                ?: throw IllegalArgumentException("Couldn't find data.json")
        val r = Klaxon().parse<ConferenceDataModel>(ins)
        r.shouldNotBeNull()
        r.events shouldHaveSize 5
        r.events shouldHaveKey "-L3daccTVLOcYi9hVHsD"
        r.events["-L3daccTVLOcYi9hVHsD"]?.name shouldBe "Registration & Breakfast"
    }
}

class Employee(val firstName: String, val lastName: String, val age: Int) {
    val lazyValue: String by lazy {
        val result = ""
        result
    }
}



