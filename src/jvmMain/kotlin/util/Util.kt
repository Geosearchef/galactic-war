package util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

actual object Util {
    fun isRunningFromJar() = Util::class.java.getResource("Util.class")?.toString()?.startsWith("jar") ?: false
    inline fun logger(): Logger = LoggerFactory.getLogger(Class.forName(Thread.currentThread().stackTrace[1].className))

    actual fun currentTimeMillis() = System.currentTimeMillis()
}