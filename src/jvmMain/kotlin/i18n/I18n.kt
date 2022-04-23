package i18n

import util.Util.logger

actual object I18n {

    val log = logger()

    var usedTranslationFile = "messages.yml" // TODO: actually read from system language
        set(value) {
            field = value
            reloadMessages()
        }
    private val messages: MutableMap<String, String> = HashMap()

    init {
        reloadMessages()
    }

    private fun reloadMessages() {
        val resourceName = "i18n/$usedTranslationFile"
        val messagesFileContent: String = I18n::class.java.classLoader.getResourceAsStream(resourceName)
            ?.readAllBytes()
            ?.let { String(it) }
            ?: throw RuntimeException("Couldn't read i18n file $resourceName!")

        messages.clear()
        messagesFileContent.lines().map { line -> line.split(":").map { it.trim() } }.forEach { messages[it[0]] = it[1] }

        log.debug("Read ${messages.size} entries from $resourceName")
    }

    actual fun get(key: String): String = messages[key] ?: ""

    actual fun getAll(): Map<String, String> = HashMap(messages)
}