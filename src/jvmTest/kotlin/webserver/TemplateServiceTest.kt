package webserver

import i18n.I18n
import kotlin.test.*

class TemplateServiceTest {

    //TODO: mock i18n

    @Test
    fun testRenderContext() {
        val templateString = "\${list[1]}\n\${list.size()}".trimIndent()
        val list = listOf(1, 4, 6)
        val result = TemplateService.renderTemplateString(templateString, mapOf("list" to list))

        assertEquals("4\n3", result)
    }

    @Test
    fun testRenderI18n() {
        val templateString = "\${i18n[\"username\"]}"
        val result = TemplateService.renderTemplateString(templateString)
        assertEquals("username", result)
    }

    @Test
    fun testRenderMath() {
        val templateString = "#set (\$sum = 10 + 20)\n\$sum"
        val result = TemplateService.renderTemplateString(templateString)
        assertEquals("30", result)
    }

    @Test
    fun testRenderLogin() {
        val result = TemplateService.renderTemplate("user/login.vm")

        assertContains(result, "GW Login")
        assertContains(result, "/gw.js")
        assertContains(result, "loading-indicator")

        assertFalse(result.contains("\${i18n[\"username\"]}")) // i18n string replaced
        assertContains(result, I18n.get("username"))
    }

}