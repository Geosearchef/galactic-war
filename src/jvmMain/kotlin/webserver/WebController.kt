package webserver

import HelloWorldController
import i18n.I18n
import spark.ModelAndView
import spark.template.velocity.VelocityTemplateEngine
import user.UserController

abstract class WebController(val path: String) {
    companion object {
        val controllers = listOf<WebController>(
            UserController,
            HelloWorldController
        )
        val templateEngine = VelocityTemplateEngine()
    }

    abstract fun init()

    protected fun render(view: String, model: Map<String, Any>): String {
        val entireModel = HashMap(model)
        entireModel["i18n"] = I18n.getAll()

        return templateEngine.render(ModelAndView(entireModel, view)) ?: "Error while rendering template"
    }
}