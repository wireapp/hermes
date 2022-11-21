package com.wire.monitoring

import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Map that is able to transform itself to [Marker].
 */
@Suppress("UndocumentedPublicFunction", "unused")
class MarkerMap(
    private val markersMap: MutableMap<String, String> = mutableMapOf(INSTANCE_ID to INSTANCE)
) {

    companion object {
        private const val KEY_VALUE_SEPARATOR = ":"
        private const val SEPARATOR = ";"

        /**
         * Creates [MarkerMap] from the given string.
         */
        fun fromString(value: String): MarkerMap {
            val keyValueMap = value.split(SEPARATOR).mapNotNull { keyValue ->
                keyValue.split(KEY_VALUE_SEPARATOR)
                    .takeIf { it.size == 2 }
                    ?.let { it[0] to it[1] }
            }.toMap()

            return MarkerMap(keyValueMap.toMutableMap())
        }
    }

    /**
     * Get raw map.
     */
    fun getMap(): Map<String, String> = this.markersMap.toMap()

    /**
     * Adds key:value to the map.
     */
    fun add(key: String, value: String?): MarkerMap = this.also { if (value != null) markersMap[key] = value }

    fun addRequestId(value: String?) = add(REQUEST_ID, value)
    fun addRemoteHost(value: String?) = add(REMOTE_HOST, value)
    fun addPath(value: String?) = add(PATH, value)
    fun addInstanceId(value: String?) = add(INSTANCE_ID, value)
    fun addSessionId(value: String?) = add(SESSION_ID, value)

    /**
     * Creates new [Marker] from this instance.
     */
    fun toMarker(): Marker = MarkerFactory.getMarker(this.toString())

    override fun toString() =
        markersMap.map { (key, value) -> "$key$KEY_VALUE_SEPARATOR$value" }.joinToString(SEPARATOR)
}
