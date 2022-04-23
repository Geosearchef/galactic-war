package i18n

import util.ReflectionUtil
import kotlin.test.*

class I18nServiceTest {

    @Test
    fun testMessagesFileRead() {
        val value = I18n.get("this-value-should-not-exist-if-you-really-created-this-in-the-translation-you-are-doing-sth-wrong")

        val messages = ReflectionUtil.getFieldValue("messages", I18n)
        assertNotNull(messages, "Messages map is null")
        assertTrue((messages as HashMap<*, *>).isNotEmpty(), "No I18n messages loaded")
    }

    @Test
    fun testEntryNotPresent() {
        val value = I18n.get("this-value-should-not-exist-if-you-really-created-this-in-the-translation-you-are-doing-sth-wrong")
        assertEquals("", value, "Found entry for key that should not exist")
    }

    @Test
    fun testEntryPresent() {
        I18n.usedTranslationFile = "messages.yml"
        assertEquals("username", I18n.get("username"), "Couldn't find value for key")
    }

    @Test
    fun testReloadEntryPresent() {
        I18n.usedTranslationFile = "messages-de.yml"
        assertEquals("Benutzername", I18n.get("username"), "Incorrect value for key after reloading")
    }
}