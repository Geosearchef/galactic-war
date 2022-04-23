package user

import config.ConfigService
import data.User
import data.Users
import org.jetbrains.exposed.sql.transactions.transaction
import tasks.TaskService.verifyTaskThread
import toolbox.TokenFactory
import util.Util.logger
import java.time.Duration
import java.time.Instant

object UserService {

    val log = logger()

    val tokens = ArrayList<UserToken>()

    fun authorizeUser(username: String, password: String): User? {
        verifyTaskThread()

        val user = transaction { User.find { Users.username eq username }.firstOrNull() }
        if(user == null) {
            log.info("User $username not found")
            return null
        }

        return user
    }

    fun createUserToken(user: User): String {
        verifyTaskThread()

        val token = UserToken(user, TokenFactory.generateRandomToken(), Instant.now(), Instant.now().plus(ConfigService.config.authTokenTimeoutDuration))
        tokens.add(token)

        tokens.removeIf { it.validUntil.isBefore(Instant.now()) }

        return token.tokenString
    }

    fun getUserByName(username: String): User? {
        verifyTaskThread()
        return transaction { User.find { Users.username eq username }.firstOrNull() }
    }
    fun getUserById(id: Int): User {
        verifyTaskThread()
        return transaction { User[id] }
    }
    fun getUserByFafId(fafId: Int): User? {
        verifyTaskThread()
        return transaction { User.find { Users.fafId eq fafId }.firstOrNull() }
    }

    data class UserToken(val username: User, val tokenString: String, val issuedAt: Instant, val validUntil: Instant)
}