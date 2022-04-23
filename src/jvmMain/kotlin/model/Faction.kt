package model

enum class Faction(val string: String, val id: Int) {
    UEF("uef", 0), AEON("aeon", 1), CYBRAN("cybran", 2), SERAPHIM("seraphim", 3); // TODO: fix ids

    companion object {
        fun fromId(id: Int) = values().find { it.id == id }
        fun fromString(string: String) = values().find { it.string == string }
    }
}