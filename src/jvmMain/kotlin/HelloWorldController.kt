import webserver.WebController
import spark.Spark.*
import util.Util

object HelloWorldController: WebController("/hello-world") {

    val log = Util.logger()

    override fun init() {
        before("/*") { _, _ ->
            log.info("Request for hello world controller")
            // ensureAuthorizedOrHalt()
        }

        after("/*") { _, _ ->
            log.info("After hello world controller")
        }



        get("/something") { _, _ ->
            "Hello there!"
        }
    }
}