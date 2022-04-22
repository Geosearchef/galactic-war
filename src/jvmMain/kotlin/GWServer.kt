import config.ConfigService
import i18n.I18n
import org.slf4j.LoggerFactory
import util.Util
import webserver.WebServerService


fun main(args: Array<String>) {
    System.setProperty("org.slf4j.simpleLogger.logFile", "System.out")
    val log = LoggerFactory.getLogger("main")

    log.info("Starting Galactic War Backend...")
    WebServerService.init()

    log.info("Galactic War Backend fully started")
}