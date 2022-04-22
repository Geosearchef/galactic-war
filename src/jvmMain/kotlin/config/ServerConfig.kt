package config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration

data class ServerConfig(
    val webserver: WebServerConfig

)

data class WebServerConfig(
    @JsonProperty("port") val port: Int,
    @JsonProperty("static-files-path") val staticFiles: String,
    @JsonProperty("static-files-caching") val staticFilesCaching: Boolean,
    @JsonProperty("static-files-development-mode") val staticFilesDevelopmentMode: Boolean,
    @JsonProperty("websocket") val websocket: WebSocketConfig
)
data class WebSocketConfig(
    @JsonProperty("route") val route: String,
    @JsonProperty("idle-timeout-seconds") val idleTimeoutSeconds: Long
) {
    @JsonIgnore
    val idleTimeoutDuration = Duration.ofSeconds(idleTimeoutSeconds)
}