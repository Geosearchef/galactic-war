package config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory

object ConfigService {

    val mapper = ObjectMapper(YAMLFactory()).apply {
        findAndRegisterModules()
    }

    // can't use javaClass to obtain resource
    val config: ServerConfig = mapper.readValue(ConfigService::class.java.classLoader.getResourceAsStream("config.yaml"), ServerConfig::class.java)

}