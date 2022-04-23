package webserver

import config.ConfigService
import i18n.I18n
import org.apache.velocity.Template
import org.apache.velocity.runtime.RuntimeSingleton
import spark.ModelAndView
import spark.Spark
import spark.template.velocity.VelocityTemplateEngine
import util.Util
import websocket.WebSocketService
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

object WebServerService {

    private val log = Util.logger()

    val staticFilesLocation: Path = obtainStaticFilesLocation()

    fun init() {

        log.info("Serving static files from $staticFilesLocation")
        verifyStaticFilesLocation(staticFilesLocation)

        Spark.staticFiles.externalLocation(staticFilesLocation.toAbsolutePath().toString())
        Spark.staticFiles.expireTime(if(ConfigService.config.webserver.staticFilesCaching) 600 else 0)

        Spark.initExceptionHandler { log.error("Error while initializing webserver:", it) }
        Spark.port(ConfigService.config.webserver.port)

        WebSocketService.init()

        Spark.get("/") { _, res ->
            res.redirect(ConfigService.config.webserver.defaultRoute)
        }

        WebController.controllers.forEach {
            Spark.path(it.path) { // change scope
                it.init()
            }
            log.info("Controller registered for path ${it.path}")
        }

        Spark.init()
        log.info("WebServer started")
    }

    private fun obtainStaticFilesLocation(): Path {
        var staticFilesSubdirectory = ""
        if(Util.isRunningFromJar()) {
            staticFilesSubdirectory = ConfigService.config.webserver.staticFiles
        }
        else {
            staticFilesSubdirectory = "build/distributions"
        }

        return Paths.get(System.getProperty("user.dir")).resolve(staticFilesSubdirectory)
    }

    private fun verifyStaticFilesLocation(staticFilesLocation: Path) {
        val gwScriptPath = staticFilesLocation.resolve("gw.js")
        val messagesFilePath = staticFilesLocation.resolve("i18n").resolve("messages.yml")

        if(gwScriptPath.exists()) {
            log.info("Frontend script found, static directory is set correctly (${gwScriptPath.toAbsolutePath()})")
        } else {
            throw RuntimeException("Couldn't find frontend script")
        }
        if(messagesFilePath.exists()) {
            log.info("I18n static file found, static directory is set correctly (${messagesFilePath.toAbsolutePath()})")
        } else {
            throw RuntimeException("Couldn't find static file")
        }
    }

}