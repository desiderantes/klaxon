package com.beust.klaxon

open class KlaxonException(message: String) : RuntimeException(message)
class JsonParsingException(message: String) : KlaxonException(message)