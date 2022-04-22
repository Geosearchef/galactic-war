package i18n

expect object I18n {
    fun get(key: String): String
    fun getAll(): Map<String, String>
}