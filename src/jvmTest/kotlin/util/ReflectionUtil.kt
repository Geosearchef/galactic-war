package util

import util.Util.logger
import java.lang.IllegalArgumentException
import java.lang.reflect.Field

object ReflectionUtil {
    val log = logger()

    fun getFieldValue(fieldName: String, obj: Any): Any {
        try {
            return getAccessibleField(fieldName, obj).get(obj)
        } catch(e: IllegalArgumentException) {
            throw RuntimeException(e)
        } catch(e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    fun getAccessibleField(fieldName: String, obj: Any): Field {
        try {
            // doesn't find fields higher up in the hierarchy
            return obj.javaClass.getDeclaredField(fieldName).apply {
                isAccessible = true
            }
        } catch(e: NoSuchFieldException) {
            log.error("Couldn't find field to read", e)
            throw RuntimeException(e)
        } catch(e: SecurityException) {
            throw RuntimeException(e)
        }
    }
}