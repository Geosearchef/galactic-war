import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class Message() {
    companion object {
        fun fromJson(json: String) = Json.decodeFromString<Message>(json)
    }
    fun toJson() = Json.encodeToString(this)
}

@Serializable @SerialName("clEchoRep")
data class ClientEchoReplyMessage(val serverTimestamp: Long) : Message()

@Serializable @SerialName("seEchoReq")
data class ServerEchoRequestMessage(val serverTimestamp: Long, val lastRTT: Int) : Message()

//@Serializable
//data class ClientLoginMessage(val username: String, val code: String) : Message()
//
//@Serializable
//data class ServerLoginMessage( val serverTimestamp: Long, val admin: Boolean) : Message()
