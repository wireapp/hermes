package com.wire.monitoring

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.IThrowableProxy
import ch.qos.logback.classic.spi.ThrowableProxyUtil
import ch.qos.logback.core.CoreConstants
import ch.qos.logback.core.LayoutBase
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * Layout logging into jsons.
 */
class JsonLoggingLayout : LayoutBase<ILoggingEvent>() {

    private companion object {
        val dateTimeFormatter: DateTimeFormatter =
            DateTimeFormatter.ISO_DATE_TIME
                .withZone(ZoneOffset.UTC)

        val mapper = jacksonObjectMapper()
    }

    override fun doLayout(event: ILoggingEvent): String {
        val finalMap: MutableMap<String, Any> = mutableMapOf(
            "timestamp" to formatTime(event),
            "message" to event.formattedMessage,
            "logger" to event.loggerName
                .takeLastWhile { it != '.' } // take only names without packages
                .replace("\$Companion", ""), // delete static name from the logger name
            "level" to (event.level.levelStr ?: event.level.levelInt),
        )
        // add markers
        event.markerList
            ?.mapNotNull { it.name }
            ?.map { MarkerMap.fromString(it) }
            ?.forEach { finalMap.putAll(it.getMap()) }
        // include all MDCs
        event.mdcPropertyMap.forEach { (key, entry) -> finalMap[key] = entry }
        // if this was an exception, include necessary data
        if (event.throwableProxy != null) {
            finalMap["exception"] = exception(event.throwableProxy)
        }

        return mapper.writeValueAsString(finalMap) + CoreConstants.LINE_SEPARATOR
    }

    private fun exception(proxy: IThrowableProxy) = mapOf(
        "message" to proxy.message,
        "class" to proxy.className,
        "stacktrace" to ThrowableProxyUtil.asString(proxy)
    )

    private fun formatTime(event: ILoggingEvent): String =
        dateTimeFormatter.format(Instant.ofEpochMilli(event.timeStamp))
}
