package webserver

import config.ConfigService
import i18n.I18n
import org.apache.velocity.VelocityContext
import org.apache.velocity.app.Velocity
import spark.ModelAndView
import spark.template.velocity.VelocityTemplateEngine
import util.Util.logger
import java.io.StringWriter
import java.nio.file.Files

object TemplateService {
    val log = logger()

    val templateEngine = VelocityTemplateEngine()
    val templates: MutableMap<String, String> = HashMap()

    fun renderTemplate(templateName: String, model: Map<String, Any> = emptyMap()): String {
        return renderTemplateString(getTemplate(templateName), model)
    }

    fun renderTemplateString(templateString: String, model: Map<String, Any> = emptyMap()): String {
        val entireModel = getModelWithI18n(model)
        val context = VelocityContext()
        entireModel.entries.forEach { e -> context.put(e.key, e.value) }

        val writer = StringWriter()
        val success = Velocity.evaluate(context, writer, "tag", templateString)

        if(!success) {
            throw RuntimeException("Error encountered while trying to render velocity template")
        }

        return writer.toString()
    }

    fun getTemplate(name: String): String {
        synchronized(templates) {
            templates[name]?.let { return@getTemplate it }

            val templatePath = WebServerService.staticFilesLocation.resolve(name)
            log.debug("Loading template from ${templatePath.toAbsolutePath().toString()}")
            println("Static files location ${WebServerService.staticFilesLocation.toAbsolutePath().toString()}")
            val template = String(Files.readAllBytes(templatePath))

            if(ConfigService.config.webserver.staticFilesCaching) {
                templates[name] = template
            }

            return@getTemplate template
        }
    }

    fun renderWithPlainEngine(view: String, model: Map<String, Any> = emptyMap()): String { // pulls the template from the classpath!
        return templateEngine.render(ModelAndView(getModelWithI18n(model), view)) ?: "Error while rendering template"
    }

    fun getModelWithI18n(model: Map<String, Any> = emptyMap()): Map<String, Any> {
        val entireModel = HashMap(model)
        entireModel["i18n"] = I18n.getAll()
        return entireModel
    }
}