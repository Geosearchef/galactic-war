package i18n

actual object I18n {
    actual fun get(key: String): String { // TODO: implement https://github.com/Geosearchef/cards/blob/master/src/jsMain/resources/i18n.js
        throw RuntimeException("I18n not yet implemented")
    }

    actual fun getAll(): Map<String, String> {
        throw RuntimeException("I18n not yet implemented")
    }
}