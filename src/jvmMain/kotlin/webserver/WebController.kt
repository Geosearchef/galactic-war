package webserver

import HelloWorldController
import user.UserController

abstract class WebController(val path: String) {
    companion object {
        val controllers = listOf<WebController>(
            UserController,
            HelloWorldController
        )
    }

    abstract fun init()

    protected fun render(view: String, model: Map<String, Any> = emptyMap()): String {
        return TemplateService.renderTemplate(view, model)
    }
}