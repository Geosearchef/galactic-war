package toolbox

import java.security.SecureRandom
import java.util.*

object TokenFactory {
    val random = SecureRandom()

    fun generateRandomToken(): String {
        val data = ByteArray(32)
        random.nextBytes(data)
        return Base64.getUrlEncoder().encodeToString(data)
    }
}