package i18n

actual object I18n {
    private val messages: MutableMap<String, String> = HashMap()
    init {
        val messagesFileContent: String = I18n::class.java.classLoader.getResourceAsStream(getUsedTranslationFile())
            ?.readAllBytes()
            ?.let { String(it) }
            ?: throw RuntimeException("Couldn't read i18n file ${getUsedTranslationFile()}!")

        messagesFileContent.lines().map { line -> line.split(":").map { it.trim() } }.forEach { messages[it[0]] = it[1] }
    }

    actual fun get(key: String): String = messages[key] ?: ""

    actual fun getAll(): Map<String, String> = HashMap(messages)

    fun getUsedTranslationFile() = "i18n/messages-de.yml" // TODO: actually read
}