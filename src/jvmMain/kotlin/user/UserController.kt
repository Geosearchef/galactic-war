package user

import org.eclipse.jetty.http.HttpStatus
import spark.Response
import spark.Spark.*
import tasks.TaskService
import util.Util.logger
import webserver.WebController

object UserController : WebController("/user") {

    private val log = logger()

    override fun init() {

        get("/login") { req, _ ->
            render("user/login.vm")
        }

        post("/authorize") { req, res ->
            TaskService.addTask<Response> {
                val user = UserService.authorizeUser(req.queryParams("username"), req.queryParams("password"))
                if(user == null) {
                    res.status(HttpStatus.UNAUTHORIZED_401)
                    res.redirect("/user/login")
                    return@addTask res
                }

                val token = UserService.createUserToken(user)

                res.redirect("/game/main")
                return@addTask res
            }.get().let { return@post it }
        }
    }

}