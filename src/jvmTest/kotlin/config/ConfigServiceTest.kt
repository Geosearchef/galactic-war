package config

import kotlin.test.Test
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

class ConfigServiceTest {
    @Test fun testConfigLoaded() {
        assertNotNull(ConfigService.config)
        assertNotEquals(0, ConfigService.config.webserver.port)
    }
}