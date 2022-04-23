package config

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Duration

data class ServerConfig(
    val webserver: WebServerConfig,
    val database: DatabaseConfig,
    val game: GameConfig,
    @JsonProperty("auth-token-timeout-minutes") val authTokenTimeoutMinutes: Long
) {
    @JsonIgnore val authTokenTimeoutDuration = Duration.ofMinutes(authTokenTimeoutMinutes)
}

data class DatabaseConfig(
    val user: String,
    val password: String,
    val db: String,
    val host: String,
    val port: String,
    @JsonProperty("use-sqlite-instead") val useSqliteInstead: Boolean,
    @JsonProperty("sqlite-db-file") val sqliteDbFile: String
)

data class GameConfig(
    val character: CharacterConfig
)

data class CharacterConfig(
    @JsonProperty("name-selection-possibilities") val nameSelectionPossibilities: Int
)

data class WebServerConfig(
    @JsonProperty("port") val port: Int,
    @JsonProperty("default-route") val defaultRoute: String,
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