package user

import spark.Spark.*
import util.Util.logger
import webserver.WebController

object UserController : WebController("/user") {

    private val log = logger()

    override fun init() {

        get("/login") { req, _ ->
            render("user/login.vm", mapOf(
                "host" to req.host()
            ))
        }
    }

}