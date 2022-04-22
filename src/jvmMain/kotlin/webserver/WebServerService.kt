package webserver

import config.ConfigService
import spark.Spark
import util.Util
import websocket.WebSocketService
import java.nio.file.Path
import java.nio.file.Paths

object WebServerService {

    private val log = Util.logger()

    fun init() {
        val staticFilesLocation = getStaticFilesLocation()
        log.info("Serving static files from $staticFilesLocation")

        Spark.staticFiles.externalLocation(staticFilesLocation.toAbsolutePath().toString())
        Spark.staticFiles.expireTime(if(ConfigService.config.webserver.staticFilesCaching) 600 else 0)

        Spark.initExceptionHandler { log.error("Error while initializing webserver:", it) }
        Spark.port(ConfigService.config.webserver.port)

        WebSocketService.init()
        WebController.controllers.forEach {
            Spark.path(it.path) { // change scope
                it.init()
            }
            log.info("Controller registered for path ${it.path}")
        }

        Spark.init()
        log.info("WebServer started")
    }

    private fun getStaticFilesLocation(): Path {
        var staticFilesSubdirectory = ""
        if(Util.isRunningFromJar()) {
            staticFilesSubdirectory = ConfigService.config.webserver.staticFiles
        }
        else {
            staticFilesSubdirectory = "build/distributions"
        }

        return Paths.get(System.getProperty("user.dir")).resolve(staticFilesSubdirectory)
    }

}