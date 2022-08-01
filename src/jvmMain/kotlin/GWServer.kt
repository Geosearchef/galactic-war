import config.ConfigService
import data.DatabaseService
import i18n.I18n
import model.Faction
import org.slf4j.LoggerFactory
import tasks.TaskService
import toolbox.CharacterNameGenerator
import util.Util
import webserver.WebServerService


fun main(args: Array<String>) {
    System.setProperty("org.slf4j.simpleLogger.logFile", "System.out")
    System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "debug");
    val log = LoggerFactory.getLogger("main")

    log.info("Starting Galactic War Backend...")

    TaskService.init()

    DatabaseService.init()
    WebServerService.init()

    log.info("Galactic War Backend fully started")
}